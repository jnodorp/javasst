package parser;

/**
 * A class.
 */
public class ParserObjectClass extends ParserObject {

    /**
     * The token table.
     */
    private final SymbolTable symbolTable;

    /**
     * The variable definitions.
     */
    private ParserObject variableDefinitions;

    /**
     * The method declarations.
     */
    private ParserObject methodDeclarations;

    /**
     * Create a new class.
     *
     * @param symbolTable The token table.
     */
    public ParserObjectClass(final SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public ObjectClass getParserObjectClass() {
        return ObjectClass.CLASS;
    }

    /**
     * Get the variable definitions.
     *
     * @return The variable definitions.
     */
    public ParserObject getVariableDefinitions() {
        return variableDefinitions;
    }

    /**
     * Set the variable definitions.
     *
     * @param variableDefinitions The variable definitions.
     */
    public void setVariableDefinitions(ParserObject variableDefinitions) {
        this.variableDefinitions = variableDefinitions;
    }

    /**
     * Get the method declarations.
     *
     * @return The method declarations.
     */
    public ParserObject getMethodDeclarations() {
        return methodDeclarations;
    }

    /**
     * Set the method declarations.
     *
     * @param methodDeclarations The method declarations.
     */
    public void setMethodDeclarations(ParserObject methodDeclarations) {
        this.methodDeclarations = methodDeclarations;
    }

    /**
     * Get the token table.
     *
     * @return The token table.
     */
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
}
