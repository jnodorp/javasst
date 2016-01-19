package javasst.parser;

import ast.Ast;
import ast.Node;
import javasst.JavaSstType;
import javasst.ast.JavaSstNode;
import javasst.scanner.JavaSstToken;
import parser.Parser;
import parser.SymbolTable;
import scanner.Scanner;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ast.Ast.Position.*;
import static javasst.JavaSstType.*;

/**
 * A parser for Java SST.
 */
public class JavaSstParser extends Parser<JavaSstToken, JavaSstType, JavaSstParserObject, JavaSstNode> {

    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(JavaSstParser.class.getName());

    /**
     * Create a new {@link Parser} based on the given scanner.
     *
     * @param scanner The scanner.
     */
    public JavaSstParser(final Scanner<JavaSstToken, JavaSstType> scanner) {
        super(scanner);
        this.symbolTable = new SymbolTable<>(null);
    }

    /**
     * Class: {@code class} {@link JavaSstType#IDENT} {@link #classBody()}.
     *
     * @return The class node.
     */
    private JavaSstNode clazz() {
        final JavaSstNode n = new JavaSstNode();
        final JavaSstParserObject[] o = new JavaSstParserObject[1];
        n.setClazz(CLASS);

        ast.setRoot(n);
        scope(() -> {
            token(CLASS).once();
            final JavaSstToken token = token(IDENT).once();

            // Create the {@link ParserObject}.
            o[0] = new JavaSstParserObject(token, CLASS, symbolTable);

            classBody();
        });

        // Add object to symbol table.
        symbolTable.add(o[0]);

        // Finish AST.
        n.setObject(o[0]);

        return n;
    }

    /**
     * Class body: &#123; {@link #constant()} {@link #variableDeclaration()} {@link #functionDeclaration()} &#125;.
     */
    private void classBody() {
        token(CURLY_BRACE_OPEN).once();
        token(FINAL).repeat(() -> ast.insert(constant(), LEFT, LINK));
        token(first("variable_declaration")).repeat(this::variableDeclaration);
        token(first("function_declaration")).repeat(() -> ast.insert(functionDeclaration(), RIGHT, LINK));
        token(CURLY_BRACE_CLOSE).once();
    }

    /**
     * Constant: {@code final} {@link #type()} {@link JavaSstType#IDENT} {@code =} {@link JavaSstType#NUMBER}
     * {@code ;}.
     * <p>
     * TODO: Add handling for non integer constants.
     * TODO: Add handling for calculated constants.
     */
    private JavaSstNode constant() {
        // Verify syntax.
        token(FINAL).once();
        type();
        final JavaSstToken token = token(IDENT).once();
        token(EQUALS).once();
        final int integerValue = Integer.parseInt(token(NUMBER).once().getIdentifier());
        token(SEMICOLON).once();

        // Create the object.
        final JavaSstParserObject object = new JavaSstParserObject(token, CONSTANT);
        object.setIntValue(integerValue);
        object.setType(INTEGER);
        symbolTable.add(object);

        // Create the node.
        final JavaSstNode node = new JavaSstNode();
        node.setClazz(CONSTANT);
        node.setObject(object);
        node.setType(INTEGER);
        node.setConstant(integerValue);
        return node;
    }

    /**
     * Variable declaration: {@link #type()} {@link JavaSstType#IDENT} {@code ;}.
     * <p>
     * TODO: Add handling for non integer variable declarations.
     */
    private void variableDeclaration() {
        // Verify syntax.
        type();
        final JavaSstToken token = token(IDENT).once();
        token(SEMICOLON).once();

        // Build symbol table entry.
        final JavaSstParserObject object = new JavaSstParserObject(token, VARIABLE);
        object.setType(INTEGER);
        symbolTable.add(object);
    }

    /**
     * Function declaration: {@code public} ... {@link JavaSstType#IDENT} ...
     * <p>
     * TODO: Finish documentation.
     * TODO: Add handling for non integer function declarations.
     */
    private JavaSstNode functionDeclaration() {
        // Build AST.
        JavaSstNode n = new JavaSstNode();
        n.setClazz(FUNCTION);

        // Verify syntax.
        token(PUBLIC).once();
        final JavaSstToken t = token;
        final String type = token(VOID, INT).once().getIdentifier();

        final JavaSstToken token = token(IDENT).once();
        final List<JavaSstParserObject> parameters = new LinkedList<>();
        SymbolTable[] st = new SymbolTable[1];
        scope(() -> {
            st[0] = symbolTable;
            parameters.addAll(formalParameters());
            parameters.forEach((object) -> {
                try {
                    symbolTable.add(object);
                } catch (SymbolTable.SymbolAlreadyExists symbolAlreadyExists) {
                    LOGGER.log(Level.SEVERE, "Parameter name already used.", symbolAlreadyExists);
                    System.exit(symbolAlreadyExists.hashCode());
                }
            });

            token(CURLY_BRACE_OPEN).once();
            token(first("variable_declaration")).repeat(this::variableDeclaration);

            n.setRight(statementSequence());
            token(CURLY_BRACE_CLOSE).once();
        });

        // Build symbol table.
        @SuppressWarnings("unchecked")
        JavaSstParserObject p = new JavaSstParserObject(token, FUNCTION, st[0]);
        p.setType(INTEGER);

        switch (type) {
            case "void":
                p.setType(VOID);
                n.setType(VOID);
                break;
            case "int":
                p.setType(INTEGER);
                n.setType(INTEGER);
                break;
            default:
                LOGGER.log(Level.SEVERE, "Unknown return type " + t.toString());
                System.exit(-1);
        }

        n.setObject(p);
        try {
            symbolTable.add(p);
        } catch (SymbolTable.SymbolAlreadyExists symbolAlreadyExists) {
            LOGGER.log(Level.SEVERE, "Function name already used.", symbolAlreadyExists);
            System.exit(symbolAlreadyExists.hashCode());
        }

        return n;
    }

    /**
     * TODO: Add handling for non integer parameters.
     */
    private List<JavaSstParserObject> formalParameters() {
        token(PARENTHESIS_OPEN).once();

        List<JavaSstParserObject> parameters = new LinkedList<>();
        token(first("fp_section")).optional(x -> {
            type();
            JavaSstToken token = token(IDENT).once();

            // Build symbol table.
            JavaSstParserObject parameter = new JavaSstParserObject(token, PARAMETER);
            parameter.setType(INTEGER);
            parameters.add(parameter);

            token(COMMA).repeat(() -> {
                next();
                type();
                final JavaSstToken t = token(IDENT).once();

                // Build symbol table.
                final JavaSstParserObject p = new JavaSstParserObject(t, PARAMETER);
                p.setType(INTEGER);
                parameters.add(p);
            });
        });

        token(PARENTHESIS_CLOSE).once();

        return parameters;
    }

    private JavaSstNode statementSequence() {
        final JavaSstNode node = statement();
        final List<JavaSstNode> nodes = new LinkedList<>();
        token(first("statement")).repeat(() -> nodes.add(statement()));

        // Chain statement nodes.
        JavaSstNode current = node;
        for (JavaSstNode n : nodes) {
            current.setLink(n); // Will never happen.
            current = n;
        }

        return node;
    }

    /**
     * TODO: Handle non integer assignments.
     *
     * @return TODO: Documentation.
     */
    private JavaSstNode statement() {
        final JavaSstToken t = token;

        // Could be an assignment or a function call.
        if (IDENT == token.getType()) {
            next();

            if (PARENTHESIS_OPEN == token.getType()) {
                final JavaSstNode node = new JavaSstNode(CALL);
                node.setObject(symbolTable.object(t.getIdentifier()).orElse(
                        new JavaSstParserObjectFuture(t.getIdentifier(), symbolTable)
                ));
                node.setLeft(actualParameters().get());
                token(SEMICOLON).once();
                return node;
            } else if (EQUALS == token.getType()) {
                token(EQUALS).once();
                final JavaSstNode node = new JavaSstNode(ASSIGNMENT, INTEGER);

                final JavaSstNode left = new JavaSstNode(VARIABLE);
                left.setObject(symbolTable.object(t.getIdentifier()).orElseThrow(UnknownError::new));
                node.setLeft(left);
                node.setRight(expression());
                token(SEMICOLON).once();
                return node;
            } else {
                error(PARENTHESIS_OPEN, EQUALS);
            }
        } else if (first("if_statement").contains(token.getType())) {
            return ifStatement();
        } else if (first("while_statement").contains(token.getType())) {
            return whileStatement();
        } else if (first("return_statement").contains(token.getType())) {
            return returnStatement();
        } else {
            error(IDENT, IF, WHILE, RETURN);
        }

        return null;
    }

    private void type() {
        token(INT).once();
    }

    private JavaSstNode ifStatement() {
        token(IF).once();
        final JavaSstNode ifElse = new JavaSstNode(IF_ELSE);
        final JavaSstNode node = new JavaSstNode(IF);
        ifElse.setLeft(node);

        token(PARENTHESIS_OPEN).once();
        node.setLeft(expression());
        token(PARENTHESIS_CLOSE).once();
        token(CURLY_BRACE_OPEN).once();
        node.setRight(statementSequence());
        token(CURLY_BRACE_CLOSE).once();
        token(ELSE).once();
        token(CURLY_BRACE_OPEN).once();
        ifElse.setRight(statementSequence());
        token(CURLY_BRACE_CLOSE).once();

        return ifElse;
    }

    private JavaSstNode whileStatement() {
        token(WHILE).once();
        final JavaSstNode node = new JavaSstNode(WHILE);

        token(PARENTHESIS_OPEN).once();
        node.setLeft(expression());
        token(PARENTHESIS_CLOSE).once();

        token(CURLY_BRACE_OPEN).once();
        node.setRight(statementSequence());
        token(CURLY_BRACE_CLOSE).once();

        return node;
    }

    /**
     * TODO: Handle non integer and void return types.
     *
     * @return TODO: Documentation.
     */
    private JavaSstNode returnStatement() {
        token(RETURN).once();
        final JavaSstNode node = new JavaSstNode(RETURN, VOID);

        token(first("simple_expression")).optional(x -> {
            node.setLeft(simpleExpression());
            node.setType(INTEGER);
        });
        token(SEMICOLON).once();

        return node;
    }

    private Optional<Node<JavaSstType, JavaSstType>> actualParameters() {
        final JavaSstNode node = new JavaSstNode();
        token(PARENTHESIS_OPEN).once();
        token(first("expression")).optional(x -> {
            node.setLink(expression());
            token(COMMA).repeat(() -> {
                token(COMMA).once();
                Node<JavaSstType, JavaSstType> current = node;
                while (current.getLink().isPresent()) {
                    current = current.getLink().get();
                }
                current.setLink(expression());
            });
        });

        token(PARENTHESIS_CLOSE).once();
        return node.getLink();
    }

    private JavaSstNode expression() {
        final JavaSstNode node = simpleExpression();
        final JavaSstNode parent = new JavaSstNode();
        token(EQUALS_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS, LESS_THAN, LESS_THAN_EQUALS).optional(token -> {
            next();
            parent.setClazz(token.getType());
            parent.setRight(simpleExpression());
        });

        if (parent.getClazz() != null) {
            parent.setLeft(node);
            return parent;
        }

        return node;
    }

    /**
     * TODO: Handle concatenated expressions.
     *
     * @return TODO: Documentation.
     */
    private JavaSstNode simpleExpression() {
        final JavaSstNode node = term();
        final JavaSstNode parent = new JavaSstNode();
        token(PLUS, MINUS).repeat(() -> {
            switch (token.getType()) {
                case PLUS:
                    parent.setClazz(PLUS);
                    next();
                    parent.setRight(term());
                    break;
                case MINUS:
                    parent.setClazz(MINUS);
                    next();
                    parent.setRight(term());
                    break;
                default:
                    error(PLUS, MINUS);
            }
        });

        if (parent.getClazz() != null) {
            parent.setLeft(node);
            return parent;
        }

        return node;
    }

    /**
     * TODO: Handle concatenated terms.
     *
     * @return TODO: Documentation.
     */
    private JavaSstNode term() {
        final JavaSstNode node = factor();
        final JavaSstNode parent = new JavaSstNode();

        token(TIMES, SLASH).repeat(() -> {
            switch (token.getType()) {
                case TIMES:
                    parent.setClazz(TIMES);
                    next();
                    parent.setRight(factor());
                    break;
                case SLASH:
                    parent.setClazz(SLASH);
                    next();
                    parent.setRight(factor());
                    break;
                default:
                    error(TIMES, SLASH);
            }
        });

        if (parent.getClazz() != null) {
            parent.setLeft(node);
            return parent;
        }

        return node;
    }

    /**
     * TODO: Handle non integer numbers and variables.
     *
     * @return TODO: Documentation.
     */
    private JavaSstNode factor() {
        JavaSstNode node = new JavaSstNode();
        switch (token.getType()) {
            case IDENT:
                final JavaSstToken t = token;
                next();

                final String identifier = t.getIdentifier();
                final Optional<JavaSstParserObject> optional = symbolTable.object(identifier);
                final JavaSstParserObjectFuture alternative = new JavaSstParserObjectFuture(identifier, symbolTable);
                final JavaSstParserObject object = optional.orElseGet(() -> alternative);

                // Could be an internal function call.
                if (PARENTHESIS_OPEN == token.getType()) {
                    node.setClazz(CALL);
                    node.setObject(object);
                    node.setLeft(actualParameters().orElse(null));
                } else {
                    node.setClazz(VARIABLE);
                    node.setType(INTEGER);
                    node.setObject(object);
                }
                break;
            case NUMBER:
                node.setClazz(NUMBER);
                node.setType(INTEGER);
                node.setConstant(Integer.parseInt(token.getIdentifier()));
                next();
                break;
            case PARENTHESIS_OPEN:
                next();
                node = expression();
                token(PARENTHESIS_CLOSE).once();
                break;
            default:
                error(IDENT, NUMBER, PARENTHESIS_OPEN);
        }

        return node;
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
    private void error(final JavaSstType... expected) {
        error(Arrays.asList(expected));
    }

    /**
     * Get all possible first {@link JavaSstType} of the construct c.
     *
     * @param c The construct.
     * @return The possible first token types of c.
     */
    private List<JavaSstType> first(final String c) {
        final ArrayList<JavaSstType> result = new ArrayList<>();

        switch (c) {
            case "type":
                result.add(INT);
                break;
            case "function_declaration":
                result.addAll(first("function_head"));
                break;
            case "function_head":
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
                result.addAll(first("function_call"));
                result.addAll(first("if_statement"));
                result.addAll(first("while_statement"));
                result.addAll(first("return_statement"));
                break;
            case "assignment":
                result.add(IDENT);
                break;
            case "function_call":
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
