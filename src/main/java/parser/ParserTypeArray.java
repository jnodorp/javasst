package parser;

/**
 * An array type.
 */
public class ParserTypeArray extends ParserType {

    /**
     * The length.
     */
    private final int length;

    /**
     * The base type.
     */
    private final ParserType baseType;

    /**
     * Create a new array type.
     *
     * @param kind     The {@link TypeKind}.
     * @param clazz    The class.
     * @param length   The length.
     * @param baseType The base type.
     */
    public ParserTypeArray(final TypeKind kind, final ParserObject clazz, final int length, final ParserType baseType) {
        super(kind, clazz);
        this.length = length;
        this.baseType = baseType;
    }

    /**
     * Get the length.
     *
     * @return The length.
     */
    public int getLength() {
        return length;
    }

    /**
     * Get the base type.
     *
     * @return The base type.
     */
    public ParserType getBaseType() {
        return baseType;
    }
}
