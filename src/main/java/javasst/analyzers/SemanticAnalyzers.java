package javasst.analyzers;

import ast.Node;

import java.util.function.Consumer;

/**
 * This class contains factory methods for semantic analyzers.
 */
public class SemanticAnalyzers {

    /**
     * Create a function checking whether every referenced {@link parser.ParserObject} is declared somewhere.
     *
     * @return The function.
     */
    public static Consumer<Node<?, ?>> allDeclared() {
        return node -> {
            if (node.getObject().isPresent()) {
                try {
                    node.getObject().get().getIdentifier();
                } catch (UnknownError e) {
                    throw new SemanticAnalysisException();
                }
            }
        };
    }

    /**
     * Exception thrown if the semantic analysis fails.
     */
    public static class SemanticAnalysisException extends RuntimeException {

        /**
         * Create a new instance.
         */
        public SemanticAnalysisException() {
            super("The semantical analysis failed.");
        }
    }
}
