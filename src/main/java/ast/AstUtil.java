package ast;

/**
 * Utilities for working with the {@link Ast}.
 */
public class AstUtil {

    public static <N extends Node<?, ?, ?>> AstUtilOn on(Ast<N> ast) {
        return new AstUtilOn(ast);
    }

    /**
     * Perform actions on an {@link Ast}.
     */
    public static class AstUtilOn {

        /**
         * The {@link Ast}.
         */
        private final Ast ast;

        /**
         * Create a new {@link AstUtilOn}.
         *
         * @param ast The {@link Ast}.
         */
        private AstUtilOn(Ast ast) {
            this.ast = ast;
        }
    }
}
