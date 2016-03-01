package javasst.bytecode;

/**
 * Test case for assignments.
 */
public class AssignmentTest extends BytecodeTestBase {

    @Override
    protected String getJavaFile() {
        return "javasst/bytecode/AssignTest.java";
    }

    @Override
    protected String getJavaSstFile() {
        return "javasst/bytecode/AssignTest.sst";
    }

    @Override
    protected String getClassName() {
        return "AssignTest";
    }
}
