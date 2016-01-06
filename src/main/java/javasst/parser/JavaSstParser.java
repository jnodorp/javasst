package javasst.parser;

import ast.Ast;
import ast.Node;
import exceptions.UnknownSymbolException;
import javasst.ast.JavaSstNode;
import javasst.ast.JavaSstNodeClass;
import javasst.ast.JavaSstNodeSubclass;
import javasst.ast.JavaSstNodeType;
import javasst.scanner.JavaSstToken;
import javasst.scanner.JavaSstTokenType;
import parser.Parser;
import parser.SymbolTable;
import scanner.Scanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javasst.scanner.JavaSstTokenType.*;

/**
 * A parser for Java SST.
 */
public class JavaSstParser extends Parser<JavaSstToken, JavaSstTokenType, JavaSstParserObject, JavaSstNode> {

    /**
     * The current {@link JavaSstParserObject}.
     */
    private JavaSstParserObject parserObject;

    /**
     * The current {@link JavaSstNode}.
     */
    private JavaSstNode node;

    /**
     * Create a new {@link Parser} based on the given scanner.
     *
     * @param scanner The scanner.
     */
    public JavaSstParser(final Scanner<JavaSstToken, JavaSstTokenType> scanner) {
        super(scanner);
        this.symbolTable = new SymbolTable<>(null);
    }

    /**
     * Class: {@code class} {@link JavaSstTokenType#IDENT} {@link #classBody()}.
     *
     * @return The class node.
     */
    private JavaSstNode clazz() {
        node = new JavaSstNode();
        node.setClazz(JavaSstNodeClass.CLASS);

        scope(() -> {
            token().is(CLASS).once();
            final String identifier = token().is(IDENT).and().getIdentifier();

            // Create the {@link ParserObject}.
            parserObject = new JavaSstParserObject(JavaSstParserObjectClass.CLASS, symbolTable);
            parserObject.setIdentifier(identifier);

            classBody();
        });

        symbolTable.add(parserObject);

        // Finish AST.
        node.setObject(parserObject);

        return node;
    }

    /**
     * Class body: &#123; {@link #constant()} {@link #variableDeclaration()} {@link #methodDeclaration()} &#125;.
     */
    private void classBody() {
        token().is(CURLY_BRACE_OPEN).once();
        token().is(FINAL).repeat(this::constant);
        token().is(first("variable_declaration")).repeat(this::variableDeclaration);
        token().is(first("method_declaration")).repeat(this::methodDeclaration);
        token().is(CURLY_BRACE_CLOSE).once();
    }

    /**
     * Constant: {@code final} {@link #type()} {@link JavaSstTokenType#IDENT} {@code =} {@link JavaSstTokenType#NUMBER}
     * {@code ;}.
     * <p>
     * TODO: Add handling for non integer constants.
     * TODO: Add handling for calculated constants.
     */
    private void constant() {
        // Verify syntax.
        token().is(FINAL).once();
        type();
        final String identifier = token().is(IDENT).and().getIdentifier();
        token().is(EQUALS).once();
        final int integerValue = Integer.parseInt(token().is(NUMBER).and().getIdentifier());
        token().is(SEMICOLON).once();

        // Build symbol table.
        JavaSstParserObject p = new JavaSstParserObject(JavaSstParserObjectClass.CONSTANT);
        p.setIdentifier(identifier);
        p.setIntValue(integerValue);
        symbolTable.add(p);

        // Build AST.
        JavaSstNode n = new JavaSstNode();
        n.setClazz(JavaSstNodeClass.CONSTANT);
        n.setObject(p);
        n.setType(JavaSstNodeType.INTEGER);

        if (node.getLeft().isPresent()) {
            Node<JavaSstNodeClass, JavaSstNodeSubclass, JavaSstNodeType> parent = node.getLeft().get();
            while (parent.getLink().isPresent()) {
                parent = parent.getLink().get();
            }

            parent.setLink(n);
        } else {
            node.setLeft(n);
        }
    }

    /**
     * Variable declaration: {@link #type()} {@link JavaSstTokenType#IDENT} {@code ;}.
     * <p>
     * TODO: Add handling for non integer variable declarations.
     *
     * @return The variable declaration node.
     */
    private JavaSstNode variableDeclaration() {
        // Verify syntax.
        type();
        final String identifier = token().is(IDENT).and().getIdentifier();
        token().is(SEMICOLON).once();

        // Build symbol table.
        JavaSstParserObject p = new JavaSstParserObject(JavaSstParserObjectClass.VARIABLE);
        p.setIdentifier(identifier);
        symbolTable.add(p);

        // Build AST.
        JavaSstNode node = new JavaSstNode();
        node.setClazz(JavaSstNodeClass.VARIABLE_DECLARATION);
        node.setObject(p);
        node.setType(JavaSstNodeType.INTEGER);

        return node;
    }

    /**
     * Method declaration: {@code public} ... {@link JavaSstTokenType#IDENT} ...
     * <p>
     * TODO: Finish documentation.
     * TODO: Add handling for non integer method declarations.
     *
     * @return The method declaration node.
     */
    private JavaSstNode methodDeclaration() {
        // Verify syntax.
        token().is(PUBLIC).once();
        token().is(VOID, INT).once();

        final String identifier = token().is(IDENT).and().getIdentifier();
        scope(() -> {
            formalParameters();
            token().is(CURLY_BRACE_OPEN).once();
            token().is(first("variable_declaration")).repeat(this::variableDeclaration);
            statementSequence();
            token().is(CURLY_BRACE_CLOSE).once();
        });

        // Build symbol table.
        JavaSstParserObject p = new JavaSstParserObject(JavaSstParserObjectClass.PROCEDURE);
        p.setIdentifier(identifier);
        symbolTable.add(p);

        // Build AST.
        JavaSstNode node = new JavaSstNode();
        node.setClazz(JavaSstNodeClass.METHOD);
        node.setObject(p);
        node.setType(JavaSstNodeType.INTEGER);

        return node;
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
        final JavaSstNode node = new JavaSstNode();

        if (IDENT == token.getType()) {
            final String identifier = token.getIdentifier();
            next();

            // Could be an internal procedure call.
            if (PARENTHESIS_OPEN == token.getType()) {
                actualParameters();
            } else {
                node.setClazz(JavaSstNodeClass.VARIABLE);
                node.setObject(symbolTable.get(identifier).orElseThrow(UnknownSymbolException::new));
            }
        } else if (NUMBER == token.getType()) {
            node.setClazz(JavaSstNodeClass.NUMBER);
            node.setConstant(Integer.parseInt(token.getIdentifier()));
            next();
        } else if (PARENTHESIS_OPEN == token.getType()) {
            next();
            expression();
            token().is(PARENTHESIS_CLOSE).once();
        } else {
            error(IDENT, NUMBER, PARENTHESIS_OPEN);
        }
    }

    @Override
    public Ast<JavaSstNode> parse() {
        next();
        ast.setRoot(clazz());
        // token().is(EOF).once();

        return ast;
    }

    /**
     * Switch to the error state.
     *
     * @param expected A list of expected tokens.
     * @see #error(List)
     */
    private void error(JavaSstTokenType... expected) {
        error(Arrays.asList(expected));
    }

    /**
     * Get all possible first {@link JavaSstTokenType} of the construct c.
     *
     * @param c The construct.
     * @return The possible first token types of c.
     */
    private List<JavaSstTokenType> first(String c) {
        final ArrayList<JavaSstTokenType> result = new ArrayList<>();

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
            case "variable_declaration":
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
                result.add(IDENT);
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
