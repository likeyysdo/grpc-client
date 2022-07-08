package com.byco.remotejdbc.rpc;

import com.byco.remotejdbc.type.RemoteType;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.InvalidProtocolBufferException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @Classname ManualCovert
 * @Description TODO
 * @Date 2022/7/4 15:35
 * @Created by byco
 */
public class ManualCovert {

    public static void main(String[] args) throws InvalidProtocolBufferException {
    f1();
    }

    public static  void f2(){
        System.out.println(Integer.MAX_VALUE);
        RemoteType t = RemoteType.BIGINT;
    }


    public static  void f1() throws InvalidProtocolBufferException {
        Int64Value.Builder builder = Int64Value.newBuilder();
        builder.setValue(-1123L);
        byte[] ba = builder.build().toByteArray();
        System.out.println(Arrays.toString(ba));
        long s = Int64Value.parseFrom(ba).getValue();
        System.out.println(s);

        builder.setValue(1232323L);
        ba = builder.build().toByteArray();
        System.out.println(Arrays.toString(ba));
        s = Int64Value.parseFrom(ba).getValue();
        System.out.println(s);
        System.out.println(Arrays.toString(Int32Value.newBuilder()
            .setValue(1232323)
            .build().toByteArray()));
        System.out.println(Arrays.toString(ByteBuffer.allocate(4).putInt(-1123).array()));
    }
}
