package javasst.parser;

import parser.SymbolTable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * {@link JavaSstParserObject}s (e.g. variables, constant, etc.).
 */
public class JavaSstParserObject implements parser.ParserObject {

    /**
     * The {@link SymbolTable}.
     */
    private final SymbolTable<JavaSstParserObject> symbolTable;

    /**
     * The {@link JavaSstParserObjectClass}.
     */
    private final JavaSstParserObjectClass objectClass;

    /**
     * The name.
     */
    private String identifier;

    /**
     * The {@link JavaSstParserObjectType}.
     */
    private JavaSstParserObjectType type;

    /**
     * The int value.
     */
    private int intValue;

    /**
     * Create a new {@link JavaSstParserObject} with a {@link SymbolTable}.
     *
     * @param objectClass The {@link JavaSstParserObjectClass}.
     * @param symbolTable The {@link SymbolTable}.
     */
    public JavaSstParserObject(final JavaSstParserObjectClass objectClass, final SymbolTable<JavaSstParserObject> symbolTable) {
        this.objectClass = objectClass;
        this.symbolTable = symbolTable;
    }

    /**
     * Create a new {@link JavaSstParserObject}.
     *
     * @param objectClass The {@link JavaSstParserObjectClass}.
     */
    public JavaSstParserObject(final JavaSstParserObjectClass objectClass) {
        this(objectClass, null);
    }

    /**
     * Get the identifier.
     *
     * @return The identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Set the identifier.
     *
     * @param identifier The identifier.
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Get the {@link JavaSstParserObjectClass}.
     *
     * @return The {@link JavaSstParserObjectClass}.
     */
    public JavaSstParserObjectClass getObjectClass() {
        return objectClass;
    }

    /**
     * Get the {@link JavaSstParserObjectType}.
     *
     * @return The {@link JavaSstParserObjectType}.
     */
    public JavaSstParserObjectType getParserType() {
        return type;
    }

    /**
     * Set the {@link JavaSstParserObjectType}.
     *
     * @param type The {@link JavaSstParserObjectType}.
     */
    public void setType(final JavaSstParserObjectType type) {
        this.type = type;
    }

    /**
     * Get the {@link SymbolTable}.
     *
     * @return The {@link SymbolTable}.
     */
    public SymbolTable<JavaSstParserObject> getSymbolTable() {
        if (objectClass == JavaSstParserObjectClass.CLASS || objectClass == JavaSstParserObjectClass.PROCEDURE) {
            return symbolTable;
        } else {
            throw new ObjectClassException(JavaSstParserObjectClass.CLASS, JavaSstParserObjectClass.PROCEDURE);
        }
    }

    /**
     * Get the method declarations.
     *
     * @return The method declarations.
     */
    public List<JavaSstParserObject> getMethodDeclarations() {
        if (objectClass == JavaSstParserObjectClass.CLASS) {
            return null; // FIXME
        } else {
            throw new ObjectClassException(JavaSstParserObjectClass.CLASS);
        }
    }

    /**
     * Get the variable definitions.
     *
     * @return The variable definitions.
     */
    public Optional<JavaSstParserObject> getVariableDefinitions() {
        if (objectClass == JavaSstParserObjectClass.CLASS) {
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
    public int getIntValue() {
        if (objectClass == JavaSstParserObjectClass.CONSTANT) {
            return intValue;
        } else {
            throw new ObjectClassException(JavaSstParserObjectClass.CONSTANT);
        }
    }

    /**
     * Get the int value.
     *
     * @param intValue The int value.
     */
    public void setIntValue(int intValue) {
        if (objectClass == JavaSstParserObjectClass.CONSTANT) {
            this.intValue = intValue;
        } else {
            throw new ObjectClassException(JavaSstParserObjectClass.CONSTANT);
        }
    }

    /**
     * Get the parameter list.
     *
     * @return The parameter list.
     */
    public JavaSstParserObject getParameterList() {
        if (objectClass == JavaSstParserObjectClass.PROCEDURE) {
            return null; // FIXME
        } else {
            throw new ObjectClassException(JavaSstParserObjectClass.PROCEDURE);
        }
    }

    /**
     * Add a parameter to the parameter list.
     *
     * @param parameter The parameter.
     */
    public void addParameter(final JavaSstParserObject parameter) {
        if (objectClass == JavaSstParserObjectClass.PROCEDURE) {
            // TODO: Do your thing.
        } else {
            throw new ObjectClassException(JavaSstParserObjectClass.PROCEDURE);
        }
    }

    /**
     * Get the result.
     *
     * @return The result.
     */
    public JavaSstParserObjectType getResult() {
        if (objectClass == JavaSstParserObjectClass.PROCEDURE) {
            return null; // FIXME
        } else {
            throw new ObjectClassException(JavaSstParserObjectClass.PROCEDURE);
        }
    }

    /**
     * Exception thrown if a wrong {@link JavaSstParserObjectClass} has been assumed.
     */
    public class ObjectClassException extends RuntimeException {

        /**
         * Create a new {@link ObjectClassException}.
         *
         * @param expected The {@link JavaSstParserObjectClass}es that would have been valid.
         */
        public ObjectClassException(final JavaSstParserObjectClass... expected) {
            super("Expected class of " + Arrays.toString(expected) + " but was " + objectClass);
        }
    }

}
