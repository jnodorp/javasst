package javasst.bytecode;

/**
 * Test case for recursive calls.
 */
public class FibonacciTest extends BytecodeTestBase {

    @Override
    protected String getJavaFile() {
        return "javasst/bytecode/Fibonacci.java";
    }

    @Override
    protected String getJavaSstFile() {
        return "javasst/bytecode/Fibonacci.sst";
    }

    @Override
    protected String getClassName() {
        return "Fibonacci";
    }
}
