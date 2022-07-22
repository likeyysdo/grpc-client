package com.lncn.remotejdbc.encode;

/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;


/**
 * A speedy alternative to {@link java.io.ByteArrayOutputStream}. Note that
 * this variant does <i>not</i> extend {@code ByteArrayOutputStream}, unlike
 * its sibling {@link ResizableByteArrayOutputStream}.
 *
 * <p>Unlike {@link java.io.ByteArrayOutputStream}, this implementation is backed
 * by an {@link java.util.ArrayDeque} of {@code byte[]} instead of 1 constantly
 * resizing {@code byte[]}. It does not copy buffers when it gets expanded.
 *
 * <p>The initial buffer is only created when the stream is first written.
 * There is also no copying of the internal buffer if its contents is extracted
 * with the {@link #writeTo(OutputStream)} method.
 *
 * @author Craig Andrews
 * @author Juergen Hoeller
 * @since 4.2
 * @see #resize
 * @see ResizableByteArrayOutputStream
 */
public class FastByteArrayOutputStream extends OutputStream {

    private static final int DEFAULT_BLOCK_SIZE = 256;


    // The buffers used to store the content bytes
    private final Deque<byte[]> buffers = new ArrayDeque<>();

    // The size, in bytes, to use when allocating the first byte[]
    private final int initialBlockSize;

    // The size, in bytes, to use when allocating the next byte[]
    private int nextBlockSize = 0;

    // The number of bytes in previous buffers.
    // (The number of bytes in the current buffer is in 'index'.)
    private int alreadyBufferedSize = 0;

    // The index in the byte[] found at buffers.getLast() to be written next
    private int index = 0;

    // Is the stream closed?
    private boolean closed = false;


    /**
     * Create a new <code>FastByteArrayOutputStream</code>
     * with the default initial capacity of 256 bytes.
     */
    public FastByteArrayOutputStream() {
        this(DEFAULT_BLOCK_SIZE);
    }

    /**
     * Create a new <code>FastByteArrayOutputStream</code>
     * with the specified initial capacity.
     * @param initialBlockSize the initial buffer size in bytes
     */
    public FastByteArrayOutputStream(int initialBlockSize) {
        if( initialBlockSize <= 0 ) throw new IllegalArgumentException("Initial block size must be greater than 0");
        this.initialBlockSize = initialBlockSize;
        this.nextBlockSize = initialBlockSize;
    }


    // Overridden methods

    @Override
    public void write(int datum) throws IOException {
        if (this.closed) {
            throw new IOException("Stream closed");
        }
        else {
            if (this.buffers.peekLast() == null || this.buffers.getLast().length == this.index) {
                addBuffer(1);
            }
            // store the byte
            this.buffers.getLast()[this.index++] = (byte) datum;
        }
    }

    @Override
    public void write(byte[] data, int offset, int length) throws IOException {
        if (offset < 0 || offset + length > data.length || length < 0) {
            throw new IndexOutOfBoundsException();
        }
        else if (this.closed) {
            throw new IOException("Stream closed");
        }
        else {
            if (this.buffers.peekLast() == null || this.buffers.getLast().length == this.index) {
                addBuffer(length);
            }
            if (this.index + length > this.buffers.getLast().length) {
                int pos = offset;
                do {
                    if (this.index == this.buffers.getLast().length) {
                        addBuffer(length);
                    }
                    int copyLength = this.buffers.getLast().length - this.index;
                    if (length < copyLength) {
                        copyLength = length;
                    }
                    System.arraycopy(data, pos, this.buffers.getLast(), this.index, copyLength);
                    pos += copyLength;
                    this.index += copyLength;
                    length -= copyLength;
                }
                while (length > 0);
            }
            else {
                // copy in the sub-array
                System.arraycopy(data, offset, this.buffers.getLast(), this.index, length);
                this.index += length;
            }
        }
    }

    @Override
    public void close() {
        this.closed = true;
    }

    /**
     * Convert the buffer's contents into a string decoding bytes using the
     * platform's default character set. The length of the new <tt>String</tt>
     * is a function of the character set, and hence may not be equal to the
     * size of the buffer.
     * <p>This method always replaces malformed-input and unmappable-character
     * sequences with the default replacement string for the platform's
     * default character set. The {@linkplain java.nio.charset.CharsetDecoder}
     * class should be used when more control over the decoding process is
     * required.
     * @return a String decoded from the buffer's contents
     */
    @Override
    public String toString() {
        return new String(toByteArrayUnsafe());
    }


    // Custom methods

    /**
     * Return the number of bytes stored in this <code>FastByteArrayOutputStream</code>.
     */
    public int size() {
        return (this.alreadyBufferedSize + this.index);
    }

    /**
     * Convert the stream's data to a byte array and return the byte array.
     * <p>Also replaces the internal structures with the byte array to conserve memory:
     * if the byte array is being made anyways, mind as well as use it. This approach
     * also means that if this method is called twice without any writes in between,
     * the second call is a no-op.
     * <p>This method is "unsafe" as it returns the internal buffer.
     * Callers should not modify the returned buffer.
     * @return the current contents of this output stream, as a byte array.
     * @see #size()
     * @see #toByteArray()
     */
    public byte[] toByteArrayUnsafe() {
        int totalSize = size();
        if (totalSize == 0) {
            return new byte[0];
        }
        resize(totalSize);
        return this.buffers.getFirst();
    }

    /**
     * Creates a newly allocated byte array.
     * <p>Its size is the current
     * size of this output stream and the valid contents of the buffer
     * have been copied into it.</p>
     * @return the current contents of this output stream, as a byte array.
     * @see #size()
     * @see #toByteArrayUnsafe()
     */
    public byte[] toByteArray() {
        byte[] bytesUnsafe = toByteArrayUnsafe();
        return bytesUnsafe.clone();
    }

    /**
     * Reset the contents of this <code>FastByteArrayOutputStream</code>.
     * <p>All currently accumulated output in the output stream is discarded.
     * The output stream can be used again.
     */
    public void reset() {
        this.buffers.clear();
        this.nextBlockSize = this.initialBlockSize;
        this.closed = false;
        this.index = 0;
        this.alreadyBufferedSize = 0;
    }


    /**
     * Write the buffers content to the given OutputStream.
     * @param out the OutputStream to write to
     */
    public void writeTo(OutputStream out) throws IOException {
        Iterator<byte[]> it = this.buffers.iterator();
        while (it.hasNext()) {
            byte[] bytes = it.next();
            if (it.hasNext()) {
                out.write(bytes, 0, bytes.length);
            }
            else {
                out.write(bytes, 0, this.index);
            }
        }
    }

    /**
     * Resize the internal buffer size to a specified capacity.
     * @param targetCapacity the desired size of the buffer
     * @throws IllegalArgumentException if the given capacity is smaller than
     * the actual size of the content stored in the buffer already
     * @see FastByteArrayOutputStream#size()
     */
    public void resize(int targetCapacity) {
        if( targetCapacity < size() ) throw new IllegalArgumentException("New capacity must not be smaller than current size");
        if (this.buffers.peekFirst() == null) {
            this.nextBlockSize = targetCapacity - size();
        }
        else if (size() == targetCapacity && this.buffers.getFirst().length == targetCapacity) {
            // do nothing - already at the targetCapacity
        }
        else {
            int totalSize = size();
            byte[] data = new byte[targetCapacity];
            int pos = 0;
            Iterator<byte[]> it = this.buffers.iterator();
            while (it.hasNext()) {
                byte[] bytes = it.next();
                if (it.hasNext()) {
                    System.arraycopy(bytes, 0, data, pos, bytes.length);
                    pos += bytes.length;
                }
                else {
                    System.arraycopy(bytes, 0, data, pos, this.index);
                }
            }
            this.buffers.clear();
            this.buffers.add(data);
            this.index = totalSize;
            this.alreadyBufferedSize = 0;
        }
    }

    /**
     * Create a new buffer and store it in the ArrayDeque.
     * <p>Adds a new buffer that can store at least {@code minCapacity} bytes.
     */
    private void addBuffer(int minCapacity) {
        if (this.buffers.peekLast() != null) {
            this.alreadyBufferedSize += this.index;
            this.index = 0;
        }
        if (this.nextBlockSize < minCapacity) {
            this.nextBlockSize = nextPowerOf2(minCapacity);
        }
        this.buffers.add(new byte[this.nextBlockSize]);
        this.nextBlockSize *= 2;  // block size doubles each time
    }

    /**
     * Get the next power of 2 of a number (ex, the next power of 2 of 119 is 128).
     */
    private static int nextPowerOf2(int val) {
        val--;
        val = (val >> 1) | val;
        val = (val >> 2) | val;
        val = (val >> 4) | val;
        val = (val >> 8) | val;
        val = (val >> 16) | val;
        val++;
        return val;
    }



}