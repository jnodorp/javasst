package scanner;

import java.io.File;

/**
 * This object stores a position within a parsed file.
 */
public class Position {

    /**
     * The file.
     */
    private final File file;

    /**
     * The line.
     */
    private final int line;

    /**
     * The column.
     */
    private final int column;

    /**
     * Create a new position object.
     *
     * @param file   The currently scanned file.
     * @param line   The line.
     * @param column The column.
     */
    public Position(final File file, final int line, final int column) {
        this.file = file;
        this.line = line;
        this.column = column;
    }

    /**
     * Get the file.
     *
     * @return The file.
     */
    public File getFile() {
        return file;
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

    @Override
    public String toString() {
        return getFile() + "(" + getLine() + ":" + getColumn() + ")";
    }
}
