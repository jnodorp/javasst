package javasst.bytecode;

/**
 * Test case for assignments.
 */
public class ATest extends BytecodeTestBase {

    @Override
    protected String getJavaFile() {
        return "javasst/bytecode/A.java";
    }

    @Override
    protected String getJavaSstFile() {
        return "javasst/bytecode/A.sst";
    }

    @Override
    protected String getClassName() {
        return "A";
    }
}
