package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * A parser converts a scanner to a tree structure.
 *
 * @param <T> The {@link Token} object.
 * @param <E> The {@link Token}s type.
 */
public abstract class Parser<T extends Token<E>, E extends Enum> {

    /**
     * The scanner.
     */
    protected Iterator<T> scanner;

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
    public Parser(final Iterator<T> scanner) {
        this.scanner = scanner;
    }

    /**
     * Set current to the next {@link Token}.
     */
    protected void next() {
        this.token = scanner.next();
        Logger.getLogger(this.getClass().getName()).info(this.token.toString());
    }

    /**
     * Switch to the error state.
     */
    abstract void error(final List<E> expected);

    /**
     * Start the parsing process (by calling the start node method).
     */
    public abstract void parse();

    /**
     * Allow verifications on the current token.
     *
     * @return A {@link Specification} object.
     */
    Specification token() {
        return new Specification();
    }

    /**
     * A pecification verifies, that the current {@link Token} is valid.
     */
    protected class Specification {

        /**
         * The expected tokens.
         */
        private final List<E> expected;

        /**
         * Create a new specification.
         */
        private Specification() {
            this.expected = new ArrayList<>();
        }

        /**
         * Add expected tokens to the specification.
         *
         * @param expected The expected tokens.
         * @return A counter object t specify the number of times the tokens have to occur.
         */
        @SafeVarargs
        public final Counter is(final E... expected) {
            this.expected.addAll(Arrays.asList(expected));
            return new Counter(this.expected);
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
