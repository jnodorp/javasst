package parser;

import scanner.Symbol;
import scanner.SymbolType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static scanner.SymbolType.*;

/**
 * A parser for Java SST.
 */
public class JavaSstParser extends Parser {

    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(JavaSstParser.class.getName());

    /**
     * The {@link SymbolTable}.
     */
    private SymbolTable symbolTable;

    /**
     * Create a new {@link Parser} based on the given scanner.
     *
     * @param scanner The scanner.
     */
    public JavaSstParser(final Iterator<Symbol> scanner) {
        super(scanner);

        this.symbolTable = new SymbolTable(null, null);
    }

    /**
     * Class: {@code class} {@code identifier} {@link #classBody()}.
     */
    private void clazz() {
        symbol().is(CLASS).once();
        final String className = symbol.getIdentifier();
        symbol().is(IDENT).once();

        // Create the {@link SymbolTable} for the class.
        final SymbolTable oldSymbolTable = symbolTable;
        symbolTable = new SymbolTable(null, oldSymbolTable);

        // Create the {@link ParserObject}.
        final ParserObject p = new ParserObjectClass(null, null, null, null, symbolTable);
        p.setName(className);

        oldSymbolTable.setHead(p);

        classBody();

        // Reset {@link SymbolTable} when leaving scope.
        symbolTable = oldSymbolTable;
    }

    /**
     * Class body: &#123; {@link #declarations()} &#125;.
     */
    private void classBody() {
        symbol().is(CURLY_BRACE_OPEN).once();
        declarations();
        symbol().is(CURLY_BRACE_CLOSE).once();
    }

    private void constant() {
        symbol().is(FINAL).once();
        type();

        // Get the constants name.
        final String identifier = symbol.getIdentifier();

        symbol().is(IDENT).once();
        symbol().is(EQUALS).once();
        expression();
        symbol().is(SEMICOLON).once();

        ParserObject p = new ParserObjectConstant(0); // FIXME: Evaluate expression.
        p.setName(identifier);
        symbolTable.insert(p);
    }

    private void variableDeclaration() {
        type();

        // Get the variables name.
        final String identifier = symbol.getIdentifier();

        symbol().is(IDENT).once();
        symbol().is(SEMICOLON).once();

        ParserObject p = new ParserObjectVariable();
        p.setName(identifier);
        symbolTable.insert(p);
    }

    private void declarations() {
        symbol().is(FINAL).repeat(this::constant);
        symbol().is(first("type")).repeat(this::variableDeclaration);
        symbol().is(first("method_declaration")).repeat(this::methodDeclaration);
    }

    private void methodDeclaration() {
        symbol().is(PUBLIC).once();
        methodType();

        // Get the method name.
        final String identifier = symbol.getIdentifier();

        symbol().is(IDENT).once();
        formalParameters();
        symbol().is(CURLY_BRACE_OPEN).once();
        symbol().is(first("local_declaration")).repeat(this::localDeclaration);
        statementSequence();
        symbol().is(CURLY_BRACE_CLOSE).once();

        ParserObject p = new ParserObjectProcedure(null, null, null);
        p.setName(identifier);
        symbolTable.insert(p);
    }

    private void methodType() {
        if (VOID == symbol.getType()) {
            next();
        } else if (first("type").contains(symbol.getType())) {
            type();
        } else {
            error(VOID, INT);
        }
    }

    private void formalParameters() {
        symbol().is(PARENTHESIS_OPEN).once();
        symbol().is(first("fp_section")).optional(() -> {
            fpSection();

            symbol().is(COMMA).repeat(() -> {
                next();
                fpSection();
            });
        });

        symbol().is(PARENTHESIS_CLOSE).once();
    }

    private void fpSection() {
        type();
        symbol().is(IDENT).once();
    }

    private void localDeclaration() {
        type();
        symbol().is(IDENT).once();
        symbol().is(SEMICOLON).once();
    }

    private void statementSequence() {
        statement();
        symbol().is(first("statement")).repeat(this::statement);
    }

    private void statement() {
        // Could be an assignment or a procedure call.
        if (IDENT == symbol.getType()) {
            next();

            if (PARENTHESIS_OPEN == symbol.getType()) {
                actualParameters();
                symbol().is(SEMICOLON).once();
            } else if (EQUALS == symbol.getType()) {
                symbol().is(EQUALS).once();
                expression();
                symbol().is(SEMICOLON).once();
            } else {
                error(PARENTHESIS_OPEN, EQUALS);
            }
        } else if (first("if_statement").contains(symbol.getType())) {
            ifStatement();
        } else if (first("while_statement").contains(symbol.getType())) {
            whileStatement();
        } else if (first("return_statement").contains(symbol.getType())) {
            returnStatement();
        } else {
            error(IDENT, IF, WHILE, RETURN);
        }
    }

    private void type() {
        symbol().is(INT).once();
    }

    private void internProcedureCall() {
        symbol().is(IDENT).once();
        actualParameters();
    }

    private void ifStatement() {
        symbol().is(IF).once();
        symbol().is(PARENTHESIS_OPEN).once();
        expression();
        symbol().is(PARENTHESIS_CLOSE).once();
        symbol().is(CURLY_BRACE_OPEN).once();
        statementSequence();
        symbol().is(CURLY_BRACE_CLOSE).once();
        symbol().is(ELSE).once();
        symbol().is(CURLY_BRACE_OPEN).once();
        statementSequence();
        symbol().is(CURLY_BRACE_CLOSE).once();
    }

    private void whileStatement() {
        symbol().is(WHILE).once();
        symbol().is(PARENTHESIS_OPEN).once();
        expression();
        symbol().is(PARENTHESIS_CLOSE).once();
        symbol().is(CURLY_BRACE_OPEN).once();
        statementSequence();
        symbol().is(CURLY_BRACE_CLOSE).once();
    }

    private void returnStatement() {
        symbol().is(RETURN).once();
        symbol().is(first("simple_expression")).optional(this::simpleExpression);
        symbol().is(SEMICOLON).once();
    }

    private void actualParameters() {
        symbol().is(PARENTHESIS_OPEN).once();
        symbol().is(first("expression")).optional(() -> {
            expression();
            symbol().is(COMMA).repeat(() -> {
                symbol().is(COMMA).once();
                expression();
            });
        });

        symbol().is(PARENTHESIS_CLOSE).once();
    }

    private void expression() {
        simpleExpression();
        symbol().is(EQUALS_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS, LESS_THAN, LESS_THAN_EQUALS).optional(() -> {
            next();
            simpleExpression();
        });
    }

    private void simpleExpression() {
        term();
        symbol().is(PLUS, MINUS).repeat(() -> {
            next();
            term();
        });
    }

    private void term() {
        factor();
        symbol().is(TIMES, SLASH).repeat(() -> {
            next();
            factor();
        });
    }

    private void factor() {
        if (IDENT == symbol.getType()) {
            next();

            // Could be an internal procedure call.
            symbol().is(PARENTHESIS_OPEN).optional(this::actualParameters);
        } else if (NUMBER == symbol.getType()) {
            next();
        } else if (PARENTHESIS_OPEN == symbol.getType()) {
            next();
            expression();
            symbol().is(PARENTHESIS_CLOSE).once();
        } else if (first("intern_procedure_call").contains(symbol.getType())) {
            internProcedureCall();
        } else {
            error(IDENT, NUMBER, PARENTHESIS_OPEN);
        }
    }

    @Override
    public void parse() {
        next();
        clazz();
        symbol().is(EOF).once();
    }

    private void error(SymbolType... expected) {
        error(Arrays.asList(expected));
    }

    @Override
    protected void error(final List<SymbolType> expected) {
        String string = "Unexpected symbol '" + symbol.getIdentifier() + "'" +
                " of type '" + symbol.getType() + "'" +
                " at '" + symbol.getPosition() + "'.";

        if (expected.size() > 0) {
            string += " Expected symbol of one of the following types: " + expected.toString() + ".";
        }

        LOGGER.log(Level.SEVERE, string);
        throw new RuntimeException();
    }

    /**
     * Get all possible first {@link SymbolType} of the construct c.
     *
     * @param c The construct.
     * @return The possible first symbol types of c.
     */
    private List<SymbolType> first(String c) {
        final ArrayList<SymbolType> result = new ArrayList<>();

        switch (c) {
            case "type":
                result.add(INT);
                break;
            case "method_declaration":
                result.addAll(first("method_head"));
                break;
            case "method_head":
                result.add(PUBLIC);
                break;
            case "fp_section":
                result.addAll(first("type"));
                break;
            case "local_declaration":
                result.addAll(first("type"));
                break;
            case "statement":
                result.addAll(first("assignment"));
                result.addAll(first("procedure_call"));
                result.addAll(first("if_statement"));
                result.addAll(first("while_statement"));
                result.addAll(first("return_statement"));
                break;
            case "assignment":
                result.add(IDENT);
                break;
            case "procedure_call":
                result.addAll(first("intern_procedure_call"));
                break;
            case "if_statement":
                result.add(IF);
                break;
            case "while_statement":
                result.add(WHILE);
                break;
            case "return_statement":
                result.add(RETURN);
                break;
            case "intern_procedure_call":
                result.add(IDENT);
                break;
            case "simple_expression":
                result.addAll(first("term"));
                break;
            case "term":
                result.addAll(first("factor"));
                break;
            case "factor":
                result.add(IDENT);
                result.add(NUMBER);
                result.add(PARENTHESIS_OPEN);
                result.addAll(first("intern_procedure_call"));
                break;
            case "expression":
                result.addAll(first("simple_expression"));
                break;
            default:
                throw new IllegalArgumentException("Invalid construct: '" + c + "'");
        }

        return result;
    }
}
