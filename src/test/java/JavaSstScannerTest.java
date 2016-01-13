import javasst.scanner.JavaSstScanner;
import javasst.scanner.JavaSstToken;
import javasst.scanner.JavaSstTokenType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import scanner.Input;
import scanner.Scanner;

import java.io.File;

import static javasst.scanner.JavaSstTokenType.*;
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
    private Scanner<JavaSstToken, JavaSstTokenType> scanner;

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
        Assert.assertEquals(CLASS, symbol.getType());
        assertEquals("class", symbol.getIdentifier());
        assertEquals(FILE, symbol.getFile().get().toString());
        assertEquals(4, symbol.getLine());
        assertEquals(1, symbol.getColumn());

        symbol = scanner.next();
        assertEquals(IDENT, symbol.getType());
        assertEquals("className6", symbol.getIdentifier());
        assertEquals(FILE, symbol.getFile().get().toString());
        assertEquals(4, symbol.getLine());
        assertEquals(7, symbol.getColumn());

        symbol = scanner.next();
        assertEquals(CURLY_BRACE_OPEN, symbol.getType());
        assertEquals("{", symbol.getIdentifier());
        assertEquals(FILE, symbol.getFile().get().toString());
        assertEquals(4, symbol.getLine());
        assertEquals(18, symbol.getColumn());

        // final int const1 = 123;
        symbol = scanner.next();
        assertEquals(FINAL, symbol.getType());
        assertEquals("final", symbol.getIdentifier());
        assertEquals(FILE, symbol.getFile().get().toString());
        assertEquals(5, symbol.getLine());
        assertEquals(5, symbol.getColumn());

        symbol = scanner.next();
        assertEquals(INT, symbol.getType());
        assertEquals("int", symbol.getIdentifier());
        assertEquals(FILE, symbol.getFile().get().toString());
        assertEquals(5, symbol.getLine());
        assertEquals(11, symbol.getColumn());

        symbol = scanner.next();
        assertEquals(IDENT, symbol.getType());
        assertEquals("intConst1", symbol.getIdentifier());
        assertEquals(FILE, symbol.getFile().get().toString());
        assertEquals(5, symbol.getLine());
        assertEquals(15, symbol.getColumn());

        symbol = scanner.next();
        assertEquals(EQUALS, symbol.getType());
        assertEquals("=", symbol.getIdentifier());
        assertEquals(FILE, symbol.getFile().get().toString());
        assertEquals(5, symbol.getLine());
        assertEquals(25, symbol.getColumn());

        symbol = scanner.next();
        assertEquals(NUMBER, symbol.getType());
        assertEquals("123", symbol.getIdentifier());
        assertEquals(FILE, symbol.getFile().get().toString());
        assertEquals(5, symbol.getLine());
        assertEquals(27, symbol.getColumn());

        symbol = scanner.next();
        assertEquals(SEMICOLON, symbol.getType());
        assertEquals(";", symbol.getIdentifier());
        assertEquals(FILE, symbol.getFile().get().toString());
        assertEquals(5, symbol.getLine());
        assertEquals(30, symbol.getColumn());

        // int var1;
        assertEquals(INT, scanner.next().getType());
        assertEquals(IDENT, scanner.next().getType());
        assertEquals(SEMICOLON, scanner.next().getType());

        // public int getVar1() {
        assertEquals(PUBLIC, scanner.next().getType());
        assertEquals(INT, scanner.next().getType());
        assertEquals(IDENT, scanner.next().getType());
        assertEquals(PARENTHESIS_OPEN, scanner.next().getType());
        assertEquals(PARENTHESIS_CLOSE, scanner.next().getType());
        assertEquals(CURLY_BRACE_OPEN, scanner.next().getType());

        // return var1;
        assertEquals(RETURN, scanner.next().getType());
        assertEquals(IDENT, scanner.next().getType());
        assertEquals(SEMICOLON, scanner.next().getType());

        // }
        assertEquals(CURLY_BRACE_CLOSE, scanner.next().getType());

        // public void setIntVar1(int x) {
        assertEquals(PUBLIC, scanner.next().getType());
        assertEquals(VOID, scanner.next().getType());
        assertEquals(IDENT, scanner.next().getType());
        assertEquals(PARENTHESIS_OPEN, scanner.next().getType());
        assertEquals(INT, scanner.next().getType());
        assertEquals(IDENT, scanner.next().getType());
        assertEquals(PARENTHESIS_CLOSE, scanner.next().getType());
        assertEquals(CURLY_BRACE_OPEN, scanner.next().getType());

        // var1 = x;
        assertEquals(IDENT, scanner.next().getType());
        assertEquals(EQUALS, scanner.next().getType());
        assertEquals(IDENT, scanner.next().getType());
        assertEquals(SEMICOLON, scanner.next().getType());

        // }
        assertEquals(CURLY_BRACE_CLOSE, scanner.next().getType());

        // public void incrementVar1() {
        assertEquals(PUBLIC, scanner.next().getType());
        assertEquals(VOID, scanner.next().getType());
        assertEquals(IDENT, scanner.next().getType());
        assertEquals(PARENTHESIS_OPEN, scanner.next().getType());
        assertEquals(PARENTHESIS_CLOSE, scanner.next().getType());
        assertEquals(CURLY_BRACE_OPEN, scanner.next().getType());

        // setIntVar1(add(getIntVar1(), 1));
        assertEquals(IDENT, scanner.next().getType());
        assertEquals(PARENTHESIS_OPEN, scanner.next().getType());
        assertEquals(IDENT, scanner.next().getType());
        assertEquals(PARENTHESIS_OPEN, scanner.next().getType());
        assertEquals(IDENT, scanner.next().getType());
        assertEquals(PARENTHESIS_OPEN, scanner.next().getType());
        assertEquals(PARENTHESIS_CLOSE, scanner.next().getType());
        assertEquals(COMMA, scanner.next().getType());
        assertEquals(NUMBER, scanner.next().getType());
        assertEquals(PARENTHESIS_CLOSE, scanner.next().getType());
        assertEquals(PARENTHESIS_CLOSE, scanner.next().getType());
        assertEquals(SEMICOLON, scanner.next().getType());

        // }
        assertEquals(CURLY_BRACE_CLOSE, scanner.next().getType());

        // TODO: Finish test.
    }
}