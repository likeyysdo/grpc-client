package com.byco.remotejdbc.type;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * @Classname EncodeTest
 * @Description TODO
 * @Date 2022/7/6 23:24
 * @Created by byco
 */
public class EncodeTest {

    @Test
    void endecode() throws IOException {
        byte[] bytes = new byte[50];
        CodedOutputStream output = CodedOutputStream.newInstance(bytes);
        output.writeInt64(1,1000L);
        output.writeFixed64(2,2L);
        output.writeSInt64(3,-123L);
        output.writeString(3,"asdadwa");
        System.out.println(Arrays.toString(bytes));
        CodedInputStream input = CodedInputStream.newInstance(bytes);
        System.out.println(input.readTag());
        System.out.println(input.readInt64());
        System.out.println(input.readTag());
        System.out.println(input.readFixed64());
        System.out.println(input.readTag());
        System.out.println(input.readSInt64());
        System.out.println(input.readTag());
        System.out.println(input.readString());
    }

    @Test
    void endecodeWithNoTag() throws IOException {
        byte[] bytes = new byte[50];
        CodedOutputStream output = CodedOutputStream.newInstance(bytes);

        output.writeInt64NoTag(1000L);
        output.writeSInt64NoTag( -123L);
        output.writeStringNoTag( "asdadwa");
        System.out.println(Arrays.toString(bytes));
        CodedInputStream input = CodedInputStream.newInstance(bytes);
        System.out.println(input.readInt64());
        System.out.println(input.readSInt64());
        System.out.println(input.readString());
    }

    @Test
    void endecodeWithEmptyValue() throws IOException {
        byte[] bytes = new byte[50];
        CodedOutputStream output = CodedOutputStream.newInstance(bytes);
        output.writeStringNoTag( "");
        output.writeInt64NoTag(1000L);
        //output.writeStringNoTag( "cccc");
        System.out.println(Arrays.toString(bytes));
        CodedInputStream input = CodedInputStream.newInstance(bytes);
        System.out.println(input.readString());
        System.out.println(input.readInt64());
        System.out.println(input.isAtEnd());
        //System.out.println(input.readString());
    }
}
