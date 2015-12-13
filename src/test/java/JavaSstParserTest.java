import javasst.JavaSstParser;
import javasst.JavaSstScanner;
import javasst.JavaSstToken;
import javasst.JavaSstTokenType;
import org.junit.Test;
import parser.Parser;
import parser.ParserObject;
import parser.SymbolTable;
import scanner.Input;
import scanner.Scanner;

import java.io.FileNotFoundException;

/**
 * Test class for {@link JavaSstParser}.
 */
public class JavaSstParserTest {

    /**
     * Convert {@link ParserObject} to {@link String}.
     *
     * @param parserObject The {@link ParserObject}.
     * @return The {@link String}.
     */
    private static String convertParserObjectToString(ParserObject parserObject) {
        String out = "";
        while (parserObject.getNext() != null) {
            out += parserObject.getName() + System.lineSeparator();
            out += "\t|" + System.lineSeparator();
            out += "\tv" + System.lineSeparator();
            parserObject = parserObject.getNext();
        }

        return out;
    }

    /**
     * Convert {@link parser.SymbolTable} to {@link String}.
     *
     * @param symbolTable The {@link parser.SymbolTable}.
     * @return The {@link String}.
     */
    private static String convertSymbolTableToString(SymbolTable symbolTable) {
        String head = "head -> " + convertParserObjectToString(symbolTable.getHead()) + System.lineSeparator();
        String enclose = "enclose -> " + symbolTable.getEnclose() + System.lineSeparator();
        return head + enclose;
    }

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
            System.out.println(convertSymbolTableToString(st));
        });
    }
}