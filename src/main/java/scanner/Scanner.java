package scanner;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * This class processes input provided by an {@link Input} instance.
 */
public abstract class Scanner<T extends Token<E>, E extends Enum> {

    /**
     * The logger.
     */
    protected static final Logger LOGGER = Logger.getLogger(Scanner.class.getName());

    /**
     * The {@link Input} stream.
     */
    protected final Input input;

    /**
     * The last read character.
     */
    protected char current;

    /**
     * The read characters since the last token.
     */
    protected String stack;

    /**
     * Create a new scanner which processes the input provided by an {@link Input} instance.
     *
     * @param input The {@link Input} instance.
     */
    protected Scanner(final Input input) {
        this.input = input;
    }

    /**
     * Get the next {@link Token}.
     *
     * @return The next {@link Token}.
     */
    public abstract T next();

    /**
     * Perform a lookahead.
     *
     * @param match   The characters to look at.
     * @param success {@link Token} type to return on match.
     * @param failure {@link Token} type to return on mismatch.
     * @return One of the specified {@link Token} types.
     */
    protected E lookahead(final String match, final E success, final E failure) {
        final String newMatch = match.substring(1);
        final StringBuilder lookahead = new StringBuilder();
        Arrays.stream(input.lookahead(newMatch.getBytes().length)).forEach(lookahead::append);

        if (newMatch.equals(lookahead.toString())) {
            stack = match;
            for (int i = 0; i < newMatch.getBytes().length; i++) {
                input.next();
            }
            return success;
        } else {
            return failure;
        }
    }
}
