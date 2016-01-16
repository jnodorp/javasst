package ast;

import parser.ParserObject;

import java.util.Optional;

/**
 * A {@link Node} of the abstract syntax tree (AST).
 *
 * @param <C> The {@link Node}s class enumeration.
 * @param <T> The {@link Node}s type enumeration.
 */
public abstract class Node<C extends Enum, T extends Enum> {

    /**
     * The left {@link Node}.
     */
    private Optional<Node<C, T>> left;

    /**
     * The right {@link Node}.
     */
    private Optional<Node<C, T>> right;

    /**
     * The link {@link Node}.
     */
    private Optional<Node<C, T>> link;

    /**
     * The type.
     */
    private C clazz;

    /**
     * The type.
     */
    private T type;

    /**
     * The object.
     */
    private Optional<ParserObject> object;

    /**
     * The constant.
     */
    private Optional<Number> constant;

    /**
     * Create a new {@link Node}.
     */
    public Node() {
        this.left = Optional.empty();
        this.right = Optional.empty();
        this.link = Optional.empty();
        this.object = Optional.empty();
        this.constant = Optional.empty();
    }

    /**
     * Get the left {@link Node}.
     *
     * @return The left {@link Node}.
     */
    public Optional<Node<C, T>> getLeft() {
        return left;
    }

    /**
     * Set the left.
     *
     * @param left The left.
     */
    public void setLeft(Node<C, T> left) {
        this.left = Optional.ofNullable(left);
    }

    /**
     * Get the right {@link Node}.
     *
     * @return The right {@link Node}.
     */
    public Optional<Node<C, T>> getRight() {
        return right;
    }

    /**
     * Set the right {@link Node}.
     *
     * @param right The right {@link Node}.
     */
    public void setRight(Node<C, T> right) {
        this.right = Optional.ofNullable(right);
    }

    /**
     * Get the link {@link Node}.
     *
     * @return The link {@link Node}.
     */
    public Optional<Node<C, T>> getLink() {
        return link;
    }

    /**
     * Set the link {@link Node}.
     *
     * @param link The link {@link Node}.
     */
    public void setLink(Node<C, T> link) {
        this.link = Optional.ofNullable(link);
    }

    /**
     * Get the class.
     *
     * @return The class.
     */
    public C getClazz() {
        return clazz;
    }

    /**
     * Set the class.
     *
     * @param clazz The class.
     */
    public void setClazz(C clazz) {
        this.clazz = clazz;
    }

    /**
     * Get the type.
     *
     * @return The type.
     */
    public T getType() {
        return type;
    }

    /**
     * Set the type.
     *
     * @param type The type.
     */
    public void setType(T type) {
        this.type = type;
    }

    /**
     * Get the object.
     *
     * @return The object.
     */
    public Optional<ParserObject> getObject() {
        return object;
    }

    /**
     * Set the object.
     *
     * @param object The object.
     */
    public void setObject(ParserObject object) {
        this.object = Optional.of(object);
    }

    /**
     * Get the constant.
     *
     * @return The constant.
     */
    public Optional<Number> getConstant() {
        return constant;
    }

    /**
     * Set the constant.
     *
     * @param constant The constant.
     */
    public void setConstant(Number constant) {
        this.constant = Optional.of(constant);
    }

    /**
     * Create the dot representation for this Node.
     *
     * @return The dot representation of this Node.
     */
    public String toDot(final String name) {
        final String cl = clazz == null ? "-" : clazz.toString();
        final String t = type == null ? "-" : type.toString();
        final String o = object.isPresent() ? object.get().getIdentifier() : "-";
        final String co = constant.isPresent() ? constant.get().toString() : "-";

        return "\"" + name + "\" [shape=record, label=\"{" + "<class> " + cl + " | " +
                "<type> " + t + " | " +
                "<object> " + o + " | " +
                "<constant> " + co +
                "}\"];";
    }
}
