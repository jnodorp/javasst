import javasst.JavaSstParser;
import javasst.JavaSstScanner;
import javasst.JavaSstToken;
import javasst.JavaSstTokenType;
import org.junit.Test;
import parser.ObjectClass;
import parser.Parser;
import parser.ParserObject;
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
        final Scanner<JavaSstToken, JavaSstTokenType> scanner = new JavaSstScanner(input);
        Parser parser = new JavaSstParser(scanner);

        parser.parse();
    }

    @Test(expected = Exception.class)
    public void testError() throws FileNotFoundException {
        final Input input = new Input("src/test/resources/error_test.sst");
        final Scanner<JavaSstToken, JavaSstTokenType> scanner = new JavaSstScanner(input);
        Parser parser = new JavaSstParser(scanner);

        parser.parse();
    }

    @Test
    public void testSymbolTable() throws FileNotFoundException {
        final Input input = new Input("src/test/resources/table_test.sst");
        final Scanner<JavaSstToken, JavaSstTokenType> scanner = new JavaSstScanner(input);
        JavaSstParser parser = new JavaSstParser(scanner);

        parser.parse(st -> {
            SymbolTable root = st;
            while (root.getEnclose().isPresent()) {
                root = root.getEnclose().get();
            }

            assertFalse(root.getEnclose().isPresent());
            assertEquals("A", root.getHead().getName());
            assertFalse(root.getHead().getIntegerValue().isPresent());
            // assertTrue(root.getHead().getMethodDeclarations().isPresent()); // FIXME
            assertFalse(root.getHead().getNext().isPresent());
            assertEquals(ObjectClass.CLASS, root.getHead().getObjectClass());
            assertFalse(root.getHead().getParameterList().isPresent());
            assertFalse(root.getHead().getParserType().isPresent());
            assertFalse(root.getHead().getResult().isPresent());
            assertTrue(root.getHead().getSymbolTable().isPresent());
            // assertTrue(root.getHead().getVariableDefinitions().isPresent()); // FIXME

            ParserObject head = root.getHead().getSymbolTable().get().getHead();
            assertEquals("b", head.getName());
            assertEquals(3, head.getIntegerValue().get(), 0);
        });
    }
}