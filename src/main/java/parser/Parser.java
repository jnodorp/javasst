package parser;

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
 */
public abstract class Parser<T extends Token<E>, E extends Enum> {

    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());

    /**
     * The scanner.
     */
    protected Scanner<T, E> scanner;

    /**
     * The current token.
     */
    protected T token;

    /**
     * The symbol table.
     */
    protected SymbolTable symbolTable;

    /**
     * Create a new parser.
     *
     * @param scanner The scanner.
     */
    public Parser(final Scanner<T, E> scanner) {
        this.scanner = scanner;
    }

    /**
     * Set current to the next {@link Token}.
     */
    protected void next() {
        this.token = scanner.next();
        LOGGER.fine(this.token.toString());
    }

    /**
     * Run the given code with its own {@link SymbolTable}.
     *
     * @param runnable The code to run.
     */
    protected void scope(Runnable runnable) {
        final SymbolTable oldSymbolTable = symbolTable;
        symbolTable = new SymbolTable(oldSymbolTable);
        runnable.run();
        symbolTable = oldSymbolTable;
    }

    /**
     * Switch to the error state.
     */
    protected void error(final List<E> expected) {
        String message = "Unexpected token " + System.lineSeparator() + token + System.lineSeparator();

        if (expected.size() > 0) {
            message += " Expected token of one of the following types: " + expected.toString() + ".";
        }

        LOGGER.log(Level.SEVERE, message);
        throw new RuntimeException();
    }

    /**
     * Start the parsing process (by calling the start node method).
     */
    public abstract void parse();

    /**
     * Allow verifications on the current token.
     *
     * @return A {@link Specification} object.
     */
    protected Specification token() {
        return new Specification();
    }

    /**
     * A pecification verifies, that the current {@link Token} is valid.
     */
    protected class Specification {

        /**
         * Create a new specification.
         */
        private Specification() {
            // Hidden constructor.
        }

        /**
         * Add expected tokens to the specification.
         *
         * @param expected The expected tokens.
         * @return A counter object t specify the number of times the tokens have to occur.
         */
        @SafeVarargs
        public final Counter is(final E... expected) {
            return is(Arrays.asList(expected));
        }

        /**
         * Add expected {@link Token}s to the specification.
         *
         * @param expected The expected {@link Token}s.
         * @return A counter object t specify the number of times the {@link Token}s have to occur.
         */
        public Counter is(final List<E> expected) {
            return new Counter(expected);
        }
    }

    /**
     * As part of the specification the counter verifies the number of {@link Token}s to match.
     */
    protected class Counter {

        /**
         * The expected tokens.
         */
        private final List<E> expected;

        /**
         * Create a new counter expecting the given {@link Token}s.
         *
         * @param expected The expected {@link Token}s.
         */
        private Counter(final List<E> expected) {
            this.expected = expected;
        }

        /**
         * Make sure the token is available exactly once. Throw an error otherwise.
         */
        public void once() {
            if (expected.contains(token.getType())) {
                next();
            } else {
                error(expected);
            }
        }

        /**
         * If the {@link Specification} matches the current {@link Token} execute the function.
         *
         * @param function The function.
         */
        public void optional(final Runnable function) {
            if (expected.contains(token.getType())) {
                function.run();
            }
        }

        /**
         * Repeat the function while the {@link Specification} matches the current {@link Token}.
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
