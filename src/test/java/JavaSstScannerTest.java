import javasst.JavaSstScanner;
import javasst.JavaSstToken;
import javasst.JavaSstTokenType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import scanner.Input;

import java.io.File;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link JavaSstScanner}.
 */
public class JavaSstScannerTest {

    /**
     * The test file to use.
     */
    private static final String FILE = "src" + File.separator + "test" + File.separator + "resources" + File
            .separator + "test.sst";

    /**
     * The scanner under test.
     */
    private Iterator<JavaSstToken> scanner;

    @Before
    public void setUp() throws Exception {
        final Input input = new Input(FILE);
        scanner = new JavaSstScanner(input);
    }

    @Test
    public void testNext() throws Exception {
        JavaSstToken symbol;

        // class class NiceClassName6 {
        symbol = scanner.next();
        Assert.assertEquals(JavaSstTokenType.CLASS, symbol.getType());
        assertEquals("class", symbol.getIdentifier());
        assertEquals(FILE, symbol.getFile().toString());
        assertEquals(4, symbol.getLine());
        assertEquals(1, symbol.getColumn());

        symbol = scanner.next();
        assertEquals(JavaSstTokenType.IDENT, symbol.getType());
        assertEquals("NiceClassName6", symbol.getIdentifier());
        assertEquals(FILE, symbol.getFile().toString());
        assertEquals(4, symbol.getLine());
        assertEquals(8, symbol.getColumn()); // FIXME: Should be 7.

        symbol = scanner.next();
        assertEquals(JavaSstTokenType.CURLY_BRACE_OPEN, symbol.getType());
        assertEquals("{", symbol.getIdentifier());
        assertEquals(FILE, symbol.getFile().toString());
        assertEquals(4, symbol.getLine());
        assertEquals(23, symbol.getColumn()); // FIXME: Should be 22.

        // final int const1 = 123;
        symbol = scanner.next();
        assertEquals(JavaSstTokenType.FINAL, symbol.getType());
        assertEquals("final", symbol.getIdentifier());
        assertEquals(FILE, symbol.getFile().toString());
        assertEquals(5, symbol.getLine());
        assertEquals(5, symbol.getColumn());

        symbol = scanner.next();
        assertEquals(JavaSstTokenType.INT, symbol.getType());
        assertEquals("int", symbol.getIdentifier());
        assertEquals(FILE, symbol.getFile().toString());
        assertEquals(5, symbol.getLine());
        assertEquals(11, symbol.getColumn());

        symbol = scanner.next();
        assertEquals(JavaSstTokenType.IDENT, symbol.getType());
        assertEquals("const1", symbol.getIdentifier());
        assertEquals(FILE, symbol.getFile().toString());
        assertEquals(5, symbol.getLine());
        assertEquals(15, symbol.getColumn());

        symbol = scanner.next();
        assertEquals(JavaSstTokenType.EQUALS, symbol.getType());
        assertEquals("=", symbol.getIdentifier());
        assertEquals(FILE, symbol.getFile().toString());
        assertEquals(5, symbol.getLine());
        assertEquals(22, symbol.getColumn());

        symbol = scanner.next();
        assertEquals(JavaSstTokenType.NUMBER, symbol.getType());
        assertEquals("123", symbol.getIdentifier());
        assertEquals(FILE, symbol.getFile().toString());
        assertEquals(5, symbol.getLine());
        assertEquals(24, symbol.getColumn());

        symbol = scanner.next();
        assertEquals(JavaSstTokenType.SEMICOLON, symbol.getType());
        assertEquals(";", symbol.getIdentifier());
        assertEquals(FILE, symbol.getFile().toString());
        assertEquals(5, symbol.getLine());
        assertEquals(27, symbol.getColumn());

        // final int const2 = const1 * const1;
        assertEquals(JavaSstTokenType.FINAL, scanner.next().getType());
        assertEquals(JavaSstTokenType.INT, scanner.next().getType());
        assertEquals(JavaSstTokenType.IDENT, scanner.next().getType());
        assertEquals(JavaSstTokenType.EQUALS, scanner.next().getType());
        assertEquals(JavaSstTokenType.IDENT, scanner.next().getType());
        assertEquals(JavaSstTokenType.TIMES, scanner.next().getType());
        assertEquals(JavaSstTokenType.IDENT, scanner.next().getType());
        assertEquals(JavaSstTokenType.SEMICOLON, scanner.next().getType());

        // int var1;
        assertEquals(JavaSstTokenType.INT, scanner.next().getType());
        assertEquals(JavaSstTokenType.IDENT, scanner.next().getType());
        assertEquals(JavaSstTokenType.SEMICOLON, scanner.next().getType());

        // public void setVar1(int x) {
        assertEquals(JavaSstTokenType.PUBLIC, scanner.next().getType());
        assertEquals(JavaSstTokenType.VOID, scanner.next().getType());
        assertEquals(JavaSstTokenType.IDENT, scanner.next().getType());
        assertEquals(JavaSstTokenType.PARENTHESIS_OPEN, scanner.next().getType());
        assertEquals(JavaSstTokenType.INT, scanner.next().getType());
        assertEquals(JavaSstTokenType.IDENT, scanner.next().getType());
        assertEquals(JavaSstTokenType.PARENTHESIS_CLOSE, scanner.next().getType());
        assertEquals(JavaSstTokenType.CURLY_BRACE_OPEN, scanner.next().getType());

        // var1 = x;
        assertEquals(JavaSstTokenType.IDENT, scanner.next().getType());
        assertEquals(JavaSstTokenType.EQUALS, scanner.next().getType());
        assertEquals(JavaSstTokenType.IDENT, scanner.next().getType());
        assertEquals(JavaSstTokenType.SEMICOLON, scanner.next().getType());

        // }
        assertEquals(JavaSstTokenType.CURLY_BRACE_CLOSE, scanner.next().getType());

        // public int getVar1() {
        assertEquals(JavaSstTokenType.PUBLIC, scanner.next().getType());
        assertEquals(JavaSstTokenType.INT, scanner.next().getType());
        assertEquals(JavaSstTokenType.IDENT, scanner.next().getType());
        assertEquals(JavaSstTokenType.PARENTHESIS_OPEN, scanner.next().getType());
        assertEquals(JavaSstTokenType.PARENTHESIS_CLOSE, scanner.next().getType());
        assertEquals(JavaSstTokenType.CURLY_BRACE_OPEN, scanner.next().getType());

        // return var1;
        assertEquals(JavaSstTokenType.RETURN, scanner.next().getType());
        assertEquals(JavaSstTokenType.IDENT, scanner.next().getType());
        assertEquals(JavaSstTokenType.SEMICOLON, scanner.next().getType());

        // }
        assertEquals(JavaSstTokenType.CURLY_BRACE_CLOSE, scanner.next().getType());

        // public void incrementVar1() {
        assertEquals(JavaSstTokenType.PUBLIC, scanner.next().getType());
        assertEquals(JavaSstTokenType.VOID, scanner.next().getType());
        assertEquals(JavaSstTokenType.IDENT, scanner.next().getType());
        assertEquals(JavaSstTokenType.PARENTHESIS_OPEN, scanner.next().getType());
        assertEquals(JavaSstTokenType.PARENTHESIS_CLOSE, scanner.next().getType());
        assertEquals(JavaSstTokenType.CURLY_BRACE_OPEN, scanner.next().getType());

        // var1 = getVar1() + 1;
        assertEquals(JavaSstTokenType.IDENT, scanner.next().getType());
        assertEquals(JavaSstTokenType.EQUALS, scanner.next().getType());
        assertEquals(JavaSstTokenType.IDENT, scanner.next().getType());
        assertEquals(JavaSstTokenType.PARENTHESIS_OPEN, scanner.next().getType());
        assertEquals(JavaSstTokenType.PARENTHESIS_CLOSE, scanner.next().getType());
        assertEquals(JavaSstTokenType.PLUS, scanner.next().getType());
        assertEquals(JavaSstTokenType.NUMBER, scanner.next().getType());
        assertEquals(JavaSstTokenType.SEMICOLON, scanner.next().getType());

        // }
        assertEquals(JavaSstTokenType.CURLY_BRACE_CLOSE, scanner.next().getType());

        // }
        assertEquals(JavaSstTokenType.CURLY_BRACE_CLOSE, scanner.next().getType());
    }
}