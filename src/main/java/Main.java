import ast.Ast;
import javasst.JavaSstType;
import javasst.ast.JavaSstNode;
import javasst.parser.JavaSstParser;
import javasst.parser.JavaSstParserObject;
import javasst.scanner.JavaSstScanner;
import javasst.scanner.JavaSstToken;
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
 * The main class compiles a given file and displays the {@link ast.Ast} as a PDF. This requires the installation of dot
 * (<a href="http://graphviz.org/">http://graphviz.org/</a>) and one of the following PDF viewers:
 * <ul>
 * <li><a href="https://wiki.gnome.org/Apps/Evince">Evince</a></li>
 * <li><a href="https://okular.kde.org/">Okular</a></li>
 * </ul>
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
     * PDF viewers to try.
     */
    private static final String[] VIEWERS = {"evince", "okular"};

    /**
     * Compile the given source file.
     *
     * @param args The source file.
     */
    public static void main(String args[]) {
        String file = "src/test/resources/test.sst";

        if (args.length != 1) {
            LOGGER.warning("Wrong number of arguments. Expected 1 but was " + args.length + "!");
            LOGGER.info("Using \"src/test/resources/test.sst\" by default.");
        } else {
            file = args[0];
        }

        Input input = null;
        try {
            input = new Input(file);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Unable to open file " + file + "!", e);
            System.exit(e.hashCode());
        }

        final Scanner<JavaSstToken, JavaSstType> scanner = new JavaSstScanner(input);
        Parser<JavaSstToken, JavaSstType, JavaSstParserObject, JavaSstNode> parser = new JavaSstParser(scanner);

        Ast<JavaSstNode> ast = parser.parse();
        try {
            FileUtils.writeStringToFile(AST, ast.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to save file " + AST + "!", e);
            System.exit(e.hashCode());
        }

        try {
            run("dot", "-Tps", AST.toString(), "-o", PDF.toString());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "You need to install 'dot' to run this program.", e);
            System.exit(e.hashCode());
        }

        try {
            FileUtils.forceDelete(AST);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to delete file " + AST + "!", e);
        }

        boolean success = false;
        for (String viewer : VIEWERS) {
            if (success) {
                break;
            }

            try {
                run(viewer, PDF.toString());
                success = true;
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Did not find " + viewer + "!", e);
            }
        }

        try {
            FileUtils.forceDelete(PDF);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to delete file " + PDF + "!", e);
        }

        if (!success) {
            LOGGER.log(Level.WARNING, "You must install one PDF viewer: " + Arrays.toString(VIEWERS) + ".");
        }

        LOGGER.info("Successfully compiled file " + file + ".");
    }

    /**
     * Run a command.
     *
     * @param cmd The command.
     * @return The exit code.
     */
    private static int run(final String... cmd) throws IOException {
        final ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.inheritIO();
        Process p = pb.start();
        try {
            while (p.isAlive()) {
                Thread.sleep(100);
            }
            return p.exitValue();
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Interrupt while running " + Arrays.toString(cmd) + ".", e);
            return e.hashCode();
        }
    }
}
