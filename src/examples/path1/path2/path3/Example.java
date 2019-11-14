package path1.path2.path3;

public class Example {

    public Example() {

    }

    public void method1() {
        int a = 1;
    }

    public int method2(int a, int b) {
        return a + b;
    }

    private int method3() {
        return 1;
    }

    public static void main(String[] args) {
        Example example1 = new Example();
        example1.method1();
        int c = example1.method2(1, 2);
        example1.method3();
    }
}
