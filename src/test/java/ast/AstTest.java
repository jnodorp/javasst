package ast;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link Ast}.
 */
public class AstTest {

    /**
     * The {@link Ast}.
     */
    private final Ast<TestNode> ast = new Ast<>();

    @Before
    public void setUp() throws Exception {
        TestNode root = new TestNode();
        root.setClazz(C.ROOT);

        TestNode n0 = new TestNode();
        n0.setClazz(C.N0);

        TestNode n1 = new TestNode();
        n1.setClazz(C.N1);

        TestNode n2 = new TestNode();
        n2.setClazz(C.N2);

        TestNode n3 = new TestNode();
        n3.setClazz(C.N3);

        TestNode n4 = new TestNode();
        n4.setClazz(C.N4);

        TestNode n5 = new TestNode();
        n5.setClazz(C.N5);

        TestNode n6 = new TestNode();
        n6.setClazz(C.N6);

        TestNode n7 = new TestNode();
        n7.setClazz(C.N7);

        TestNode n8 = new TestNode();
        n8.setClazz(C.N8);

        TestNode n9 = new TestNode();
        n9.setClazz(C.N9);

        TestNode n10 = new TestNode();
        n10.setClazz(C.N10);

        TestNode n11 = new TestNode();
        n11.setClazz(C.N11);

        root.setLeft(n0);
        root.setRight(n1);
        root.setLink(n2);

        n0.setLeft(n3);
        n0.setRight(n4);
        n0.setLink(n5);

        n1.setLeft(n6);
        n1.setRight(n7);
        n1.setLink(n8);

        n2.setLeft(n9);
        n2.setRight(n10);
        n2.setLink(n11);

        ast.setRoot(root);
    }

    @Test
    public void testToDot() throws Exception {
        final String dot = ast.toString();
        final String expected = new Scanner(new File("src/test/resources/ast.dot")).useDelimiter("\\Z").next();
        assertEquals(expected, dot);
    }

    private enum C {ROOT, N0, N1, N2, N3, N4, N5, N6, N7, N8, N9, N10, N11}

    private enum S {}

    private enum T {}

    private class TestNode extends Node<C, T> {
    }
}