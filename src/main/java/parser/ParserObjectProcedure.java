package parser;

import com.sun.org.apache.bcel.internal.generic.ObjectType;

/**
 * A procedure.
 */
public class ParserObjectProcedure extends ParserObject {

    /**
     * The parameter list.
     */
    private final ParserObject parameterList;

    /**
     * The result type.
     */
    private final ObjectType result;

    /**
     * The scope.
     */
    private final ParserObject scope;

    /**
     * Create a new procedure.
     *
     * @param parameterList The parameter list.
     * @param result        The result type.
     * @param scope         The scope.
     */
    public ParserObjectProcedure(final ParserObject parameterList, final ObjectType result, final ParserObject scope) {
        this.parameterList = parameterList;
        this.result = result;
        this.scope = scope;
    }

    @Override
    public ObjectClass getParserObjectClass() {
        return ObjectClass.PROCEDURE;
    }

    /**
     * Get the parameter list.
     *
     * @return The parameter list.
     */
    public ParserObject getParameterList() {
        return parameterList;
    }

    /**
     * Get the result.
     *
     * @return The result.
     */
    public ObjectType getResult() {
        return result;
    }

    /**
     * Get the scope.
     *
     * @return The scope.
     */
    public ParserObject getScope() {
        return scope;
    }
}
