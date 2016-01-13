package exceptions;

import parser.ParserObject;

/**
 * Exception thrown when double defining symbols.
 */
public class SymbolAlreadyExists extends Throwable {

    /**
     * Create a new exception.
     *
     * @param existing The {@link ParserObject} representing the already defined symbol.
     * @param added    The {@link ParserObject} representing the newly defined symbol.
     */
    public SymbolAlreadyExists(final ParserObject existing, final ParserObject added) {
        super("Defining " + added + " but already defined as " + existing);
    }
}
