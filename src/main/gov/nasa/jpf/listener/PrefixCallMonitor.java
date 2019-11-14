package gov.nasa.jpf.listener;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.*;

public class PrefixCallMonitor extends ListenerAdapter {

    private String prefix;

    public PrefixCallMonitor(Config conf, JPF jpf) {
        prefix = conf.getString("jpf.prefix_call_monitor.prefix", "");
    }

    @Override
    public void methodEntered (VM vm, ThreadInfo currentThread, MethodInfo enteredMethod) {
        String enteredFullName = enteredMethod.getFullName();
        if (enteredFullName.toLowerCase().startsWith(prefix.toLowerCase())) {
            System.out.println("Entered method: " + enteredFullName);
            System.out.println("Matched with monitored prefix: " + prefix);
            System.out.println("Stack trace name: " + enteredMethod.getStackTraceName());
            System.out.println("Argument numbers: " + enteredMethod.getArgumentsSize());
            System.out.println("Signature: " + enteredMethod.getSignature());
            System.out.println("-----------------------------------");
        }

    }
}
