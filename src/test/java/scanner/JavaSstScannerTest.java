package scanner;

import org.junit.Before;
import org.junit.Test;

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
    private Iterator<Symbol> scanner;

    @Before
    public void setUp() throws Exception {
        final Input input = new Input(FILE);
        scanner = new JavaSstScanner(input);
    }

    @Test
    public void testNext() throws Exception {
        Symbol symbol;

        // class class NiceClassName6 {
        symbol = scanner.next();
        assertEquals(SymbolType.CLASS, symbol.getType());
        assertEquals("class", symbol.getIdentifier());
        assertEquals(FILE, symbol.getPosition().getFile().toString());
        assertEquals(4, symbol.getPosition().getLine());
        assertEquals(1, symbol.getPosition().getColumn());

        symbol = scanner.next();
        assertEquals(SymbolType.IDENT, symbol.getType());
        assertEquals("NiceClassName6", symbol.getIdentifier());
        assertEquals(FILE, symbol.getPosition().getFile().toString());
        assertEquals(4, symbol.getPosition().getLine());
        assertEquals(8, symbol.getPosition().getColumn()); // FIXME: Should be 7.

        symbol = scanner.next();
        assertEquals(SymbolType.CURLY_BRACE_OPEN, symbol.getType());
        assertEquals("{", symbol.getIdentifier());
        assertEquals(FILE, symbol.getPosition().getFile().toString());
        assertEquals(4, symbol.getPosition().getLine());
        assertEquals(23, symbol.getPosition().getColumn()); // FIXME: Should be 22.

        // final int const1 = 123;
        symbol = scanner.next();
        assertEquals(SymbolType.FINAL, symbol.getType());
        assertEquals("final", symbol.getIdentifier());
        assertEquals(FILE, symbol.getPosition().getFile().toString());
        assertEquals(5, symbol.getPosition().getLine());
        assertEquals(5, symbol.getPosition().getColumn());

        symbol = scanner.next();
        assertEquals(SymbolType.INT, symbol.getType());
        assertEquals("int", symbol.getIdentifier());
        assertEquals(FILE, symbol.getPosition().getFile().toString());
        assertEquals(5, symbol.getPosition().getLine());
        assertEquals(11, symbol.getPosition().getColumn());

        symbol = scanner.next();
        assertEquals(SymbolType.IDENT, symbol.getType());
        assertEquals("const1", symbol.getIdentifier());
        assertEquals(FILE, symbol.getPosition().getFile().toString());
        assertEquals(5, symbol.getPosition().getLine());
        assertEquals(15, symbol.getPosition().getColumn());

        symbol = scanner.next();
        assertEquals(SymbolType.EQUALS, symbol.getType());
        assertEquals("=", symbol.getIdentifier());
        assertEquals(FILE, symbol.getPosition().getFile().toString());
        assertEquals(5, symbol.getPosition().getLine());
        assertEquals(22, symbol.getPosition().getColumn());

        symbol = scanner.next();
        assertEquals(SymbolType.NUMBER, symbol.getType());
        assertEquals("123", symbol.getIdentifier());
        assertEquals(FILE, symbol.getPosition().getFile().toString());
        assertEquals(5, symbol.getPosition().getLine());
        assertEquals(24, symbol.getPosition().getColumn());

        symbol = scanner.next();
        assertEquals(SymbolType.SEMICOLON, symbol.getType());
        assertEquals(";", symbol.getIdentifier());
        assertEquals(FILE, symbol.getPosition().getFile().toString());
        assertEquals(5, symbol.getPosition().getLine());
        assertEquals(27, symbol.getPosition().getColumn());

        // final int const2 = const1 * const1;
        assertEquals(SymbolType.FINAL, scanner.next().getType());
        assertEquals(SymbolType.INT, scanner.next().getType());
        assertEquals(SymbolType.IDENT, scanner.next().getType());
        assertEquals(SymbolType.EQUALS, scanner.next().getType());
        assertEquals(SymbolType.IDENT, scanner.next().getType());
        assertEquals(SymbolType.TIMES, scanner.next().getType());
        assertEquals(SymbolType.IDENT, scanner.next().getType());
        assertEquals(SymbolType.SEMICOLON, scanner.next().getType());

        // int var1;
        assertEquals(SymbolType.INT, scanner.next().getType());
        assertEquals(SymbolType.IDENT, scanner.next().getType());
        assertEquals(SymbolType.SEMICOLON, scanner.next().getType());

        // public void setVar1(int x) {
        assertEquals(SymbolType.PUBLIC, scanner.next().getType());
        assertEquals(SymbolType.VOID, scanner.next().getType());
        assertEquals(SymbolType.IDENT, scanner.next().getType());
        assertEquals(SymbolType.PARENTHESIS_OPEN, scanner.next().getType());
        assertEquals(SymbolType.INT, scanner.next().getType());
        assertEquals(SymbolType.IDENT, scanner.next().getType());
        assertEquals(SymbolType.PARENTHESIS_CLOSE, scanner.next().getType());
        assertEquals(SymbolType.CURLY_BRACE_OPEN, scanner.next().getType());

        // var1 = x;
        assertEquals(SymbolType.IDENT, scanner.next().getType());
        assertEquals(SymbolType.EQUALS, scanner.next().getType());
        assertEquals(SymbolType.IDENT, scanner.next().getType());
        assertEquals(SymbolType.SEMICOLON, scanner.next().getType());

        // }
        assertEquals(SymbolType.CURLY_BRACE_CLOSE, scanner.next().getType());

        // public int getVar1() {
        assertEquals(SymbolType.PUBLIC, scanner.next().getType());
        assertEquals(SymbolType.INT, scanner.next().getType());
        assertEquals(SymbolType.IDENT, scanner.next().getType());
        assertEquals(SymbolType.PARENTHESIS_OPEN, scanner.next().getType());
        assertEquals(SymbolType.PARENTHESIS_CLOSE, scanner.next().getType());
        assertEquals(SymbolType.CURLY_BRACE_OPEN, scanner.next().getType());

        // return var1;
        assertEquals(SymbolType.RETURN, scanner.next().getType());
        assertEquals(SymbolType.IDENT, scanner.next().getType());
        assertEquals(SymbolType.SEMICOLON, scanner.next().getType());

        // }
        assertEquals(SymbolType.CURLY_BRACE_CLOSE, scanner.next().getType());

        // public void incrementVar1() {
        assertEquals(SymbolType.PUBLIC, scanner.next().getType());
        assertEquals(SymbolType.VOID, scanner.next().getType());
        assertEquals(SymbolType.IDENT, scanner.next().getType());
        assertEquals(SymbolType.PARENTHESIS_OPEN, scanner.next().getType());
        assertEquals(SymbolType.PARENTHESIS_CLOSE, scanner.next().getType());
        assertEquals(SymbolType.CURLY_BRACE_OPEN, scanner.next().getType());

        // var1 = getVar1() + 1;
        assertEquals(SymbolType.IDENT, scanner.next().getType());
        assertEquals(SymbolType.EQUALS, scanner.next().getType());
        assertEquals(SymbolType.IDENT, scanner.next().getType());
        assertEquals(SymbolType.PARENTHESIS_OPEN, scanner.next().getType());
        assertEquals(SymbolType.PARENTHESIS_CLOSE, scanner.next().getType());
        assertEquals(SymbolType.PLUS, scanner.next().getType());
        assertEquals(SymbolType.NUMBER, scanner.next().getType());
        assertEquals(SymbolType.SEMICOLON, scanner.next().getType());

        // }
        assertEquals(SymbolType.CURLY_BRACE_CLOSE, scanner.next().getType());

        // }
        assertEquals(SymbolType.CURLY_BRACE_CLOSE, scanner.next().getType());
    }
}