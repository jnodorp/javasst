package parser;

import scanner.Token;

/**
 * An object placed in a {@link SymbolTable}.
 */
public interface ParserObject {

    /**
     * Get the identifier.
     *
     * @return The identifier.
     */
    String getIdentifier();

    /**
     * Get the {@link Token}.
     *
     * @return The {@link Token}.
     */
    Token getToken();
}
