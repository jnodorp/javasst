package javasst.bytecode;

/**
 * Test case for booleans.
 */
public class BooleanTest extends BytecodeTestBase {

    @Override
    protected String getJavaFile() {
        return "javasst/bytecode/BooleanTest.java";
    }

    @Override
    protected String getJavaSstFile() {
        return "javasst/bytecode/BooleanTest.sst";
    }

    @Override
    protected String getClassName() {
        return "BooleanTest";
    }
}
