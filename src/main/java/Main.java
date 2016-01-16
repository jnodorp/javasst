import ast.Ast;
import javasst.ast.JavaSstNode;
import javasst.parser.JavaSstParser;
import javasst.parser.JavaSstParserObject;
import javasst.scanner.JavaSstScanner;
import javasst.scanner.JavaSstToken;
import javasst.JavaSstType;
import org.apache.commons.io.FileUtils;
import parser.Parser;
import scanner.Input;
import scanner.Scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main class compiles a given file and displays the {@link ast.Ast} as a PDF.
 */
public class Main {

    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * The {@link Ast} file.
     */
    private static final File AST = new File("ast.dot").getAbsoluteFile();

    /**
     * The PDF file.
     */
    private static final File PDF = new File("ast.pdf").getAbsoluteFile();

    /**
     * Compile the given source file.
     *
     * @param args The source file.
     */
    public static void main(String args[]) throws InterruptedException {
        String file = "src/test/resources/test.sst";

        if (args.length != 1) {
            LOGGER.warning("Wrong number of arguments. Expected 1 but was " + args.length + "!");
            LOGGER.info("Using \"src/test/resources/test.sst\" by default.");
        } else {
            file = args[0];
        }

        final Input input;
        try {
            input = new Input(file);
            final Scanner<JavaSstToken, JavaSstType> scanner = new JavaSstScanner(input);
            Parser<JavaSstToken, JavaSstType, JavaSstParserObject, JavaSstNode> parser = new JavaSstParser(scanner);

            Ast<JavaSstNode> ast = parser.parse();
            FileUtils.writeStringToFile(AST, ast.toString(), StandardCharsets.UTF_8);
            run("dot", "-Tps", AST.toString(), "-o", PDF.toString());
            FileUtils.forceDelete(AST);
            run("evince", PDF.toString());
            FileUtils.forceDelete(PDF);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Unable to open file " + file + "!", e);
            System.exit(e.hashCode());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to save file " + AST + "!", e);
            System.exit(e.hashCode());
        }
    }

    /**
     * Run a command.
     *
     * @param cmd The command.
     * @return The exit code.
     */
    private static int run(final String... cmd) {
        final ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.inheritIO();
        try {
            Process p = pb.start();
            while (p.isAlive()) {
                Thread.sleep(100);
            }
            return p.exitValue();
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Interrupt while running " + Arrays.toString(cmd) + ".", e);
            return e.hashCode();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while running " + Arrays.toString(cmd) + ".", e);
            return e.hashCode();
        }
    }
}
