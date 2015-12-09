package scanner;

import parser.Token;

/**
 * This class contains constants for all terminal symbols.
 */
public class Symbol implements Token<SymbolType> {

    /**
     * The identifier.
     */
    private final String identifier;

    /**
     * The type.
     */
    private final SymbolType type;

    /**
     * The position.
     */
    private final Position position;

    /**
     * Create a new {@link Symbol}.
     *
     * @param identifier The identifier.
     * @param type       The {@link SymbolType}.
     * @param position   The {@link Position}.
     */
    public Symbol(final String identifier, final SymbolType type, final Position position) {
        this.identifier = identifier;
        this.type = type;
        this.position = position;
    }

    /**
     * Get the identifier.
     *
     * @return The identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Get the type.
     *
     * @return The type.
     */
    public SymbolType getType() {
        return type;
    }

    /**
     * Get the {@link Position}.
     *
     * @return The {@link Position}.
     */
    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "{\"type\": \"" + type + "\", \"identifier\": \"" + identifier + "\"}";
    }
}
