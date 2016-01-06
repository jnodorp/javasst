package exceptions;

/**
 * Exception thrown when the code uses objects not contained in the {@link parser.SymbolTable}s.
 */
public class UnknownSymbolException extends RuntimeException {

    /**
     * TODO: Improve messaging.
     */
    public UnknownSymbolException() {

    }
}
