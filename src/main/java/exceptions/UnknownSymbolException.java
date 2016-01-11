package exceptions;

import scanner.Token;

/**
 * Exception thrown when the code uses objects not contained in the {@link parser.SymbolTable}s.
 */
public class UnknownSymbolException extends RuntimeException {

    /**
     * The {@link Token}.
     */
    private final Token<? extends Enum> token;

    /**
     * Create a new exception.
     *
     * @param token The {@link Token}.
     */
    public UnknownSymbolException(final Token<? extends Enum> token) {
        super(token.toString());
        this.token = token;
    }
}
