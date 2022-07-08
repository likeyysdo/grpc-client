package com.byco.remotejdbc.encode.deprecate;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.MessageLite;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Classname ResultRowEncoder
 * @Description TODO
 * @Date 2022/7/7 10:47
 * @Created by byco
 */
public class ResultRowEncoderImpl2 implements ResultRowEncoder123 {

    private static final int BYTE_ARRAY_DEFAULT_SIZE = 1024 * 16;
    private static final int BYTE_ARRAY_MB_SIZE = 1024 * 1024;
    private byte[] buf;
    private CodedOutputStream encoder;
    private int lastLength;
    private ResultRowEncoderImpl2(){

    }

    private void addCapacity(int capacity){
        int expectSize = buf.length + capacity;
        int newSize = buf.length;
        if( expectSize < BYTE_ARRAY_MB_SIZE ){
            while( expectSize > newSize ){
                newSize <<= 1;
            }
        }else{
            
        }
    }

    public static ResultRowEncoderImpl2 getInstance(int initialSize){
        ResultRowEncoderImpl2 encoder = new ResultRowEncoderImpl2();
        encoder.buf = new byte[initialSize];
        encoder.encoder = CodedOutputStream.newInstance(encoder.buf);
        encoder.lastLength = 0;
        return encoder;
    }

    public static ResultRowEncoderImpl2 getInstance(){
        ResultRowEncoderImpl2 encoder = new ResultRowEncoderImpl2();
        encoder.buf = new byte[BYTE_ARRAY_DEFAULT_SIZE];
        encoder.encoder = CodedOutputStream.newInstance(encoder.buf);
        encoder.lastLength = 0;
        return encoder;
    }

    /**
     * Write an {@code int32} field to the stream.
     *
     * @param value
     */
    @Override
    public void writeInt32NoTag(int value) throws IOException {
        encoder.writeInt32NoTag(value);
    }

    /**
     * Write a {@code uint32} field to the stream.
     *
     * @param value
     */
    @Override
    public void writeUInt32NoTag(int value) throws IOException {
        encoder.writeUInt32NoTag(value);
    }

    /**
     * Write a {@code fixed32} field to the stream.
     *
     * @param value
     */
    @Override
    public void writeFixed32NoTag(int value) throws IOException {
        encoder.writeFixed32NoTag(value);
    }

    /**
     * Write a {@code uint64} field to the stream.
     *
     * @param value
     */
    @Override
    public void writeUInt64NoTag(long value) throws IOException {
        encoder.writeUInt64NoTag(value);
    }

    /**
     * Write a {@code fixed64} field to the stream.
     *
     * @param value
     */
    @Override
    public void writeFixed64NoTag(long value) throws IOException {
        encoder.writeFixed64NoTag(value);
    }

    /**
     * Write a {@code string} field to the stream.
     *
     * @param value
     */
    @Override
    public void writeStringNoTag(String value) throws IOException {
        encoder.writeStringNoTag(value);
    }

    /**
     * Write a {@code bytes} field to the stream.
     *
     * @param value
     */
    @Override
    public void writeBytesNoTag(ByteString value) throws IOException {
        encoder.writeBytesNoTag(value);
    }

    /**
     * Write an embedded message field to the stream.
     *
     * @param value
     */
    @Override
    public void writeMessageNoTag(MessageLite value) throws IOException {
        encoder.writeMessageNoTag(value);
    }

    @Override
    public void write(byte value) throws IOException {
        encoder.write(value);
    }

    @Override
    public void write(byte[] value, int offset, int length) throws IOException {
        encoder.write(value,offset,length);
    }

    @Override
    public void writeLazy(byte[] value, int offset, int length) throws IOException {
        encoder.writeLazy(value,offset,length);
    }

    @Override
    public void write(ByteBuffer value) throws IOException {
        encoder.write(value);
    }

    @Override
    public void writeLazy(ByteBuffer value) throws IOException {
        encoder.writeLazy(value);
    }

    /**
     * Flushes the stream and forces any buffered bytes to be written. This does not flush the
     * underlying OutputStream.
     */
    @Override
    public void flush() throws IOException {
        encoder.flush();
    }

    /**
     * If writing to a flat array, return the space left in the array. Otherwise, throws {@code
     * UnsupportedOperationException}.
     */
    @Override
    public int spaceLeft() {
        return encoder.spaceLeft();
    }

    /**
     * Get the total number of bytes successfully written to this stream. The returned value is not
     * guaranteed to be accurate if exceptions have been found in the middle of writing.
     */
    @Override
    public int getTotalBytesWritten() {
        return encoder.getTotalBytesWritten();
    }


    /**
     * Encode and write a tag.
     *
     * @param fieldNumber
     * @param wireType
     */
    @Override
    public void writeTag(int fieldNumber, int wireType) throws IOException {
        encoder.writeTag(fieldNumber,wireType);
    }

    /**
     * Write an {@code int32} field, including tag, to the stream.
     *
     * @param fieldNumber
     * @param value
     */
    @Override
    public void writeInt32(int fieldNumber, int value) throws IOException {
        encoder.writeInt32(fieldNumber,value);
    }

    /**
     * Write a {@code uint32} field, including tag, to the stream.
     *
     * @param fieldNumber
     * @param value
     */
    @Override
    public void writeUInt32(int fieldNumber, int value) throws IOException {
        encoder.writeUInt32(fieldNumber,value);
    }

    /**
     * Write a {@code fixed32} field, including tag, to the stream.
     *
     * @param fieldNumber
     * @param value
     */
    @Override
    public void writeFixed32(int fieldNumber, int value) throws IOException {
        encoder.writeFixed32(fieldNumber,value);
    }

    /**
     * Write a {@code uint64} field, including tag, to the stream.
     *
     * @param fieldNumber
     * @param value
     */
    @Override
    public void writeUInt64(int fieldNumber, long value) throws IOException {
        encoder.writeUInt64(fieldNumber,value);
    }

    /**
     * Write a {@code fixed64} field, including tag, to the stream.
     *
     * @param fieldNumber
     * @param value
     */
    @Override
    public void writeFixed64(int fieldNumber, long value) throws IOException {
        encoder.writeFixed64(fieldNumber,value);
    }

    /**
     * Write a {@code bool} field, including tag, to the stream.
     *
     * @param fieldNumber
     * @param value
     */
    @Override
    public void writeBool(int fieldNumber, boolean value) throws IOException {
        encoder.writeBool(fieldNumber,value);
    }

    /**
     * Write a {@code string} field, including tag, to the stream.
     *
     * @param fieldNumber
     * @param value
     */
    @Override
    public void writeString(int fieldNumber, String value) throws IOException {
        encoder.writeString(fieldNumber,value);
    }

    /**
     * Write a {@code bytes} field, including tag, to the stream.
     *
     * @param fieldNumber
     * @param value
     */
    @Override
    public void writeBytes(int fieldNumber, ByteString value) throws IOException {
        encoder.writeBytes(fieldNumber,value);
    }

    /**
     * Write a {@code bytes} field, including tag, to the stream.
     *
     * @param fieldNumber
     * @param value
     */
    @Override
    public void writeByteArray(int fieldNumber, byte[] value) throws IOException {
        encoder.writeByteArray(fieldNumber,value);
    }

    /**
     * Write a {@code bytes} field, including tag, to the stream.
     *
     * @param fieldNumber
     * @param value
     * @param offset
     * @param length
     */
    @Override
    public void writeByteArray(int fieldNumber, byte[] value, int offset, int length)
        throws IOException {
        encoder.writeByteArray(fieldNumber,value);
    }

    /**
     * Write a {@code bytes} field, including tag, to the stream. This method will write all content
     * of the ByteBuffer regardless of the current position and limit (i.e., the number of bytes to be
     * written is value.capacity(), not value.remaining()). Furthermore, this method doesn't alter the
     * state of the passed-in ByteBuffer. Its position, limit, mark, etc. will remain unchanged. If
     * you only want to write the remaining bytes of a ByteBuffer, you can call {@code
     * writeByteBuffer(fieldNumber, byteBuffer.slice())}.
     *
     * @param fieldNumber
     * @param value
     */
    @Override
    public void writeByteBuffer(int fieldNumber, ByteBuffer value) throws IOException {
        encoder.writeByteBuffer(fieldNumber,value);
    }

    /**
     * Write a ByteBuffer. This method will write all content of the ByteBuffer regardless of the
     * current position and limit (i.e., the number of bytes to be written is value.capacity(), not
     * value.remaining()). Furthermore, this method doesn't alter the state of the passed-in
     * ByteBuffer. Its position, limit, mark, etc. will remain unchanged. If you only want to write
     * the remaining bytes of a ByteBuffer, you can call {@code writeRawBytes(byteBuffer.slice())}.
     *
     * @param value
     */
    @Override
    public void writeRawBytes(ByteBuffer value) throws IOException {
        encoder.writeRawBytes( value);
    }

    /**
     * Write an embedded message field, including tag, to the stream.
     *
     * @param fieldNumber
     * @param value
     */
    @Override
    public void writeMessage(int fieldNumber, MessageLite value) throws IOException {
        encoder.writeMessage(fieldNumber,value);
    }

    /**
     * Write a MessageSet extension field to the stream. For historical reasons, the wire format
     * differs from normal fields.
     *
     * @param fieldNumber
     * @param value
     */
    @Override
    public void writeMessageSetExtension(int fieldNumber, MessageLite value) throws IOException {
        encoder.writeMessageSetExtension(fieldNumber,value);
    }

    /**
     * Write an unparsed MessageSet extension field to the stream. For historical reasons, the wire
     * format differs from normal fields.
     *
     * @param fieldNumber
     * @param value
     */
    @Override
    public void writeRawMessageSetExtension(int fieldNumber, ByteString value) throws IOException {
        encoder.writeRawMessageSetExtension(fieldNumber,value);
    }

}
