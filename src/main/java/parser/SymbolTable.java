package parser;

import java.util.Optional;

/**
 * A token table.
 */
public class SymbolTable {

    /**
     * The enclosing symbol table.
     */
    private final Optional<SymbolTable> enclose;

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
        this.enclose = Optional.ofNullable(enclose);
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
     * Get the enclosing {@link SymbolTable}.
     *
     * @return The enclosing {@link SymbolTable}.
     */
    public Optional<SymbolTable> getEnclose() {
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
            while (pointer.getNext().isPresent()) {
                pointer = pointer.getNext().get();
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

    /**
     * Get the owner of this {@link SymbolTable}.
     *
     * @return The owner of this {@link SymbolTable} or {@code null} if this {@link SymbolTable} does not have an owner.
     */
    private Optional<ParserObject> getOwner() {
        if (enclose.isPresent()) {
            ParserObject pointer = enclose.get().getHead();

            if (pointer.getSymbolTable().isPresent()) {
                if (pointer.getSymbolTable().get().equals(this)) {
                    return Optional.of(pointer);
                }
            }

            while (pointer.getNext().isPresent()) {
                pointer = pointer.getNext().get();

                if (pointer.getSymbolTable().isPresent()) {
                    if (pointer.getSymbolTable().get().equals(this)) {
                        return Optional.of(pointer);
                    }
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public String toString() {
        return "st1[label=\"{ SymbolTable | <head> head | <enclose> enclose }\"];";
    }
}
