package ast;

/**
 * The abstract syntax tree (AST).
 */
public class Ast<N extends Node<?, ?, ?>> {

    /**
     * The root.
     */
    private N root;

    /**
     * Traverse the {@link Ast} beginning with the given node.
     *
     * @param node The {@link Node}.
     */
    private static void traverse(Node<?, ?, ?> node) {
        while (node != null) {
            if (node.getLeft().isPresent()) {
                traverse(node.getLeft().get());
            }

            if (node.getRight().isPresent()) {
                traverse(node.getRight().get());
            }

            // TODO: Do something here.

            if (node.getLink().isPresent()) {
                node = node.getLink().get();
            }
        }
    }

    /**
     * Get the root.
     *
     * @return The root.
     */
    public N getRoot() {
        return root;
    }

    /**
     * Set the root.
     *
     * @param root The root.
     */
    public void setRoot(final N root) {
        this.root = root;
    }

    /**
     * Traverse the {@link Ast}.
     */
    public void traverse() {
        traverse(root);
    }
}
