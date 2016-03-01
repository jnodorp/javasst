package javasst.bytecode;

/**
 * Test case for boolean assignments.
 */
public class BooleanAssignmentTest extends BytecodeTestBase {

    @Override
    protected String getJavaFile() {
        return "javasst/bytecode/BooleanAssignmentTest.java";
    }

    @Override
    protected String getJavaSstFile() {
        return "javasst/bytecode/BooleanAssignmentTest.sst";
    }

    @Override
    protected String getClassName() {
        return "BooleanTest";
    }
}
