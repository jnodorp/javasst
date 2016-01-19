package ast;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The abstract syntax tree (AST).
 */
public final class Ast<N extends Node<?, ?>> {

    /**
     * The root.
     */
    private final N root;

    /**
     * Create a new AST.
     *
     * @param root The root.
     */
    public Ast(final N root) {
        this.root = root;
    }

    /**
     * Traverse the {@link Ast} beginning with the given node.
     *
     * @param node     The {@link Node}.
     * @param consumer The {@link Consumer} acting on the {@link Node}.
     */
    private static void traverse(Node<?, ?> node, final Consumer<Node<?, ?>> consumer) {
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
     * Traverse the {@link Ast}.
     *
     * @param consumer The consumer getting the {@link Node}s.
     */
    public void traverse(final Consumer<Node<?, ?>> consumer) {
        traverse(root, consumer);
    }

    @Override
    public String toString() {
        final List<Node<?, ?>> nodes = new ArrayList<>();

        // Prepare string with node definitions.
        final StringBuilder result = new StringBuilder("digraph AST {").append(System.lineSeparator());
        traverse(node -> {
            result.append("\t");
            result.append(node.toDot(Integer.toString(nodes.size())));
            result.append(System.lineSeparator());
            nodes.add(node);
        });
        result.append(System.lineSeparator());

        // Add edges to string.
        traverse(node -> {
            final String s = "\t\"" + nodes.indexOf(node) + "\" -> \"";
            if (node.getLeft().isPresent()) {
                result.append(s).append(nodes.indexOf(node.getLeft().get()));
                result.append("\" [label=\"left\"]").append(";").append(System.lineSeparator());
            }

            if (node.getRight().isPresent()) {
                result.append(s).append(nodes.indexOf(node.getRight().get()));
                result.append("\" [label=\"right\"]").append(";").append(System.lineSeparator());
            }

            if (node.getLink().isPresent()) {
                result.append(s).append(nodes.indexOf(node.getLink().get()));
                result.append("\" [label=\"link\"]").append(";").append(System.lineSeparator());
            }
        });

        return result.append("}").toString();
    }

    /**
     * Enumeration describing possible {@link Node} positions in the AST.
     */
    public enum Position {
        LEFT, LINK, RIGHT
    }
}
