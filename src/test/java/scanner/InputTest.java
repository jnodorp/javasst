package scanner;

import org.junit.Test;
import scanner.Input;

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

        assertEquals("input_test.txt", input.getPosition().getFile().getName());
        assertEquals(1, input.getPosition().getLine());
        assertEquals(1, input.getPosition().getColumn());

        assertEquals("A", "" + input.next());

        assertEquals("input_test.txt", input.getPosition().getFile().getName());
        assertEquals(1, input.getPosition().getLine());
        assertEquals(2, input.getPosition().getColumn());
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