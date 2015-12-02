package parser;

/**
 * A type.
 */
abstract class ParserType {

    /**
     * The {@link TypeKind}.
     */
    private final TypeKind kind;

    /**
     * The class.
     */
    private final ParserObject clazz;

    /**
     * Create a new type.
     *
     * @param kind  The {@link TypeKind}.
     * @param clazz The class.
     */
    public ParserType(final TypeKind kind, final ParserObject clazz) {
        this.kind = kind;
        this.clazz = clazz;
    }

    /**
     * Get the {@link TypeKind}.
     *
     * @return The {@link TypeKind}.
     */
    public TypeKind getKind() {
        return kind;
    }

    /**
     * Get the class.
     *
     * @return The class.
     */
    public ParserObject getClazz() {
        return clazz;
    }
}
