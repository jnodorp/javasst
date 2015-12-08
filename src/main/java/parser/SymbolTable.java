package parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * A symbol table.
 */
public class SymbolTable {

    /**
     * The {@link ParserObject}s.
     */
    private final List<ParserObject> parserObjects;

    /**
     * The enclosing symbol table.
     */
    private SymbolTable enclose;

    /**
     * The head.
     */
    private ParserObject head;

    /**
     * Create a new symbol table.
     *
     * @param head    The head.
     * @param enclose The enclosing symbol table.
     */
    public SymbolTable(final ParserObject head, final SymbolTable enclose) {
        this.head = head;
        this.enclose = enclose;
        this.parserObjects = new LinkedList<>();
    }

    /**
     * Get the head.
     *
     * @return The head.
     */
    public ParserObject getHead() {
        return head;
    }

    /**
     * Set the head.
     *
     * @param head The head.
     */
    public void setHead(final ParserObject head) {
        this.head = head;
    }

    /**
     * Get the enclosing symbol table.
     *
     * @return The enclosing symbol table.
     */
    public SymbolTable getEnclose() {
        return enclose;
    }

    /**
     * Set the enclosing symbol table.
     *
     * @param enclose The enclosing symbol table.
     */
    public void setEnclose(final SymbolTable enclose) {
        this.enclose = enclose;
    }

    /**
     * Insert a {@link ParserObject}.
     *
     * @param object The {@link ParserObject}.
     */
    public void insert(ParserObject object) {
        parserObjects.add(object);
    }

    /**
     * Get a {@link ParserObject}.
     *
     * @param name        The name.
     * @param objectClass The {@link ObjectClass}.
     * @return The {@link ParserObject}.
     */
    public ParserObject getObject(final String name, final ObjectClass objectClass) {
        Optional<ParserObject> parserObject = parserObjects.stream().filter(p -> p.getName().equals(name)).filter(p
                -> p.getParserObjectClass().equals(objectClass)).findAny();

        return parserObject.orElse(null);
    }
}
