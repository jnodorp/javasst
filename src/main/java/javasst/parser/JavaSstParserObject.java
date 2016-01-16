package javasst.parser;

import javasst.JavaSstType;
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
     * The class.
     */
    private final JavaSstType objectClass;

    /**
     * The type.
     */
    private JavaSstType type;

    /**
     * The int value.
     */
    private int intValue;

    /**
     * Create a new {@link JavaSstParserObject} with a {@link SymbolTable}.
     *
     * @param token       The {@link JavaSstToken}.
     * @param objectClass The class.
     * @param symbolTable The {@link SymbolTable}.
     */
    public JavaSstParserObject(final JavaSstToken token, final JavaSstType objectClass, final SymbolTable<JavaSstParserObject> symbolTable) {
        this.token = token;
        this.objectClass = objectClass;
        this.symbolTable = symbolTable;
    }

    /**
     * Create a new {@link JavaSstParserObject}.
     *
     * @param token       The {@link JavaSstToken}.
     * @param objectClass The class.
     */
    public JavaSstParserObject(final JavaSstToken token, final JavaSstType objectClass) {
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
     * Get the {@link JavaSstType}.
     *
     * @return The {@link JavaSstType}.
     */
    public JavaSstType getObjectClass() {
        return objectClass;
    }

    /**
     * Get the {@link JavaSstType}.
     *
     * @return The {@link JavaSstType}.
     */
    public JavaSstType getParserType() {
        return type;
    }

    /**
     * Set the {@link JavaSstType}.
     *
     * @param type The {@link JavaSstType}.
     */
    public void setType(final JavaSstType type) {
        this.type = type;
    }

    /**
     * Get the {@link SymbolTable}.
     *
     * @return The {@link SymbolTable}.
     */
    public SymbolTable<JavaSstParserObject> getSymbolTable() {
        if (objectClass == JavaSstType.CLASS || objectClass == JavaSstType.FUNCTION) {
            return symbolTable;
        } else {
            throw new ObjectClassException(JavaSstType.CLASS, JavaSstType.FUNCTION);
        }
    }

    /**
     * Get the function declarations.
     *
     * @return The function declarations.
     */
    public List<JavaSstParserObject> getFunctionDeclarations() {
        if (objectClass == JavaSstType.CLASS) {
            return symbolTable.getObjects().stream().filter(o -> JavaSstType.FUNCTION == o.getObjectClass()).collect(Collectors.toList());
        } else {
            throw new ObjectClassException(JavaSstType.CLASS);
        }
    }

    /**
     * Get the variable definitions.
     *
     * @return The variable definitions.
     */
    public List<JavaSstParserObject> getVariableDefinitions() {
        if (objectClass == JavaSstType.CLASS) {
            return symbolTable.getObjects().stream().filter(o -> JavaSstType.VARIABLE == o.getObjectClass()).collect(Collectors.toList());
        } else {
            throw new ObjectClassException(JavaSstType.CLASS);
        }
    }

    /**
     * Get the integer value.
     *
     * @return The integer value.
     */
    public int getIntValue() {
        if (objectClass == JavaSstType.CONSTANT) {
            return intValue;
        } else {
            throw new ObjectClassException(JavaSstType.CONSTANT);
        }
    }

    /**
     * Get the int value.
     *
     * @param intValue The int value.
     */
    public void setIntValue(int intValue) {
        if (objectClass == JavaSstType.CONSTANT) {
            this.intValue = intValue;
        } else {
            throw new ObjectClassException(JavaSstType.CONSTANT);
        }
    }

    /**
     * Get the parameter list.
     *
     * @return The parameter list.
     */
    public List<JavaSstParserObject> getParameterList() {
        if (objectClass == JavaSstType.FUNCTION) {
            return symbolTable.getObjects().stream().filter(o -> JavaSstType.PARAMETER == o.getObjectClass()).collect(Collectors.toList());
        } else {
            throw new ObjectClassException(JavaSstType.FUNCTION);
        }
    }

    /**
     * Get the result.
     *
     * @return The result.
     */
    public JavaSstType getResult() {
        if (objectClass == JavaSstType.FUNCTION) {
            return null; // FIXME
        } else {
            throw new ObjectClassException(JavaSstType.FUNCTION);
        }
    }

    @Override
    public String toString() {
        return token.toString();
    }

    /**
     * Exception thrown if a wrong {@link JavaSstType} has been assumed.
     */
    public class ObjectClassException extends RuntimeException {

        /**
         * Create a new {@link ObjectClassException}.
         *
         * @param expected The {@link JavaSstType}es that would have been valid.
         */
        public ObjectClassException(final JavaSstType... expected) {
            super("Expected class of " + Arrays.toString(expected) + " but was " + objectClass);
        }
    }
}
