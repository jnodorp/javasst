package parser;

/**
 * A token table.
 */
public class SymbolTable {

    /**
     * The enclosing symbol table.
     */
    private final SymbolTable enclose;

    /**
     * The head.
     */
    private ParserObject head;

    /**
     * Create a new symbol table.
     *
     * @param enclose The enclosing token table.
     */
    public SymbolTable(final SymbolTable enclose) {
        this.enclose = enclose;
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
     * Get the enclosing token table.
     *
     * @return The enclosing token table.
     */
    public SymbolTable getEnclose() {
        return enclose;
    }

    /**
     * Insert a {@link ParserObject}.
     *
     * @param object The {@link ParserObject}.
     */
    public void insert(ParserObject object) {
        object.setNext(null);

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
     * @param name The name.
     * @return The {@link ParserObject}.
     */
    public ParserObject getObject(final String name/* , final ObjectClass objectClass */) {
        return null; // FIXME
    }
}
