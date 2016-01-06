package ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

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

    public String toDot() {
        final StringBuilder prefix = new StringBuilder("digraph AST {").append(System.lineSeparator());
        final int[] count = new int[]{0};
        traverse(node -> {
            prefix.append(node.toDot(Integer.toString(count[0])));
            prefix.append(System.lineSeparator());
            count[0]++;
        });

        return toDot(root, prefix, 0).append(System.lineSeparator()).append("}").toString();
    }

    /**
     * Build a string by traversing the AST.
     *
     * @param node   The current node.
     * @param string The current string.
     * @return The built string.
     */
    private StringBuilder toDot(Node<?, ?, ?> node, final StringBuilder string, int count) {
        final List<Node<?, ?, ?>> nodes = new ArrayList<>();
        traverse(nodes::add);

        while (node != null) {
            if (node.getLeft().isPresent()) {
                String link = index(nodes, node) + " -> " + index(nodes, node.getLeft().get()) + ";";
                string.append(link).append(System.lineSeparator());
                return toDot(node.getLeft().get(), string, count + 1);
            }

            if (node.getRight().isPresent()) {
                String link = index(nodes, node) + " -> " + index(nodes, node.getRight().get()) + ";";
                string.append(link).append(System.lineSeparator());
                return toDot(node.getRight().get(), string, count + 1);
            }

            if (node.getLink().isPresent()) {
                String link = index(nodes, node) + " -> " + index(nodes, node.getLink().get()) + ";";
                string.append(link).append(System.lineSeparator());
                node = node.getLink().get();
            } else {
                node = null;
            }
        }

        return string;
    }

    private String index(final List<?> list, final Object object) {
        for (int i = 0; i < list.size(); i++) {
            if (object.equals(list.get(0))) {
                return Integer.toString(i);
            }
        }

        return "NOT_FOUND";
    }
}
