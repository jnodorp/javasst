package parser;

/**
 * A class.
 */
public class ParserObjectClass extends ParserObject {

    /**
     * The variable definitions.
     */
    private final ParserObject variableDefinitions;

    /**
     * The method declarations.
     */
    private final ParserObject methodDeclarations;

    /**
     * The superclasses.
     */
    private final ParserObject superclasses;

    /**
     * The interfaces.
     */
    private final ParserObject interfaces;

    /**
     * The symbol table.
     */
    private final SymbolTable symbolTable;

    /**
     * Create a new class.
     *
     * @param variableDefinitions The variable definitions.
     * @param methodDeclarations  The method declarations.
     * @param superclasses        The superclasses.
     * @param interfaces          The interfaces.
     * @param symbolTable         The symbol table.
     */
    public ParserObjectClass(final ParserObject variableDefinitions, final ParserObject methodDeclarations, final
    ParserObject superclasses, final ParserObject interfaces, final SymbolTable symbolTable) {
        this.variableDefinitions = variableDefinitions;
        this.methodDeclarations = methodDeclarations;
        this.superclasses = superclasses;
        this.interfaces = interfaces;
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
     * Get the method declarations.
     *
     * @return The method declarations.
     */
    public ParserObject getMethodDeclarations() {
        return methodDeclarations;
    }

    /**
     * Get the superclasses.
     *
     * @return The superclasses.
     */
    public ParserObject getSuperclasses() {
        return superclasses;
    }

    /**
     * Get the interfaces.
     *
     * @return The interfaces.
     */
    public ParserObject getInterfaces() {
        return interfaces;
    }

    /**
     * Get the symbol table.
     *
     * @return The symbol table.
     */
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
}
