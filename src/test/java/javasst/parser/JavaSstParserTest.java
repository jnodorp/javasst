package javasst.parser;

import javasst.ast.JavaSstNode;
import javasst.scanner.JavaSstScanner;
import javasst.scanner.JavaSstToken;
import javasst.JavaSstType;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import parser.Parser;
import parser.SymbolTable;
import scanner.Input;
import scanner.Scanner;

import java.io.FileNotFoundException;

import static org.junit.Assert.*;

/**
 * Test class for {@link JavaSstParser}.
 */
public class JavaSstParserTest {

    @Test
    public void testParse() throws Exception {
        final Input input = new Input("src/test/resources/test.sst");
        final Scanner<JavaSstToken, JavaSstType> scanner = new JavaSstScanner(input);
        Parser<JavaSstToken, JavaSstType, JavaSstParserObject, JavaSstNode> parser = new JavaSstParser(scanner);

        parser.parse();
    }

    @Test(expected = Exception.class)
    public void testError() throws FileNotFoundException {
        final Input input = new Input("src/test/resources/error_test.sst");
        final Scanner<JavaSstToken, JavaSstType> scanner = new JavaSstScanner(input);
        Parser parser = new JavaSstParser(scanner);

        parser.parse();
    }

    @Test
    public void testSymbolTable() throws FileNotFoundException {
        final Input input = new Input("src/test/resources/table_test.sst");
        final Scanner<JavaSstToken, JavaSstType> scanner = new JavaSstScanner(input);
        JavaSstParser parser = new JavaSstParser(scanner);

        parser.parse();

        @SuppressWarnings("unchecked")
        SymbolTable<JavaSstParserObject> root = (SymbolTable) Whitebox.getInternalState(parser, "symbolTable");
        while (root.getEnclose().isPresent()) {
            root = root.getEnclose().get();
        }

        assertFalse(root.getEnclose().isPresent());
        assertTrue(root.object("A").isPresent());

        JavaSstParserObject a = root.object("A").get();
        assertEquals(1, a.getFunctionDeclarations().size());
        assertEquals("f", a.getFunctionDeclarations().get(0).getIdentifier());
        assertEquals(JavaSstType.CLASS, a.getObjectClass());
        assertNotNull(a.getSymbolTable());
        assertEquals(1, a.getVariableDefinitions().size());
        assertEquals("y", a.getVariableDefinitions().get(0).getIdentifier());

        JavaSstParserObject b = a.getSymbolTable().object("b").get();
        assertEquals("b", b.getIdentifier());
        assertEquals(3, b.getIntValue());
        assertEquals(JavaSstType.CONSTANT, b.getObjectClass());
        assertEquals(JavaSstType.INTEGER, b.getType());

        JavaSstParserObject f = a.getSymbolTable().object("f").get();
        assertEquals("f", f.getIdentifier());
        assertEquals(1, f.getParameterList().size());
        assertEquals("x", f.getParameterList().get(0).getIdentifier());
        assertNotNull(f.getSymbolTable());

        JavaSstParserObject x = f.getSymbolTable().object("x").get();
        assertEquals("x", x.getIdentifier());
        assertEquals(JavaSstType.INTEGER, x.getType());
        assertNotNull(f.getSymbolTable());
    }
}