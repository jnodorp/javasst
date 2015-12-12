package javasst;

import scanner.Token;

import java.io.File;

/**
 * This class contains constants for all terminal symbols.
 */
public class JavaSstToken implements Token<JavaSstTokenType> {

    /**
     * The identifier.
     */
    private final String identifier;

    /**
     * The type.
     */
    private final JavaSstTokenType type;

    /**
     * The line.
     */
    private final int line;

    /**
     * The column.
     */
    private final int column;

    /**
     * The file.
     */
    private final File file;

    /**
     * Create a new {@link JavaSstToken}.
     *
     * @param identifier The identifier.
     * @param type       The {@link JavaSstTokenType}.
     * @param line       The line.
     * @param column     The column.
     * @param file       The {@link File}.
     */
    public JavaSstToken(final String identifier, final JavaSstTokenType type, final int line, final int column, final File file) {
        this.identifier = identifier;
        this.type = type;
        this.line = line;
        this.column = column;
        this.file = file;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public JavaSstTokenType getType() {
        return type;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return "{" + System.lineSeparator() +
                "\t\"type\": \"" + type + "\"" + System.lineSeparator() +
                "\t\"identifier\": \"" + identifier + "\"" + System.lineSeparator() +
                "\t\"line\": \"" + line + "\"" + System.lineSeparator() +
                "\t\"column\": \"" + column + "\"" + System.lineSeparator() +
                "\t\"file\": \"" + file + "\"" + System.lineSeparator() +
                "}";
    }
}
