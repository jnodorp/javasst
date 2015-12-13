package scanner;

import java.io.*;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class wraps a {@link FileInputStream} adding the {@link Input#next()} method.
 */
public class Input implements Iterator<Character> {

    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Input.class.getName());

    /**
     * The file.
     */
    private final File file;

    /**
     * The underlying input stream.
     */
    private final InputStream inputStream;

    /**
     * The current line.
     */
    private int line = 1;

    /**
     * The current column.
     */
    private int column = 1;

    /**
     * @param name the system-dependent file name.
     * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for some
     *                               other reason cannot be opened for reading.
     * @see FileInputStream#FileInputStream(String)
     */
    public Input(final String name) throws FileNotFoundException {
        this.inputStream = new BufferedInputStream(new FileInputStream(name));
        this.file = new File(name);
    }

    @Override
    public boolean hasNext() {
        inputStream.mark(1);
        int next = -1;

        try {
            next = inputStream.read();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception while reading underlying input stream.", e);
        } finally {
            try {
                inputStream.reset();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Exception while resetting underlying input stream.", e);
            }
        }

        return next != -1;
    }

    @Override
    public Character next() {
        int c = -1;
        try {
            c = inputStream.read();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while reading input.", e);
        }

        if (c == -1) {
            LOGGER.log(Level.INFO, "Closing underlying input stream due to end of input.");
            try {
                inputStream.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Closing underlying input stream failed.", e);
                e.printStackTrace();
            }
        }

        if (c == '\n') {
            line++;
            column = 0;
        } else {
            column++;
        }

        return (char) c;
    }

    /**
     * Get the next {@code n} values. The current value stays the same.
     *
     * @param n The number of values to look ahead.
     * @return An array of the next {@code n} values.
     */
    public Character[] lookahead(int n) {
        final Character[] result = new Character[n];

        inputStream.mark(n);

        for (int i = 0; i < n; i++) {
            try {
                result[i] = (char) inputStream.read();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Exception during lookahead.", e);
            }
        }

        try {
            inputStream.reset();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while resetting input after lookahead.", e);
        }

        return result;
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
    public File getFile() {
        return file;
    }
}
