package javasst.bytecode;

/**
 * Test case for assignments.
 */
public class TestTest extends BytecodeTestBase {

    @Override
    protected String getJavaFile() {
        return "javasst/bytecode/Test.java";
    }

    @Override
    protected String getJavaSstFile() {
        return "javasst/bytecode/Test.sst";
    }

    @Override
    protected String getClassName() {
        return "Test";
    }
}
