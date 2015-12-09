package scanner;

import java.io.File;

/**
 * This class contains constants for all terminal symbols.
 */
public class TokenImpl implements Token<TokenType> {

    /**
     * The identifier.
     */
    private final String identifier;

    /**
     * The type.
     */
    private final TokenType type;

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
     * Create a new {@link TokenImpl}.
     *
     * @param identifier The identifier.
     * @param type       The {@link TokenType}.
     * @param line       The line.
     * @param column     The column.
     * @param file       The {@link File}.
     */
    public TokenImpl(final String identifier, final TokenType type, final int line, final int column, final File file) {
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
    public TokenType getType() {
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
