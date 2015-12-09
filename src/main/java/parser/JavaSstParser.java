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
public class JavaSstParser extends Parser<Symbol, SymbolType> {

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
        String identifier;

        token().is(CLASS).once();

        identifier = token.getIdentifier();
        token().is(IDENT).once();

        // Create the {@link SymbolTable} for the class.
        final SymbolTable oldSymbolTable = symbolTable;
        symbolTable = new SymbolTable(null, oldSymbolTable);

        // Create the {@link ParserObject}.
        final ParserObject p = new ParserObjectClass(null, null, null, null, symbolTable);
        p.setName(identifier);

        oldSymbolTable.setHead(p);

        classBody();

        // Reset {@link SymbolTable} when leaving scope.
        symbolTable = oldSymbolTable;
    }

    /**
     * Class body: &#123; {@link #declarations()} &#125;.
     */
    private void classBody() {
        token().is(CURLY_BRACE_OPEN).once();
        declarations();
        token().is(CURLY_BRACE_CLOSE).once();
    }

    private void constant() {
        token().is(FINAL).once();
        type();

        // Get the constants name.
        final String identifier = token.getIdentifier();

        token().is(IDENT).once();
        token().is(EQUALS).once();
        expression();
        token().is(SEMICOLON).once();

        ParserObject p = new ParserObjectConstant(0); // FIXME: Evaluate expression.
        p.setName(identifier);
        symbolTable.insert(p);
    }

    private void variableDeclaration() {
        type();

        // Get the variables name.
        final String identifier = token.getIdentifier();

        token().is(IDENT).once();
        token().is(SEMICOLON).once();

        ParserObject p = new ParserObjectVariable();
        p.setName(identifier);
        symbolTable.insert(p);
    }

    private void declarations() {
        token().is(FINAL).repeat(this::constant);
        token().is(first("type")).repeat(this::variableDeclaration);
        token().is(first("method_declaration")).repeat(this::methodDeclaration);
    }

    private void methodDeclaration() {
        token().is(PUBLIC).once();
        methodType();

        // Get the method name.
        final String identifier = token.getIdentifier();

        token().is(IDENT).once();
        formalParameters();
        token().is(CURLY_BRACE_OPEN).once();
        token().is(first("local_declaration")).repeat(this::localDeclaration);
        statementSequence();
        token().is(CURLY_BRACE_CLOSE).once();

        ParserObject p = new ParserObjectProcedure(null, null, null);
        p.setName(identifier);
        symbolTable.insert(p);
    }

    private void methodType() {
        token().is(VOID, INT).once();
    }

    private void formalParameters() {
        token().is(PARENTHESIS_OPEN).once();
        token().is(first("fp_section")).optional(() -> {
            fpSection();

            token().is(COMMA).repeat(() -> {
                next();
                fpSection();
            });
        });

        token().is(PARENTHESIS_CLOSE).once();
    }

    private void fpSection() {
        type();
        token().is(IDENT).once();
    }

    private void localDeclaration() {
        type();
        token().is(IDENT).once();
        token().is(SEMICOLON).once();
    }

    private void statementSequence() {
        statement();
        token().is(first("statement")).repeat(this::statement);
    }

    private void statement() {
        // Could be an assignment or a procedure call.
        if (IDENT == token.getType()) {
            next();

            if (PARENTHESIS_OPEN == token.getType()) {
                actualParameters();
                token().is(SEMICOLON).once();
            } else if (EQUALS == token.getType()) {
                token().is(EQUALS).once();
                expression();
                token().is(SEMICOLON).once();
            } else {
                error(PARENTHESIS_OPEN, EQUALS);
            }
        } else if (first("if_statement").contains(token.getType())) {
            ifStatement();
        } else if (first("while_statement").contains(token.getType())) {
            whileStatement();
        } else if (first("return_statement").contains(token.getType())) {
            returnStatement();
        } else {
            error(IDENT, IF, WHILE, RETURN);
        }
    }

    private void type() {
        token().is(INT).once();
    }

    private void internProcedureCall() {
        token().is(IDENT).once();
        actualParameters();
    }

    private void ifStatement() {
        token().is(IF).once();
        token().is(PARENTHESIS_OPEN).once();
        expression();
        token().is(PARENTHESIS_CLOSE).once();
        token().is(CURLY_BRACE_OPEN).once();
        statementSequence();
        token().is(CURLY_BRACE_CLOSE).once();
        token().is(ELSE).once();
        token().is(CURLY_BRACE_OPEN).once();
        statementSequence();
        token().is(CURLY_BRACE_CLOSE).once();
    }

    private void whileStatement() {
        token().is(WHILE).once();
        token().is(PARENTHESIS_OPEN).once();
        expression();
        token().is(PARENTHESIS_CLOSE).once();
        token().is(CURLY_BRACE_OPEN).once();
        statementSequence();
        token().is(CURLY_BRACE_CLOSE).once();
    }

    private void returnStatement() {
        token().is(RETURN).once();
        token().is(first("simple_expression")).optional(this::simpleExpression);
        token().is(SEMICOLON).once();
    }

    private void actualParameters() {
        token().is(PARENTHESIS_OPEN).once();
        token().is(first("expression")).optional(() -> {
            expression();
            token().is(COMMA).repeat(() -> {
                token().is(COMMA).once();
                expression();
            });
        });

        token().is(PARENTHESIS_CLOSE).once();
    }

    private void expression() {
        simpleExpression();
        token().is(EQUALS_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS, LESS_THAN, LESS_THAN_EQUALS).optional(() -> {
            next();
            simpleExpression();
        });
    }

    private void simpleExpression() {
        term();
        token().is(PLUS, MINUS).repeat(() -> {
            next();
            term();
        });
    }

    private void term() {
        factor();
        token().is(TIMES, SLASH).repeat(() -> {
            next();
            factor();
        });
    }

    private void factor() {
        if (IDENT == token.getType()) {
            next();

            // Could be an internal procedure call.
            token().is(PARENTHESIS_OPEN).optional(this::actualParameters);
        } else if (NUMBER == token.getType()) {
            next();
        } else if (PARENTHESIS_OPEN == token.getType()) {
            next();
            expression();
            token().is(PARENTHESIS_CLOSE).once();
        } else if (first("intern_procedure_call").contains(token.getType())) {
            internProcedureCall();
        } else {
            error(IDENT, NUMBER, PARENTHESIS_OPEN);
        }
    }

    @Override
    public void parse() {
        next();
        clazz();
        token().is(EOF).once();
    }

    /**
     * @see #error(List).
     */
    private void error(SymbolType... expected) {
        error(Arrays.asList(expected));
    }

    @Override
    protected void error(final List<SymbolType> expected) {
        String string = "Unexpected token '" + token.getIdentifier() + "'" +
                " of type '" + token.getType() + "'" +
                " at '" + token.getPosition() + "'.";

        if (expected.size() > 0) {
            string += " Expected token of one of the following types: " + expected.toString() + ".";
        }

        LOGGER.log(Level.SEVERE, string);
        throw new RuntimeException();
    }

    /**
     * Get all possible first {@link SymbolType} of the construct c.
     *
     * @param c The construct.
     * @return The possible first token types of c.
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
