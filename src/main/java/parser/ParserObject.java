package parser;

import com.sun.org.apache.bcel.internal.generic.ObjectType;

import java.util.Optional;

/**
 * {@link ParserObject}s (e.g. variables, constant, etc.).
 */
public class ParserObject {

    /**
     * The {@link SymbolTable}.
     */
    private final Optional<SymbolTable> symbolTable;

    /**
     * The {@link ObjectClass}.
     */
    private final ObjectClass objectClass;

    /**
     * The {@link ParserType}.
     */
    private Optional<ParserType> parserType;

    /**
     * The next {@link ParserObject}.
     */
    private Optional<ParserObject> next;

    /**
     * The integer value.
     */
    private Optional<Integer> integerValue;

    /**
     * The name.
     */
    private String name;

    /**
     * Create a new {@link ParserObject} with a {@link SymbolTable}.
     *
     * @param objectClass The {@link ObjectClass}.
     * @param symbolTable The {@link SymbolTable}.
     */
    public ParserObject(final ObjectClass objectClass, final SymbolTable symbolTable) {
        this.objectClass = objectClass;
        this.symbolTable = Optional.ofNullable(symbolTable);
        this.parserType = Optional.empty();
        this.next = Optional.empty();
        this.integerValue = Optional.empty();
    }

    /**
     * Create a new {@link ParserObject}.
     *
     * @param objectClass The {@link ObjectClass}.
     */
    public ParserObject(final ObjectClass objectClass) {
        this(objectClass, null);
    }

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
    public ObjectClass getObjectClass() {
        return objectClass;
    }

    /**
     * Get the {@link ParserType}.
     *
     * @return The {@link ParserType}.
     */
    public Optional<ParserType> getParserType() {
        return parserType;
    }

    /**
     * Set the {@link ParserType}.
     *
     * @param parserType The {@link ParserType}.
     */
    public void setParserType(final ParserType parserType) {
        this.parserType = Optional.ofNullable(parserType);
    }

    /**
     * Get the next {@link ParserObject}.
     *
     * @return The next {@link ParserObject}.
     */
    public Optional<ParserObject> getNext() {
        return next;
    }

    /**
     * Set the next {@link ParserObject}.
     *
     * @param next The next {@link ParserObject}.
     */
    public void setNext(ParserObject next) {
        this.next = Optional.ofNullable(next);
    }

    /**
     * Get the {@link SymbolTable}.
     *
     * @return The {@link SymbolTable}.
     */
    public Optional<SymbolTable> getSymbolTable() {
        return symbolTable;
    }

    /**
     * Get the method declarations.
     *
     * @return The method declarations.
     */
    public Optional<ParserObject> getMethodDeclarations() {
        if (objectClass == ObjectClass.CLASS) {
            return Optional.empty(); // FIXME
        } else {
            return Optional.empty();
        }
    }

    /**
     * Get the variable definitions.
     *
     * @return The variable definitions.
     */
    public Optional<ParserObject> getVariableDefinitions() {
        if (objectClass == ObjectClass.CLASS) {
            return Optional.empty(); // FIXME
        } else {
            return Optional.empty();
        }
    }

    /**
     * Get the integer value.
     *
     * @return The integer value.
     */
    public Optional<Integer> getIntegerValue() {
        return integerValue;
    }

    /**
     * Get the integer value.
     *
     * @param integerValue The integer value.
     */
    public void setIntegerValue(int integerValue) {
        this.integerValue = Optional.of(integerValue);
    }

    /**
     * Get the parameter list.
     *
     * @return The parameter list.
     */
    public Optional<ParserObject> getParameterList() {
        if (objectClass == ObjectClass.PROCEDURE) {
            return Optional.empty(); // FIXME
        } else {
            return Optional.empty();
        }
    }

    /**
     * Get the result.
     *
     * @return The result.
     */
    public Optional<ObjectType> getResult() {
        if (objectClass == ObjectClass.PROCEDURE) {
            return Optional.empty(); // FIXME
        } else {
            return Optional.empty();
        }
    }
}
