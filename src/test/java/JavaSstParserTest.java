import javasst.JavaSstParser;
import javasst.JavaSstScanner;
import javasst.JavaSstToken;
import org.junit.Test;
import parser.Parser;
import scanner.Input;

import java.io.FileNotFoundException;
import java.util.Iterator;

/**
 * Test class for {@link JavaSstParser}.
 */
public class JavaSstParserTest {

    @Test
    public void testParse() throws Exception {
        final Input input = new Input("src/test/resources/test.sst");
        final Iterator<JavaSstToken> scanner = new JavaSstScanner(input);
        Parser parser = new JavaSstParser(scanner);

        parser.parse();
    }

    @Test(expected = Exception.class)
    public void testError() throws FileNotFoundException {
        final Input input = new Input("src/test/resources/error_test.sst");
        final Iterator<JavaSstToken> scanner = new JavaSstScanner(input);
        Parser parser = new JavaSstParser(scanner);

        parser.parse();
    }
}