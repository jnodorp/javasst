package javasst.ast;

import ast.Node;
import javasst.JavaSstType;

/**
 * A Java SST {@link Node}.
 */
public class JavaSstNode extends Node<JavaSstType, JavaSstType> {

    /**
     * Create a new {@link Node}.
     */
    public JavaSstNode() {
        this(null, null);
    }

    /**
     * Create a new {@link Node}.
     *
     * @param clazz The class.
     */
    public JavaSstNode(final JavaSstType clazz) {
        this(clazz, null);
    }

    /**
     * Create a new {@link Node}.
     *
     * @param clazz The class.
     * @param type  The type.
     */
    public JavaSstNode(final JavaSstType clazz, final JavaSstType type) {
        super();
        super.setClazz(clazz);
        super.setType(type);
    }
}
