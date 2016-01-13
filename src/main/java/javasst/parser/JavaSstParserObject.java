package javasst.parser;

import javasst.scanner.JavaSstToken;
import parser.SymbolTable;
import scanner.Token;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link JavaSstParserObject}s (e.g. variables, constant, etc.).
 */
public class JavaSstParserObject implements parser.ParserObject {

    /**
     * The {@link Token}.
     */
    private final Token token;

    /**
     * The {@link SymbolTable}.
     */
    private final SymbolTable<JavaSstParserObject> symbolTable;

    /**
     * The {@link JavaSstParserObjectClass}.
     */
    private final JavaSstParserObjectClass objectClass;

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
     * @param token       The {@link JavaSstToken}.
     * @param objectClass The {@link JavaSstParserObjectClass}.
     * @param symbolTable The {@link SymbolTable}.
     */
    public JavaSstParserObject(final JavaSstToken token, final JavaSstParserObjectClass objectClass, final SymbolTable<JavaSstParserObject> symbolTable) {
        this.token = token;
        this.objectClass = objectClass;
        this.symbolTable = symbolTable;
    }

    /**
     * Create a new {@link JavaSstParserObject}.
     *
     * @param token       The {@link JavaSstToken}.
     * @param objectClass The {@link JavaSstParserObjectClass}.
     */
    public JavaSstParserObject(final JavaSstToken token, final JavaSstParserObjectClass objectClass) {
        this(token, objectClass, null);
    }

    @Override
    public Token getToken() {
        return token;
    }

    @Override
    public String getIdentifier() {
        return token.getIdentifier();
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
        if (objectClass == JavaSstParserObjectClass.CLASS || objectClass == JavaSstParserObjectClass.FUNCTION) {
            return symbolTable;
        } else {
            throw new ObjectClassException(JavaSstParserObjectClass.CLASS, JavaSstParserObjectClass.FUNCTION);
        }
    }

    /**
     * Get the function declarations.
     *
     * @return The function declarations.
     */
    public List<JavaSstParserObject> getFunctionDeclarations() {
        if (objectClass == JavaSstParserObjectClass.CLASS) {
            return symbolTable.getObjects().stream().filter(o -> JavaSstParserObjectClass.FUNCTION == o.getObjectClass()).collect(Collectors.toList());
        } else {
            throw new ObjectClassException(JavaSstParserObjectClass.CLASS);
        }
    }

    /**
     * Get the variable definitions.
     *
     * @return The variable definitions.
     */
    public List<JavaSstParserObject> getVariableDefinitions() {
        if (objectClass == JavaSstParserObjectClass.CLASS) {
            return symbolTable.getObjects().stream().filter(o -> JavaSstParserObjectClass.VARIABLE == o.getObjectClass()).collect(Collectors.toList());
        } else {
            throw new ObjectClassException(JavaSstParserObjectClass.CLASS);
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
    public List<JavaSstParserObject> getParameterList() {
        if (objectClass == JavaSstParserObjectClass.FUNCTION) {
            return symbolTable.getObjects().stream().filter(o -> JavaSstParserObjectClass.PARAMETER == o.getObjectClass()).collect(Collectors.toList());
        } else {
            throw new ObjectClassException(JavaSstParserObjectClass.FUNCTION);
        }
    }

    /**
     * Get the result.
     *
     * @return The result.
     */
    public JavaSstParserObjectType getResult() {
        if (objectClass == JavaSstParserObjectClass.FUNCTION) {
            return null; // FIXME
        } else {
            throw new ObjectClassException(JavaSstParserObjectClass.FUNCTION);
        }
    }

    @Override
    public String toString() {
        return token.toString();
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
