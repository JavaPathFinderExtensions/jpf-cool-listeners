package gov.nasa.jpf.listener;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.bytecode.INVOKESPECIAL;
import gov.nasa.jpf.jvm.bytecode.JVMInvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.VirtualInvocation;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.*;

import java.io.PrintWriter;

public class PrefixCallMonitor extends ListenerAdapter {

    private String prefix;
    static final String INDENT = "  ";

    MethodInfo lastMi;
    PrintWriter out;

    public PrefixCallMonitor(Config conf, JPF jpf) {
        out = new PrintWriter(System.out, true);
        prefix = conf.getString("jpf.prefix_call_monitor.prefix", "");
    }
//
//    @Override
//    public void methodEntered (VM vm, ThreadInfo currentThread, MethodInfo enteredMethod) {
//        String enteredFullName = enteredMethod.getFullName();
//        if (enteredFullName.toLowerCase().startsWith(prefix.toLowerCase())) {
//            System.out.println("----------------------------------------------------------------------");
//            System.out.println("Entered method: " + enteredFullName);
//            System.out.println("Matched with monitored prefix: " + prefix);
//            System.out.println("Stack trace name: " + enteredMethod.getStackTraceName());
//            System.out.println("Argument numbers: " + enteredMethod.getArgumentsSize());
//            System.out.println("Signature: " + enteredMethod.getSignature());
//        }
//    }

    void logMethodCall(ThreadInfo ti, MethodInfo mi, int stackDepth) {
        out.print(ti.getId());
        out.print(":");

        for (int i=0; i<stackDepth%80; i++) {
            out.print(INDENT);
        }

        if (mi.isMJI()) {
            out.print("native ");
        }

        out.print(mi.getFullName());

        if (ti.isFirstStepInsn()) {
            out.print("...");
        }

        out.println();
    }

    @Override
    public void executeInstruction (VM vm, ThreadInfo ti, Instruction insnToExecute) {
        MethodInfo mi = insnToExecute.getMethodInfo();

        if (mi != lastMi && mi.getFullName().toLowerCase().startsWith(prefix.toLowerCase())) {
            logMethodCall(ti, mi, ti.getStackDepth());
            lastMi = mi;

        } else if (insnToExecute instanceof JVMInvokeInstruction) {
            MethodInfo callee;

            // that's the only little gist of it - if this is a VirtualInvocation,
            // we have to dig the callee out by ourselves (it's not known
            // before execution)

            if (insnToExecute instanceof VirtualInvocation) {
                VirtualInvocation callInsn = (VirtualInvocation)insnToExecute;
                int objref = callInsn.getCalleeThis(ti);
                if (objref != MJIEnv.NULL){
                    callee = callInsn.getInvokedMethod(ti, objref);
                } else {
                    return; // this is causing a NPE, so don't report it as a unknown callee
                }

            } else if (insnToExecute instanceof INVOKESPECIAL) {
                INVOKESPECIAL callInsn = (INVOKESPECIAL)insnToExecute;
                callee = callInsn.getInvokedMethod(ti);

            } else {
                JVMInvokeInstruction callInsn = (JVMInvokeInstruction)insnToExecute;
                callee = callInsn.getInvokedMethod(ti);
            }

            if (callee != null) {
                if (callee.isMJI()) {
                    logMethodCall(ti, callee, ti.getStackDepth()+1);
                }
            } else {
                out.println("ERROR: unknown callee of: " + insnToExecute);
            }
        }
    }

    /*
     * those are not really required, but mark the transition boundaries
     */
    @Override
    public void stateRestored(Search search) {
        int id = search.getStateId();
        out.println("----------------------------------- [" +
                search.getDepth() + "] restored: " + id);
    }

    //--- the ones we are interested in
    @Override
    public void searchStarted(Search search) {
        out.println("----------------------------------- search started");
    }

    @Override
    public void stateAdvanced(Search search) {
        int id = search.getStateId();

        out.print("----------------------------------- [" +
                search.getDepth() + "] forward: " + id);
        if (search.isNewState()) {
            out.print(" new");
        } else {
            out.print(" visited");
        }

        if (search.isEndState()) {
            out.print(" end");
        }

        out.println();

        lastMi = null;
    }

    @Override
    public void stateBacktracked(Search search) {
        int id = search.getStateId();

        lastMi = null;

        out.println("----------------------------------- [" +
                search.getDepth() + "] backtrack: " + id);
    }

    @Override
    public void searchFinished(Search search) {
        out.println("----------------------------------- search finished");
    }

}
