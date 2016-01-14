package ast;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The abstract syntax tree (AST).
 */
public final class Ast<N extends Node<?, ?, ?>> {

    /**
     * The root.
     */
    private N root;

    /**
     * Traverse the {@link Ast} beginning with the given node.
     *
     * @param node     The {@link Node}.
     * @param consumer The {@link Consumer} acting on the {@link Node}.
     */
    private static void traverse(Node<?, ?, ?> node, final Consumer<Node<?, ?, ?>> consumer) {
        while (node != null) {
            if (node.getLeft().isPresent()) {
                traverse(node.getLeft().get(), consumer);
            }

            if (node.getRight().isPresent()) {
                traverse(node.getRight().get(), consumer);
            }

            consumer.accept(node);

            if (node.getLink().isPresent()) {
                node = node.getLink().get();
            } else {
                node = null;
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
    public void traverse(final Consumer<Node<?, ?, ?>> consumer) {
        traverse(root, consumer);
    }

    /**
     * Build a string by traversing the AST.
     *
     * @return The built string.
     */
    public String toDot() {
        final List<Node<?, ?, ?>> nodes = new ArrayList<>();

        // Prepare string with node definitions.
        final StringBuilder string = new StringBuilder("digraph AST {").append(System.lineSeparator());
        traverse(node -> {
            string.append("\t");
            string.append(node.toDot(Integer.toString(nodes.size())));
            string.append(System.lineSeparator());
            nodes.add(node);
        });
        string.append(System.lineSeparator());

        // Add edges to string.
        traverse(node -> {
            final String s = "\t" + index(nodes, node) + " -> ";
            if (node.getLeft().isPresent()) {
                string.append(s).append(index(nodes, node.getLeft().get()));
                string.append("[ label=\"left\"]").append(";").append(System.lineSeparator());
            }

            if (node.getRight().isPresent()) {
                string.append(s).append(index(nodes, node.getRight().get()));
                string.append("[ label=\"right\"]").append(";").append(System.lineSeparator());
            }

            if (node.getLink().isPresent()) {
                string.append(s).append(index(nodes, node.getLink().get()));
                string.append("[ label=\"link\"]").append(";").append(System.lineSeparator());
            }
        });

        return string.append("}").toString();
    }

    /**
     * Get the index of a {@link Node} in a list of {@link Node}s.
     *
     * @param list   The list.
     * @param object The {@link Node}.
     * @return The index of the {@link Node} pre and post fixed with a '"'.
     */
    private String index(final List<Node<?, ?, ?>> list, final Node<?, ?, ?> object) {
        for (int i = 0; i < list.size(); i++) {
            if (object.equals(list.get(i))) {
                return "\"" + Integer.toString(i) + "\"";
            }
        }

        return "NOT_FOUND";
    }
}
