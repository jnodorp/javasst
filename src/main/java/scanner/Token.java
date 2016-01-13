package scanner;

import java.io.File;
import java.util.Optional;

/**
 * A token is an identifier used within a programming language. E.g. int, class, etc..
 *
 * @param <E> The tokens type enumeration.
 */
public abstract class Token<E extends Enum> {

    /**
     * The identifier.
     */
    private final String identifer;

    /**
     * The type.
     */
    private final E type;

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
    private final Optional<File> file;

    /**
     * Create a new token.
     *
     * @param identifier The identifier.
     * @param type       The type.
     * @param line       The line.
     * @param column     The column.
     */
    public Token(final String identifier, final E type, final int line, final int column) {
        this(identifier, type, line, column, null);
    }

    /**
     * Create a new token.
     *
     * @param identifier The identifier.
     * @param type       The type.
     * @param line       The line.
     * @param column     The column.
     * @param file       The file.
     */
    public Token(final String identifier, final E type, final int line, final int column, final File file) {
        this.identifer = identifier;
        this.type = type;
        this.line = line;
        this.column = column;
        this.file = Optional.ofNullable(file);
    }

    /**
     * Get the tokens type.
     *
     * @return The type.
     */
    public E getType() {
        return type;
    }

    /**
     * Get the identifier.
     *
     * @return The identifier.
     */
    public String getIdentifier() {
        return identifer;
    }

    /**
     * Get the line.
     *
     * @return The line.
     */
    public int getLine() {
        return line;
    }

    /**
     * Get the column.
     *
     * @return The column.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Get the file.
     *
     * @return The file.
     */
    public Optional<File> getFile() {
        return file;
    }

    @Override
    public String toString() {
        final String base = type + " " + identifer + " at position " + line + ":" + column;
        if (file.isPresent()) {
            return base + " in file " + file.get();
        } else {
            return base;
        }
    }
}
