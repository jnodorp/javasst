package parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * A token table.
 *
 * @param <O> The objects contained within this symbol table.
 */
public final class SymbolTable<O extends parser.ParserObject> {

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
     * Get all {@link ParserObject}s of THIS symbol table.
     *
     * @return All {@link ParserObject}s of THIS symbol table.
     */
    public List<O> getObjects() {
        return objects;
    }

    /**
     * Get a {@link ParserObject} from this or any enclosing symbol table.
     * <p>
     * TODO: Allow e.g. functions and variables with the same name.
     *
     * @param name The objects name.
     * @return The {@link ParserObject} from this or any enclosing symbol table.
     */
    public Optional<O> object(final String name) {
        Optional<O> result = objects.stream().filter(parserObject -> name.equals(parserObject.getIdentifier())).findAny();
        if (result.isPresent()) {
            return result;
        } else if (getEnclose().isPresent()) {
            return getEnclose().get().object(name);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Add a {@link ParserObject}.
     *
     * @param object The {@link ParserObject}.
     */
    public void add(final O object) throws SymbolAlreadyExists {
        Optional<O> existing = objects.stream().filter(o -> o.getIdentifier().equals(object.getIdentifier())).findAny();
        if (existing.isPresent()) {
            throw new SymbolAlreadyExists(existing.get(), object);
        } else {
            objects.add(object);
        }
    }

    /**
     * Exception thrown when double defining symbols.
     */
    public static final class SymbolAlreadyExists extends Exception {

        /**
         * Create a new exception.
         *
         * @param existing The {@link ParserObject} representing the already defined symbol.
         * @param added    The {@link ParserObject} representing the newly defined symbol.
         */
        public SymbolAlreadyExists(final ParserObject existing, final ParserObject added) {
            super("Defining " + added + " but already defined as " + existing);
        }
    }
}
