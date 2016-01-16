package parser;

import ast.Ast;
import ast.Node;
import scanner.Scanner;
import scanner.Token;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A parser converts a scanner to a tree structure.
 *
 * @param <T> The {@link Token} object.
 * @param <E> The {@link Token}s type.
 * @param <O> The {@link SymbolTable}s members type.
 * @param <N> The {@link Node}s type.
 */
public abstract class Parser<T extends Token<E>, E extends Enum, O extends ParserObject, N extends Node<?, ?, ?>> {

    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());

    /**
     * The AST.
     */
    protected final Ast<N> ast;

    /**
     * The scanner.
     */
    private final Scanner<T, E> scanner;

    /**
     * The current token.
     */
    protected T token;

    /**
     * The symbol table.
     */
    protected SymbolTable<O> symbolTable;

    /**
     * Create a new parser.
     *
     * @param scanner The scanner.
     */
    public Parser(final Scanner<T, E> scanner) {
        this.scanner = scanner;
        this.ast = new Ast<>();
    }

    /**
     * Set current to the next {@link Token}.
     */
    protected void next() {
        this.token = scanner.next();
        if (token != null) {
            LOGGER.fine(this.token.toString());
        }
    }

    /**
     * Run the given code with its own {@link SymbolTable}.
     *
     * @param runnable The code to run.
     */
    protected void scope(Runnable runnable) {
        final SymbolTable<O> oldSymbolTable = symbolTable;
        symbolTable = new SymbolTable<>(oldSymbolTable);
        runnable.run();
        symbolTable = oldSymbolTable;
    }

    /**
     * Switch to the error state.
     *
     * @param expected A list of expected tokens.
     */
    protected void error(final List<E> expected) {
        String message = "Unexpected token " + token;

        if (expected.size() > 0) {
            message += ". Expected token of one of the following types: " + expected.toString() + ".";
        }

        LOGGER.log(Level.SEVERE, message);
        throw new RuntimeException();
    }

    /**
     * Start the parsing process (by calling the start node method).
     *
     * @return The generated {@link Ast}.
     */
    public abstract Ast<N> parse();

    /**
     * Allow verifications on the current token.
     *
     * @return A {@link TokenVerification} object.
     */
    protected TokenVerification token(final List<E> expected) {
        return new TokenVerification(expected);
    }

    /**
     * Allow verifications on the current token.
     *
     * @return A {@link TokenVerification} object.
     */
    @SafeVarargs
    protected final TokenVerification token(final E... expected) {
        return token(Arrays.asList(expected));
    }

    /**
     * As part of the specification the counter verifies the number of {@link Token}s to match.
     */
    protected class TokenVerification {

        /**
         * The expected tokens.
         */
        private final List<E> expected;

        /**
         * Create a new counter expecting the given {@link Token}s.
         *
         * @param expected The expected {@link Token}s.
         */
        private TokenVerification(final List<E> expected) {
            this.expected = expected;
        }

        /**
         * Make sure the token is available exactly once. Throw an error otherwise.
         */
        public T once() {
            if (expected.contains(token.getType())) {
                final T result = token;
                next();
                return result;
            } else {
                error(expected);
                return null;
            }
        }

        /**
         * If the {@link Token} matches the current {@link Token} execute the function.
         *
         * @param function The function.
         */
        public void optional(final Runnable function) {
            if (expected.contains(token.getType())) {
                function.run();
            }
        }

        /**
         * Repeat the function while the {@link Token} matches the current {@link Token}.
         *
         * @param function The function.
         */
        public void repeat(final Runnable function) {
            while (expected.contains(token.getType())) {
                function.run();
            }
        }
    }
}
