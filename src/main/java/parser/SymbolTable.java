package parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * A token table.
 *
 * @param <O> The objects contained within this symbol table.
 */
public class SymbolTable<O extends parser.ParserObject> {

    /**
     * The enclosing symbol table.
     */
    private final Optional<SymbolTable<O>> enclose;

    /**
     * The {@link ParserObject}s.
     */
    private final List<O> objects;

    /**
     * Create a new symbol table.
     *
     * @param enclose The enclosing token table.
     */
    public SymbolTable(final SymbolTable<O> enclose) {
        this.enclose = Optional.ofNullable(enclose);
        this.objects = new LinkedList<>();
    }

    /**
     * Get the enclosing {@link SymbolTable}.
     *
     * @return The enclosing {@link SymbolTable}.
     */
    public Optional<SymbolTable<O>> getEnclose() {
        return enclose;
    }

    /**
     * Get a {@link ParserObject}.
     * <p>
     * TODO: Allow e.g. methods and variables with the same name.
     *
     * @param name The name.
     * @return The {@link ParserObject}.
     */
    public Optional<O> get(final String name) {
        return objects.stream().filter(parserObject -> name.equals(parserObject.getIdentifier())).findFirst();
    }

    /**
     * Add a {@link ParserObject}.
     *
     * @param object The {@link ParserObject}.
     */
    public void add(final O object) {
        objects.add(object);
    }
}
