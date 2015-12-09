package parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * A token table.
 */
public class SymbolTable {

    /**
     * The {@link ParserObject}s.
     */
    private final List<ParserObject> parserObjects;

    /**
     * The enclosing token table.
     */
    private SymbolTable enclose;

    /**
     * The head.
     */
    private ParserObject head;

    /**
     * Create a new token table.
     *
     * @param head    The head.
     * @param enclose The enclosing token table.
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
     * Get the enclosing token table.
     *
     * @return The enclosing token table.
     */
    public SymbolTable getEnclose() {
        return enclose;
    }

    /**
     * Set the enclosing token table.
     *
     * @param enclose The enclosing token table.
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
        object.setNext(null);
        parserObjects.add(object);

        if (head == null) {
            head = object;
        } else {
            ParserObject pointer = head;
            while (pointer.getNext() != null) {
                pointer = pointer.getNext();
            }

            pointer.setNext(object);
        }
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
