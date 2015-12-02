package scanner;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * This class is an {@link InputStream} which support multiple markers and multiple {@link #reset()} calls
 * respectively.
 */
class MultiMarkInputStream extends InputStream {

    /**
     * The underlying {@link InputStream}.
     */
    private final InputStream inputStream;

    /**
     * Buffer used for mark {@link #mark(int)} and {@link #reset()}.
     */
    private final Stack<Queue<Integer>> buffers;

    /**
     * Buffer which is currently returned.
     */
    private Queue<Integer> current;

    /**
     * Create a new {@link MultiMarkInputStream} using a provided {@link InputStream} as an underlying stream.
     *
     * @param inputStream The underlying {@link InputStream}.
     */
    public MultiMarkInputStream(final InputStream inputStream) {
        this.inputStream = inputStream;
        this.buffers = new Stack<>();
    }

    /**
     * Reads the next byte of data from the input stream. The value byte is returned as an <code>int</code> in the range
     * <code>0</code> to <code>255</code>. If no byte is available because the end of the stream has been reached, the
     * value <code>-1</code> is returned. This method blocks until input data is available, the end of the stream is
     * detected, or an exception is thrown.
     * <p>
     * <p> A subclass must provide an implementation of this method.
     *
     * @return the next byte of data, or <code>-1</code> if the end of the stream is reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read() throws IOException {
        if (current == null || current.isEmpty()) {
            int c = inputStream.read();

            if (!buffers.empty()) {
                buffers.peek().offer(c);
            }

            return c;
        } else {
            return current.poll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long skip(long n) throws IOException {
        return inputStream.skip(n);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    /**
     * Marks the current position in this input stream. A subsequent call to the <code>reset</code> method repositions
     * this stream at the last marked position so that subsequent reads re-read the same bytes.
     * <p>
     * <p> The <code>readlimit</code> arguments tells this input stream to allow that many bytes to be read before the
     * mark position gets invalidated.
     * <p>
     * <p> The general contract of <code>mark</code> is that, if the method <code>markSupported</code> returns
     * <code>true</code>, the stream somehow remembers all the bytes read after the call to <code>mark</code> and stands
     * ready to supply those same bytes again if and whenever the method <code>reset</code> is called.  However, the
     * stream is not required to remember any data at all if more than <code>readlimit</code> bytes are read from the
     * stream before <code>reset</code> is called.
     * <p>
     * <p> Marking a closed stream should not have any effect on the stream.
     * <p>
     * <p> The <code>mark</code> method of <code>InputStream</code> does nothing.
     *
     * @param readlimit the maximum limit of bytes that can be read before the mark position becomes invalid.
     * @see java.io.InputStream#reset()
     */
    public synchronized void mark(int readlimit) {
        buffers.push(new SizeLimitedQueue<>(readlimit));
    }

    /**
     * Repositions this stream to the position at the time the <code>mark</code> method was last called on this input
     * stream.
     * <p>
     * <p> The general contract of <code>reset</code> is:
     * <p>
     * <ul> <li> If the method <code>markSupported</code> returns <code>true</code>, then:
     * <p>
     * <ul><li> If the method <code>mark</code> has not been called since the stream was created, or the number of bytes
     * read from the stream since <code>mark</code> was last called is larger than the argument to <code>mark</code> at
     * that last call, then an <code>IOException</code> might be thrown.
     * <p>
     * <li> If such an <code>IOException</code> is not thrown, then the stream is reset to a state such that all the
     * bytes read since the most recent call to <code>mark</code> (or since the start of the file, if <code>mark</code>
     * has not been called) will be resupplied to subsequent callers of the <code>read</code> method, followed by any
     * bytes that otherwise would have been the next input data as of the time of the call to <code>reset</code>. </ul>
     *
     * @throws IOException if this stream has not been marked or if the mark has been invalidated.
     * @see java.io.InputStream#mark(int)
     * @see java.io.IOException
     */
    @Override
    public synchronized void reset() throws IOException {
        try {
            current = buffers.pop();
        } catch (EmptyStackException e) {
            throw new IOException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean markSupported() {
        return true;
    }

    /**
     * This {@link Queue} has a maximum capacity. Further elements will be ignored.
     *
     * @param <T> The {@link Queue}s type.
     */
    private class SizeLimitedQueue<T> extends AbstractQueue<T> implements Queue<T> {

        /**
         * The maximum capacity.
         */
        private final int maximumCapacity;

        /**
         * A {@link List} holding this queues elements.
         */
        private final List<T> elements;

        /**
         * Create a new instance with a given maximum capacity.
         *
         * @param maximumCapacity The maximum capacity.
         */
        public SizeLimitedQueue(final int maximumCapacity) {
            this.maximumCapacity = maximumCapacity;
            this.elements = new ArrayList<>(maximumCapacity);
        }

        @Override
        public Iterator<T> iterator() {
            return elements.iterator();
        }

        @Override
        public int size() {
            return elements.size();
        }

        @Override
        public boolean offer(T t) {
            if (elements.size() >= maximumCapacity) {
                return false;
            } else {
                elements.add(t);
                return true;
            }
        }

        @Override
        public T poll() {
            return elements.remove(0);
        }

        @Override
        public T peek() {
            return elements.get(0);
        }
    }
}
