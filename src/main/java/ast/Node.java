package ast;

import parser.ParserObject;

import java.util.Optional;

/**
 * A {@link Node} of the abstract syntax tree (AST).
 * <p>
 * TODO: Add handling for constants.
 *
 * @param <C> The {@link Node}s class enumeration.
 * @param <S> The {@link Node}s subclass enumeration.
 * @param <T> The {@link Node}s type enumeration.
 */
public abstract class Node<C extends Enum, S extends Enum, T extends Enum> {

    /**
     * The left {@link Node}.
     */
    private Optional<Node<C, S, T>> left;

    /**
     * The right {@link Node}.
     */
    private Optional<Node<C, S, T>> right;

    /**
     * The link {@link Node}.
     */
    private Optional<Node<C, S, T>> link;

    /**
     * The type.
     */
    private C clazz;

    /**
     * The subtype.
     */
    private S subclass;

    /**
     * The type.
     */
    private T type;

    /**
     * The object.
     */
    private Optional<ParserObject> object;

    /**
     * Create a new {@link Node}.
     */
    public Node() {
        this.left = Optional.empty();
        this.right = Optional.empty();
        this.link = Optional.empty();
        this.object = Optional.empty();
    }

    /**
     * Get the left {@link Node}.
     *
     * @return The left {@link Node}.
     */
    public Optional<Node<C, S, T>> getLeft() {
        return left;
    }

    /**
     * Set the left.
     *
     * @param left The left.
     */
    public void setLeft(Node<C, S, T> left) {
        this.left = Optional.of(left);
    }

    /**
     * Get the right {@link Node}.
     *
     * @return The right {@link Node}.
     */
    public Optional<Node<C, S, T>> getRight() {
        return right;
    }

    /**
     * Set the right {@link Node}.
     *
     * @param right The right {@link Node}.
     */
    public void setRight(Node<C, S, T> right) {
        this.right = Optional.of(right);
    }

    /**
     * Get the link {@link Node}.
     *
     * @return The link {@link Node}.
     */
    public Optional<Node<C, S, T>> getLink() {
        return link;
    }

    /**
     * Set the link {@link Node}.
     *
     * @param link The link {@link Node}.
     */
    public void setLink(Node<C, S, T> link) {
        this.link = Optional.of(link);
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
     * Get the subclass.
     *
     * @return The subclass.
     */
    public S getSubclass() {
        return subclass;
    }

    /**
     * Set the subclass.
     *
     * @param subclass The subclass.
     */
    public void setSubclass(S subclass) {
        this.subclass = subclass;
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
    public void setObject(Optional<ParserObject> object) {
        this.object = object;
    }
}
