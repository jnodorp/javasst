package javasst.scanner;

import javasst.JavaSstType;
import scanner.Token;

import java.io.File;

/**
 * This class contains constants for all terminal symbols.
 */
public class JavaSstToken extends Token<JavaSstType> {

    /**
     * Create a new token.
     *
     * @param identifier The identifier.
     * @param type       The type.
     * @param line       The line.
     * @param column     The column.
     * @param file       The file.
     */
    public JavaSstToken(final String identifier, final JavaSstType type, final int line, final int column, final File file) {
        super(identifier, type, line, column, file);
    }
}
