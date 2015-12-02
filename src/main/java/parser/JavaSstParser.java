package parser;

import scanner.Symbol;
import scanner.SymbolType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A parser for Java SST.
 */
public class JavaSstParser implements Parser {

    /**
     * The scanner.
     */
    private final Iterator<Symbol> scanner;

    /**
     * The {@link Symbol}.
     */
    private Symbol symbol;

    /**
     * The {@link SymbolTable}.
     */
    private SymbolTable symbolTable;

    /**
     * Create a new {@link Parser} based on the given scanner.
     *
     * @param scanner The scanner.
     */
    public JavaSstParser(Iterator<Symbol> scanner) {
        this.scanner = scanner;
    }

    private void clazz() {
        assertCurrent(SymbolType.CLASS);
        final String className = symbol.getIdentifier();
        assertCurrent(SymbolType.IDENT);

        ParserObject p = new ParserObjectClass(null, null, null, null, null);
        p.setName(className);
        p.setNext(null);
        symbolTable = new SymbolTable(p, null);

        classBody();
    }

    private void classBody() {
        assertCurrent(SymbolType.CURLY_BRACE_OPEN);
        declarations();
        assertCurrent(SymbolType.CURLY_BRACE_CLOSE);
    }

    private void declarations() {
        while (SymbolType.FINAL == symbol.getType()) {
            assertCurrent(SymbolType.FINAL);
            type();
            assertCurrent(SymbolType.IDENT);
            assertCurrent(SymbolType.EQUALS);
            expression();
            assertCurrent(SymbolType.SEMICOLON);
        }

        while (first("type").contains(symbol.getType())) {
            type();
            assertCurrent(SymbolType.IDENT);
            assertCurrent(SymbolType.SEMICOLON);
        }

        while (first("method_declaration").contains(symbol.getType())) {
            methodDeclaration();
        }
    }

    private void methodDeclaration() {
        methodHead();
        methodBody();
    }

    private void methodHead() {
        assertCurrent(SymbolType.PUBLIC);
        methodType();
        assertCurrent(SymbolType.IDENT);
        formalParameters();
    }

    private void methodType() {
        if (SymbolType.VOID == symbol.getType()) {
            assertCurrent(SymbolType.VOID);
        } else if (first("type").contains(symbol.getType())) {
            type();
        } else {
            error(SymbolType.VOID);
        }
    }

    private void formalParameters() {
        assertCurrent(SymbolType.PARENTHESES_OPEN);

        if (first("fp_section").contains(symbol.getType())) {
            fpSection();
            while (SymbolType.COMMA == symbol.getType()) {
                next();
                fpSection();
            }
        }

        assertCurrent(SymbolType.PARENTHESIS_CLOSE);
    }

    private void fpSection() {
        type();
        assertCurrent(SymbolType.IDENT);
    }

    private void methodBody() {
        assertCurrent(SymbolType.CURLY_BRACE_OPEN);

        while (first("local_declaration").contains(symbol.getType())) {
            localDeclaration();
        }

        statementSequence();
        assertCurrent(SymbolType.CURLY_BRACE_CLOSE);
    }

    private void localDeclaration() {
        type();
        assertCurrent(SymbolType.IDENT);
        assertCurrent(SymbolType.SEMICOLON);
    }

    private void statementSequence() {
        statement();

        while (first("statement").contains(symbol.getType())) {
            statement();
        }
    }

    private void statement() {
        // Could be an assignment or a procedure call.
        if (SymbolType.IDENT == symbol.getType()) {
            assertCurrent(SymbolType.IDENT);

            if (SymbolType.PARENTHESES_OPEN == symbol.getType()) {
                actualParameters();
                assertCurrent(SymbolType.SEMICOLON);
            } else if (SymbolType.EQUALS == symbol.getType()) {
                assertCurrent(SymbolType.EQUALS);
                expression();
                assertCurrent(SymbolType.SEMICOLON);
            } else {
                error(SymbolType.PARENTHESES_OPEN, SymbolType.EQUALS);
            }
        } else if (first("if_statement").contains(symbol.getType())) {
            ifStatement();
        } else if (first("while_statement").contains(symbol.getType())) {
            whileStatement();
        } else if (first("return_statement").contains(symbol.getType())) {
            returnStatement();
        } else {
            error(SymbolType.IDENT, SymbolType.IF, SymbolType.WHILE, SymbolType.RETURN);
        }
    }

    private void type() {
        assertCurrent(SymbolType.INT);
    }

    private void internProcedureCall() {
        assertCurrent(SymbolType.IDENT);
        actualParameters();
    }

    private void ifStatement() {
        assertCurrent(SymbolType.IF);
        assertCurrent(SymbolType.PARENTHESES_OPEN);
        expression();
        assertCurrent(SymbolType.PARENTHESIS_CLOSE);
        assertCurrent(SymbolType.CURLY_BRACE_OPEN);
        statementSequence();
        assertCurrent(SymbolType.CURLY_BRACE_CLOSE);
        assertCurrent(SymbolType.ELSE);
        assertCurrent(SymbolType.CURLY_BRACE_OPEN);
        statementSequence();
        assertCurrent(SymbolType.CURLY_BRACE_CLOSE);
    }

    private void whileStatement() {
        assertCurrent(SymbolType.WHILE);
        assertCurrent(SymbolType.PARENTHESES_OPEN);
        expression();
        assertCurrent(SymbolType.PARENTHESIS_CLOSE);
        assertCurrent(SymbolType.CURLY_BRACE_OPEN);
        statementSequence();
        assertCurrent(SymbolType.CURLY_BRACE_CLOSE);
    }

    private void returnStatement() {
        assertCurrent(SymbolType.RETURN);

        if (first("simple_expression").contains(symbol.getType())) {
            simpleExpression();
        }

        assertCurrent(SymbolType.SEMICOLON);
    }

    private void actualParameters() {
        assertCurrent(SymbolType.PARENTHESES_OPEN);

        if (first("expression").contains(symbol.getType())) {
            expression();

            while (SymbolType.COMMA == symbol.getType()) {
                assertCurrent(SymbolType.COMMA);
                expression();
            }
        }

        assertCurrent(SymbolType.PARENTHESIS_CLOSE);
    }

    private void expression() {
        simpleExpression();

        if (SymbolType.EQUALS_EQUALS == symbol.getType()) {
            assertCurrent(SymbolType.EQUALS_EQUALS);
            simpleExpression();
        } else if (SymbolType.GREATER_THAN == symbol.getType()) {
            assertCurrent(SymbolType.GREATER_THAN);
            simpleExpression();
        } else if (SymbolType.GREATER_THAN_EQUALS == symbol.getType()) {
            assertCurrent(SymbolType.GREATER_THAN_EQUALS);
            simpleExpression();
        } else if (SymbolType.LESS_THAN == symbol.getType()) {
            assertCurrent(SymbolType.LESS_THAN);
            simpleExpression();
        } else if (SymbolType.LESS_THAN_EQUALS == symbol.getType()) {
            assertCurrent(SymbolType.LESS_THAN_EQUALS);
            simpleExpression();
        }
    }

    private void simpleExpression() {
        term();

        while (SymbolType.PLUS == symbol.getType() || SymbolType.MINUS == symbol.getType()) {
            next();
            term();
        }
    }

    private void term() {
        factor();

        while (SymbolType.TIMES == symbol.getType() || SymbolType.SLASH == symbol.getType()) {
            next();
            factor();
        }
    }

    private void factor() {
        if (SymbolType.IDENT == symbol.getType()) {
            assertCurrent(SymbolType.IDENT);

            // Could be an internal procedure call.
            if (SymbolType.PARENTHESES_OPEN == symbol.getType()) {
                actualParameters();
            }
        } else if (SymbolType.NUMBER == symbol.getType()) {
            assertCurrent(SymbolType.NUMBER);
        } else if (SymbolType.PARENTHESES_OPEN == symbol.getType()) {
            assertCurrent(SymbolType.PARENTHESES_OPEN);
            expression();
            assertCurrent(SymbolType.PARENTHESIS_CLOSE);
        } else if (first("intern_procedure_call").contains(symbol.getType())) {
            internProcedureCall();
        } else {
            error(SymbolType.IDENT, SymbolType.NUMBER, SymbolType.PARENTHESES_OPEN);
        }
    }

    @Override
    public void parse() {
        next();
        clazz();
        assertCurrent(SymbolType.EOF);
    }

    /**
     * Set current to the next {@link Symbol}.
     */
    private void next() {
        this.symbol = scanner.next();
    }

    private void error(SymbolType... expected) {
        String string = "Unexpected symbol '" + symbol.getIdentifier() + "'" +
                " of type '" + symbol.getType() + "'" +
                " at '" + symbol.getPosition() + "'.";

        if (expected.length == 0) {
            string += " Expected symbol of one of the following types: " + Arrays.toString(expected) + ".";
        }

        Logger.getLogger(JavaSstParser.class.getName()).log(Level.SEVERE, string);
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
                result.add(SymbolType.INT);
                break;
            case "method_declaration":
                result.addAll(first("method_head"));
                break;
            case "method_head":
                result.add(SymbolType.PUBLIC);
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
                result.add(SymbolType.IDENT);
                break;
            case "procedure_call":
                result.addAll(first("intern_procedure_call"));
                break;
            case "if_statement":
                result.add(SymbolType.IF);
                break;
            case "while_statement":
                result.add(SymbolType.WHILE);
                break;
            case "return_statement":
                result.add(SymbolType.RETURN);
                break;
            case "intern_procedure_call":
                result.add(SymbolType.IDENT);
                break;
            case "simple_expression":
                result.addAll(first("term"));
                break;
            case "term":
                result.addAll(first("factor"));
                break;
            case "factor":
                result.add(SymbolType.IDENT);
                result.add(SymbolType.NUMBER);
                result.add(SymbolType.PARENTHESES_OPEN);
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

    /**
     * Assert, that the current {@link Symbol} has a given {@link SymbolType}. If true, the {@link #next()} method is
     * called. Otherwise the {@link #error(SymbolType...)} method is called.
     *
     * @param expected The expected {@link SymbolType}.
     */
    private void assertCurrent(SymbolType expected) {
        if (expected == symbol.getType()) {
            next();
        } else {
            error(expected);
        }
    }
}
