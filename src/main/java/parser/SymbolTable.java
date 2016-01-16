package parser;

import java.util.*;

/**
 * A token table.
 *
 * @param <O> The objects contained within this symbol table.
 */
public final class SymbolTable<O extends parser.ParserObject> {

    /**
     * The default category.
     */
    private static final String DEFAULT = "default";

    /**
     * The enclosing symbol table.
     */
    private final Optional<SymbolTable<O>> enclose;

    /**
     * The {@link ParserObject}s by category.
     */
    private final Map<String, List<O>> objects;

    /**
     * Create a new symbol table.
     *
     * @param enclose The enclosing token table.
     */
    public SymbolTable(final SymbolTable<O> enclose) {
        this.enclose = Optional.ofNullable(enclose);
        this.objects = new LinkedHashMap<>();
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
     * Get all {@link ParserObject}s of THIS symbol table with the default category.
     *
     * @return All {@link ParserObject}s of THIS symbol table with the default category.
     */
    public List<O> getObjects() {
        return getObjects(DEFAULT);
    }

    /**
     * Get all {@link ParserObject}s of THIS symbol table.
     *
     * @param category The category.
     * @return All {@link ParserObject}s of THIS symbol table.
     */
    public List<O> getObjects(final String category) {
        return objects.get(category);
    }

    /**
     * Get a {@link ParserObject} from this or any enclosing symbol table with the default category.
     *
     * @param name The objects name.
     * @return The {@link ParserObject} from this or any enclosing symbol table with the default category.
     */
    public Optional<O> object(final String name) {
        return object(DEFAULT, name);
    }

    /**
     * Get a {@link ParserObject} from this or any enclosing symbol table with the default category.
     *
     * @param category The category.
     * @param name     The objects name.
     * @return The {@link ParserObject} from this or any enclosing symbol table with the default category.
     */
    public Optional<O> object(final String category, final String name) {
        Optional<O> result = objects.getOrDefault(category, new LinkedList<>()).stream()
                .filter(parserObject -> name.equals(parserObject.getIdentifier())).findAny();
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
        add(DEFAULT, object);
    }

    /**
     * Add a {@link ParserObject}.
     *
     * @param category The category.
     * @param object   The {@link ParserObject}.
     */
    public void add(final String category, final O object) throws SymbolAlreadyExists {
        if (objects.get(category) == null) {
            objects.put(category, new LinkedList<>());
        }

        Optional<O> existing = objects.get(category).stream().filter(o -> o.getIdentifier().equals(object.getIdentifier())).findAny();
        if (existing.isPresent()) {
            throw new SymbolAlreadyExists(existing.get(), object);
        } else {
            objects.get(category).add(object);
        }
    }

    /**
     * Exception thrown when double defining symbols.
     */
    public static final class SymbolAlreadyExists extends RuntimeException {

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
