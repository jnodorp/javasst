package javasst.bytecode;

/**
 * Test case for assignments.
 */
public class ExpressionTest extends BytecodeTestBase {

    @Override
    protected String getJavaFile() {
        return "javasst/bytecode/ExpressionTest.java";
    }

    @Override
    protected String getJavaSstFile() {
        return "javasst/bytecode/ExpressionTest.sst";
    }

    @Override
    protected String getClassName() {
        return "ExpressionTest";
    }
}
