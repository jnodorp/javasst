package parser;

/**
 * {@link ParserObject}s (e.g. variables, constant, etc.).
 */
abstract class ParserObject {

    /**
     * The name.
     */
    private String name;

    /**
     * The {@link ParserType}.
     */
    private ParserType parserType;

    /**
     * The next {@link ParserObject}.
     */
    private ParserObject next;

    /**
     * Get the name.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     *
     * @param name The name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the {@link ObjectClass}.
     *
     * @return The {@link ObjectClass}.
     */
    public abstract ObjectClass getParserObjectClass();

    /**
     * Get the {@link ParserType}.
     *
     * @return The {@link ParserType}.
     */
    public ParserType getParserType() {
        return parserType;
    }

    /**
     * Set the {@link ParserType}.
     *
     * @param parserType The {@link ParserType}.
     */
    public void setParserType(final ParserType parserType) {
        this.parserType = parserType;
    }

    /**
     * Get the next {@link ParserObject}.
     *
     * @return The next {@link ParserObject}.
     */
    public ParserObject getNext() {
        return next;
    }

    /**
     * Set the next {@link ParserObject}.
     *
     * @param next The next {@link ParserObject}.
     */
    public void setNext(ParserObject next) {
        this.next = next;
    }
}
