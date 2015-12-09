package scanner;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Test class for {@link Input}.
 */
public class InputTest {

    @Test
    public void testNextAndHasNext() throws Exception {
        Input input = new Input("src/test/resources/input_test.txt");

        assertTrue(input.hasNext());
        assertEquals("A", "" + input.next());
        assertFalse(input.hasNext());
    }

    @Test
    public void testGetPosition() throws Exception {
        Input input = new Input("src/test/resources/input_test.txt");

        assertEquals("input_test.txt", input.getFile().getName());
        assertEquals(1, input.getLine());
        assertEquals(1, input.getColumn());

        assertEquals("A", "" + input.next());

        assertEquals("input_test.txt", input.getFile().getName());
        assertEquals(1, input.getLine());
        assertEquals(2, input.getColumn());
    }

    @Test
    public void testLookahead() throws Exception {
        Input input = new Input("src/test/resources/input_test1.txt");

        assertEquals("T", "" + input.next());
        assertEquals("h", "" + input.next());
        assertEquals("i", "" + input.next());

        final StringBuilder stringBuilder = new StringBuilder();
        Arrays.stream(input.lookahead(10)).forEach(stringBuilder::append);
        assertEquals("s is a lon", stringBuilder.toString());

        assertEquals("s", "" + input.next());
        assertEquals(" ", "" + input.next());
        assertEquals("i", "" + input.next());
    }
}