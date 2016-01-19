package javasst;

import javasst.scanner.JavaSstScanner;
import javasst.scanner.JavaSstToken;
import org.junit.Test;
import scanner.Input;
import scanner.Scanner;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

/**
 * Some integration tests.
 */
public class IntegrationTests {

    /**
     * Test class.
     *
     * @throws FileNotFoundException Thrown, if the test file is not available.
     */
    @Test
    public void testClass() throws FileNotFoundException {
        final Input input = new Input("src/test/resources/javasst/integration/class_test.sst");
        final Scanner<JavaSstToken, JavaSstType> scanner = new JavaSstScanner(input);

        // class
        JavaSstToken token = scanner.next();
        assertEquals("class", token.getIdentifier());
        assertEquals(JavaSstType.CLASS, token.getType());

        // className1
        token = scanner.next();
        assertEquals("className1", token.getIdentifier());
        assertEquals(JavaSstType.IDENT, token.getType());

        // {
        token = scanner.next();
        assertEquals("{", token.getIdentifier());
        assertEquals(JavaSstType.CURLY_BRACE_OPEN, token.getType());

        // }
        token = scanner.next();
        assertEquals("}", token.getIdentifier());
        assertEquals(JavaSstType.CURLY_BRACE_CLOSE, token.getType());
    }

    /**
     * Test classbody.
     *
     * @throws FileNotFoundException Thrown, if the test file is not available.
     */
    @Test
    public void testClassbody() throws FileNotFoundException {
        final Input input = new Input("src/test/resources/javasst/integration/classbody_test.sst");
        final Scanner<JavaSstToken, JavaSstType> scanner = new JavaSstScanner(input);

        // class
        JavaSstToken token = scanner.next();
        assertEquals("class", token.getIdentifier());
        assertEquals(JavaSstType.CLASS, token.getType());

        // className1
        token = scanner.next();
        assertEquals("className1", token.getIdentifier());
        assertEquals(JavaSstType.IDENT, token.getType());

        // {
        token = scanner.next();
        assertEquals("{", token.getIdentifier());
        assertEquals(JavaSstType.CURLY_BRACE_OPEN, token.getType());

        // final
        token = scanner.next();
        assertEquals("final", token.getIdentifier());
        assertEquals(JavaSstType.FINAL, token.getType());

        // int
        token = scanner.next();
        assertEquals("int", token.getIdentifier());
        assertEquals(JavaSstType.INT, token.getType());

        // intConst1
        token = scanner.next();
        assertEquals("intConst1", token.getIdentifier());
        assertEquals(JavaSstType.IDENT, token.getType());

        // =
        token = scanner.next();
        assertEquals("=", token.getIdentifier());
        assertEquals(JavaSstType.EQUALS, token.getType());

        // 123
        token = scanner.next();
        assertEquals("123", token.getIdentifier());
        assertEquals(JavaSstType.NUMBER, token.getType());

        // ;
        token = scanner.next();
        assertEquals(";", token.getIdentifier());
        assertEquals(JavaSstType.SEMICOLON, token.getType());

        // final
        token = scanner.next();
        assertEquals("final", token.getIdentifier());
        assertEquals(JavaSstType.FINAL, token.getType());

        // int
        token = scanner.next();
        assertEquals("int", token.getIdentifier());
        assertEquals(JavaSstType.INT, token.getType());

        // intConst2
        token = scanner.next();
        assertEquals("intConst2", token.getIdentifier());
        assertEquals(JavaSstType.IDENT, token.getType());

        // =
        token = scanner.next();
        assertEquals("=", token.getIdentifier());
        assertEquals(JavaSstType.EQUALS, token.getType());

        // 456
        token = scanner.next();
        assertEquals("456", token.getIdentifier());
        assertEquals(JavaSstType.NUMBER, token.getType());

        // ;
        token = scanner.next();
        assertEquals(";", token.getIdentifier());
        assertEquals(JavaSstType.SEMICOLON, token.getType());

        // final
        token = scanner.next();
        assertEquals("final", token.getIdentifier());
        assertEquals(JavaSstType.FINAL, token.getType());

        // int
        token = scanner.next();
        assertEquals("int", token.getIdentifier());
        assertEquals(JavaSstType.INT, token.getType());

        // intConst3
        token = scanner.next();
        assertEquals("intConst3", token.getIdentifier());
        assertEquals(JavaSstType.IDENT, token.getType());

        // =
        token = scanner.next();
        assertEquals("=", token.getIdentifier());
        assertEquals(JavaSstType.EQUALS, token.getType());

        // 789
        token = scanner.next();
        assertEquals("789", token.getIdentifier());
        assertEquals(JavaSstType.NUMBER, token.getType());

        // ;
        token = scanner.next();
        assertEquals(";", token.getIdentifier());
        assertEquals(JavaSstType.SEMICOLON, token.getType());

        // int
        token = scanner.next();
        assertEquals("int", token.getIdentifier());
        assertEquals(JavaSstType.INT, token.getType());

        // intVar1
        token = scanner.next();
        assertEquals("intVar1", token.getIdentifier());
        assertEquals(JavaSstType.IDENT, token.getType());

        // ;
        token = scanner.next();
        assertEquals(";", token.getIdentifier());
        assertEquals(JavaSstType.SEMICOLON, token.getType());

        // int
        token = scanner.next();
        assertEquals("int", token.getIdentifier());
        assertEquals(JavaSstType.INT, token.getType());

        // intVar2
        token = scanner.next();
        assertEquals("intVar2", token.getIdentifier());
        assertEquals(JavaSstType.IDENT, token.getType());

        // ;
        token = scanner.next();
        assertEquals(";", token.getIdentifier());
        assertEquals(JavaSstType.SEMICOLON, token.getType());

        // int
        token = scanner.next();
        assertEquals("int", token.getIdentifier());
        assertEquals(JavaSstType.INT, token.getType());

        // intVar3
        token = scanner.next();
        assertEquals("intVar3", token.getIdentifier());
        assertEquals(JavaSstType.IDENT, token.getType());

        // ;
        token = scanner.next();
        assertEquals(";", token.getIdentifier());
        assertEquals(JavaSstType.SEMICOLON, token.getType());

        // }
        token = scanner.next();
        assertEquals("}", token.getIdentifier());
        assertEquals(JavaSstType.CURLY_BRACE_CLOSE, token.getType());
    }

    /**
     * Test function declaration.
     *
     * @throws FileNotFoundException Thrown, if the test file is not available.
     */
    @Test
    public void testFunctionDeclaration() throws FileNotFoundException {
        final Input input = new Input("src/test/resources/javasst/integration/function_declaration_test.sst");
        final Scanner<JavaSstToken, JavaSstType> scanner = new JavaSstScanner(input);

        while (input.hasNext()) {
            scanner.next();
        }
    }
}
