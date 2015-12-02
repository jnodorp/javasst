package scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for {@link MultiMarkInputStream}.
 */
public class MultiMarkInputStreamTest {

    /**
     * Test content.
     */
    private static final String TEST_CONTENT = "ABCDEFGHIJKLMNOPQRSTUVVXYZ";

    /**
     * Unit unde test.
     */
    private InputStream inputStream;

    @Before
    public void setUp() {
        final InputStream byteArrayInputStream = new ByteArrayInputStream(TEST_CONTENT.getBytes());
        this.inputStream = new MultiMarkInputStream(byteArrayInputStream);
    }

    @After
    public void tearDown() throws IOException {
        inputStream.close();
    }

    @Test
    public void testRead() throws IOException {
        assertEquals("A", read());
        assertEquals("B", read());
        assertEquals("C", read());
    }

    @Test
    public void testSkip() throws IOException {
        assertEquals(9, inputStream.skip(9));
        assertEquals("J", read());
    }

    @Test
    public void testAvailable() throws IOException {
        assertEquals(26, inputStream.available());
    }

    @Test(expected = IOException.class)
    public void testClose() throws IOException {
        inputStream.close();
        assertEquals("Not a real assertion since we expect an IOException here.", -1, inputStream.read());
    }

    @Test
    public void testMark() throws IOException {
        inputStream.mark(0);
        assertEquals("A", read());
        assertEquals("B", read());
        inputStream.mark(1);
        assertEquals("C", read());
        assertEquals("D", read());
        inputStream.mark(2);
        assertEquals("E", read());
        assertEquals("F", read());
        inputStream.mark(3);
        assertEquals("G", read());
        assertEquals("H", read());
        assertEquals("I", read());
        assertEquals("J", read());
    }

    @Test
    public void testReset_OneMark() throws IOException {
        inputStream.mark(1);
        assertEquals("A", read());
        assertEquals("B", read());
        inputStream.reset();
        assertEquals("A", read());
        assertEquals("C", Character.toString((char) inputStream.read()));
    }

    @Test(expected = IOException.class)
    public void testReset_WithoutMark_EmptyStackException() throws IOException {
        inputStream.reset();
    }

    @Test
    public void testReset_MultiMark() throws Exception {
        inputStream.mark(2);
        assertEquals("A", read());
        assertEquals("B", read());
        assertEquals("C", read());
        inputStream.mark(3);
        assertEquals("D", read());
        inputStream.reset();
        assertEquals("D", read());
        assertEquals("E", read());
        inputStream.reset();
        assertEquals("A", read());
        assertEquals("B", read());
        assertEquals("F", read());
    }

    @Test
    public void testMarkSupported() throws Exception {
        assertTrue(inputStream.markSupported());
    }

    private String read() throws IOException {
        return Character.toString((char) inputStream.read());
    }
}