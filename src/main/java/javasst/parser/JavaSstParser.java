package javasst.parser;

import ast.Ast;
import ast.Node;
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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javasst.scanner.JavaSstTokenType.*;

/**
 * A parser for Java SST.
 */
public class JavaSstParser extends Parser<JavaSstToken, JavaSstTokenType, JavaSstParserObject, JavaSstNode> {

    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(JavaSstParser.class.getName());

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
     * Class: {@code class} {@link JavaSstTokenType#IDENT} {@link #classBody(JavaSstNode)}.
     *
     * @return The class node.
     */
    private JavaSstNode clazz() {
        final JavaSstNode n = new JavaSstNode();
        final JavaSstParserObject[] o = new JavaSstParserObject[1];
        n.setClazz(JavaSstNodeClass.CLASS);

        ast.setRoot(n);
        scope(() -> {
            token(CLASS).once();
            final JavaSstToken token = token(IDENT).once();

            // Create the {@link ParserObject}.
            o[0] = new JavaSstParserObject(token, JavaSstParserObjectClass.CLASS, symbolTable);

            classBody(n);
        });

        // Add object to symbol table.
        symbolTable.add(o[0]);

        // Finish AST.
        n.setObject(o[0]);

        return n;
    }

    /**
     * Class body: &#123; {@link #constant()} {@link #variableDeclaration()} {@link #functionDeclaration()} &#125;.
     *
     * @param classNode The class node.
     */
    private void classBody(final JavaSstNode classNode) {
        token(CURLY_BRACE_OPEN).once();
        token(FINAL).repeat(() -> {
            ast.insert(constant(), Ast.Position.LEFT, Ast.Position.LINK);
        });
        token(first("variable_declaration")).repeat(this::variableDeclaration);
        token(first("function_declaration")).repeat(this::functionDeclaration);
        token(CURLY_BRACE_CLOSE).once();
    }

    /**
     * Constant: {@code final} {@link #type()} {@link JavaSstTokenType#IDENT} {@code =} {@link JavaSstTokenType#NUMBER}
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
        final JavaSstParserObject object = new JavaSstParserObject(token, JavaSstParserObjectClass.CONSTANT);
        object.setIntValue(integerValue);
        object.setType(JavaSstParserObjectType.INTEGER);
        symbolTable.add(object);

        // Create the node.
        final JavaSstNode node = new JavaSstNode();
        node.setClazz(JavaSstNodeClass.CONSTANT);
        node.setObject(object);
        node.setType(JavaSstNodeType.INTEGER);
        return node;
    }

    /**
     * Variable declaration: {@link #type()} {@link JavaSstTokenType#IDENT} {@code ;}.
     * <p>
     * TODO: Add handling for non integer variable declarations.
     */
    private JavaSstNode variableDeclaration() {
        // Verify syntax.
        type();
        final JavaSstToken token = token(IDENT).once();
        token(SEMICOLON).once();

        // Build symbol table entry.
        JavaSstParserObject p = new JavaSstParserObject(token, JavaSstParserObjectClass.VARIABLE);
        try {
            symbolTable.add(p);
        } catch (SymbolTable.SymbolAlreadyExists symbolAlreadyExists) {
            LOGGER.log(Level.SEVERE, "Identifier already used.", symbolAlreadyExists);
            System.exit(symbolAlreadyExists.hashCode());
        }

        // Build AST node.
        JavaSstNode n = new JavaSstNode();
        n.setClazz(JavaSstNodeClass.VARIABLE);
        n.setObject(p);
        n.setType(JavaSstNodeType.INTEGER);
        return n;
    }

    /**
     * Function declaration: {@code public} ... {@link JavaSstTokenType#IDENT} ...
     * <p>
     * TODO: Finish documentation.
     * TODO: Add handling for non integer function declarations.
     */
    private void functionDeclaration() {
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

            statementSequence();
            token(CURLY_BRACE_CLOSE).once();
        });

        // Build symbol table.
        @SuppressWarnings("unchecked")
        JavaSstParserObject p = new JavaSstParserObject(token, JavaSstParserObjectClass.FUNCTION, st[0]);
        p.setType(JavaSstParserObjectType.INTEGER);

        // Build AST.
        JavaSstNode n = new JavaSstNode();
        n.setClazz(JavaSstNodeClass.FUNCTION);

        switch (type) {
            case "void":
                p.setType(JavaSstParserObjectType.VOID);
                n.setType(JavaSstNodeType.VOID);
                break;
            case "int":
                p.setType(JavaSstParserObjectType.INTEGER);
                n.setType(JavaSstNodeType.INTEGER);
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

        // Add node to the right of root.
        if (ast.getRoot().getRight().isPresent()) {
            Node<JavaSstNodeClass, JavaSstNodeSubclass, JavaSstNodeType> parent = ast.getRoot().getLeft().get();
            while (parent.getLink().isPresent()) {
                parent = parent.getLink().get();
            }

            parent.setLink(n);
        } else {
            ast.getRoot().setRight(n);
        }
    }

    /**
     * TODO: Add handling for non integer parameters.
     */
    private List<JavaSstParserObject> formalParameters() {
        token(PARENTHESIS_OPEN).once();

        List<JavaSstParserObject> parameters = new LinkedList<>();
        token(first("fp_section")).optional(() -> {
            type();
            JavaSstToken token = token(IDENT).once();

            // Build symbol table.
            JavaSstParserObject parameter = new JavaSstParserObject(token, JavaSstParserObjectClass.PARAMETER);
            parameter.setType(JavaSstParserObjectType.INTEGER);
            parameters.add(parameter);

            token(COMMA).repeat(() -> {
                next();
                type();
                final JavaSstToken t = token(IDENT).once();

                // Build symbol table.
                final JavaSstParserObject p = new JavaSstParserObject(t, JavaSstParserObjectClass.PARAMETER);
                p.setType(JavaSstParserObjectType.INTEGER);
                parameters.add(p);
            });
        });

        token(PARENTHESIS_CLOSE).once();

        return parameters;
    }

    private void statementSequence() {
        statement();
        token(first("statement")).repeat(this::statement);
    }

    private void statement() {
        // Could be an assignment or a function call.
        if (IDENT == token.getType()) {
            next();

            if (PARENTHESIS_OPEN == token.getType()) {
                actualParameters();
                token(SEMICOLON).once();
            } else if (EQUALS == token.getType()) {
                token(EQUALS).once();
                expression();
                token(SEMICOLON).once();
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
        token(INT).once();
    }

    private void ifStatement() {
        token(IF).once();
        token(PARENTHESIS_OPEN).once();
        expression();
        token(PARENTHESIS_CLOSE).once();
        token(CURLY_BRACE_OPEN).once();
        statementSequence();
        token(CURLY_BRACE_CLOSE).once();
        token(ELSE).once();
        token(CURLY_BRACE_OPEN).once();
        statementSequence();
        token(CURLY_BRACE_CLOSE).once();
    }

    private void whileStatement() {
        token(WHILE).once();
        token(PARENTHESIS_OPEN).once();
        expression();
        token(PARENTHESIS_CLOSE).once();
        token(CURLY_BRACE_OPEN).once();
        statementSequence();
        token(CURLY_BRACE_CLOSE).once();
    }

    private void returnStatement() {
        token(RETURN).once();
        token(first("simple_expression")).optional(this::simpleExpression);
        token(SEMICOLON).once();
    }

    private void actualParameters() {
        token(PARENTHESIS_OPEN).once();
        token(first("expression")).optional(() -> {
            expression();
            token(COMMA).repeat(() -> {
                token(COMMA).once();
                expression();
            });
        });

        token(PARENTHESIS_CLOSE).once();
    }

    private void expression() {
        simpleExpression();
        token(EQUALS_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS, LESS_THAN, LESS_THAN_EQUALS).optional(() -> {
            next();
            simpleExpression();
        });
    }

    private void simpleExpression() {
        term();
        token(PLUS, MINUS).repeat(() -> {
            next();
            term();
        });
    }

    private void term() {
        factor();
        token(TIMES, SLASH).repeat(() -> {
            next();
            factor();
        });
    }

    private void factor() {
        final JavaSstNode node = new JavaSstNode();

        if (IDENT == token.getType()) {
            final String identifier = token.getIdentifier();
            next();

            // Could be an internal function call.
            if (PARENTHESIS_OPEN == token.getType()) {
                actualParameters();
            } else {
                node.setClazz(JavaSstNodeClass.VARIABLE);
                JavaSstParserObject o = symbolTable.object(identifier).orElseThrow(UnknownError::new);
                node.setObject(o);
            }
        } else if (NUMBER == token.getType()) {
            node.setClazz(JavaSstNodeClass.NUMBER);
            node.setConstant(Integer.parseInt(token.getIdentifier()));
            next();
        } else if (PARENTHESIS_OPEN == token.getType()) {
            next();
            expression();
            token(PARENTHESIS_CLOSE).once();
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
