package javasst.parser;

import javasst.JavaSstType;
import javasst.scanner.JavaSstToken;
import parser.SymbolTable;
import scanner.Token;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link JavaSstParserObjectFuture}s (e.g. variables, constant, etc.).
 */
public class JavaSstParserObjectFuture extends JavaSstParserObject {

    /**
     * Create a new {@link JavaSstParserObjectFuture} with a {@link SymbolTable}.
     *
     * @param identifier  The identifier.
     * @param symbolTable The {@link SymbolTable}.
     */
    public JavaSstParserObjectFuture(final String identifier, final SymbolTable<JavaSstParserObject> symbolTable) {
        super(new JavaSstToken(identifier, null, 0, 0, null), JavaSstType.FUNCTION, symbolTable);
    }

    @Override
    public Token getToken() {
        return super.getSymbolTable().object(super.getIdentifier()).orElseThrow(UnknownError::new).getToken();
    }

    @Override
    public String getIdentifier() {
        return super.getSymbolTable().object(super.getIdentifier()).orElseThrow(UnknownError::new).getToken().getIdentifier();
    }

    @Override
    public JavaSstType getObjectClass() {
        return super.getSymbolTable().object(super.getIdentifier()).orElseThrow(UnknownError::new).getObjectClass();
    }

    @Override
    public JavaSstType getParserType() {
        return super.getSymbolTable().object(super.getIdentifier()).orElseThrow(UnknownError::new).getParserType();
    }

    @Override
    public SymbolTable<JavaSstParserObject> getSymbolTable() {
        if (getObjectClass() == JavaSstType.CLASS || getObjectClass() == JavaSstType.FUNCTION) {
            return super.getSymbolTable().object(super.getIdentifier()).orElseThrow(UnknownError::new).getSymbolTable();
        } else {
            throw new ObjectClassException(JavaSstType.CLASS, JavaSstType.FUNCTION);
        }
    }

    @Override
    public List<JavaSstParserObject> getFunctionDeclarations() {
        if (getObjectClass() == JavaSstType.CLASS) {
            return super.getSymbolTable().object(super.getIdentifier()).orElseThrow(UnknownError::new).getSymbolTable()
                    .getObjects().stream().filter(o -> JavaSstType.FUNCTION == o.getObjectClass()).collect(Collectors.toList());
        } else {
            throw new ObjectClassException(JavaSstType.CLASS);
        }
    }

    @Override
    public List<JavaSstParserObject> getVariableDefinitions() {
        if (getObjectClass() == JavaSstType.CLASS) {
            return super.getSymbolTable().object(super.getIdentifier()).orElseThrow(UnknownError::new).getSymbolTable()
                    .getObjects().stream().filter(o -> JavaSstType.VARIABLE == o.getObjectClass()).collect(Collectors.toList());
        } else {
            throw new ObjectClassException(JavaSstType.CLASS);
        }
    }

    @Override
    public int getIntValue() {
        if (getObjectClass() == JavaSstType.CONSTANT) {
            return super.getSymbolTable().object(super.getIdentifier()).orElseThrow(UnknownError::new).getIntValue();
        } else {
            throw new ObjectClassException(JavaSstType.CONSTANT);
        }
    }

    @Override
    public List<JavaSstParserObject> getParameterList() {
        if (getObjectClass() == JavaSstType.FUNCTION) {
            return super.getSymbolTable().object(super.getIdentifier()).orElseThrow(UnknownError::new).getSymbolTable()
                    .getObjects().stream().filter(o -> JavaSstType.PARAMETER == o.getObjectClass()).collect(Collectors.toList());
        } else {
            throw new ObjectClassException(JavaSstType.FUNCTION);
        }
    }

    @Override
    public JavaSstType getResult() {
        if (getObjectClass() == JavaSstType.FUNCTION) {
            return null; // FIXME
        } else {
            throw new ObjectClassException(JavaSstType.FUNCTION);
        }
    }

    @Override
    public String toString() {
        return super.getSymbolTable().object(super.getIdentifier()).orElseThrow(UnknownError::new).getToken().toString();
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
            super("Expected class of " + Arrays.toString(expected) + " but was " + getObjectClass());
        }
    }
}
