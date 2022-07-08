package com.byco.remotejdbc.encode;

import com.byco.remotejdbc.encode.deprecate.JavaTypeEncodeConsumer;
import com.byco.remotejdbc.encode.deprecate.ResultRowEncoderConverter;
import com.byco.remotejdbc.type.JdbcToJavaTypeFunction;
import com.byco.remotejdbc.type.RemoteTypeConverter;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * @Classname ResultRowEncoderConverterTest
 * @Description TODO
 * @Date 2022/7/7 16:28
 * @Created by byco
 */
class ResultRowEncoderConverterTest {


    @Test
    void fun1() throws Exception{
        var url = "jdbc:mysql://localhost/dwh";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (var con = DriverManager.getConnection(url,"root","admin"); var st = con.createStatement()) {
            //st.execute("SELECT * FROM `bundle`");
            try (var rs = st.executeQuery("SELECT * from bundle")) {
                ResultSetMetaData metaData = rs.getMetaData();
                int count = metaData.getColumnCount();
                JdbcToJavaTypeFunction[] typeList = new JdbcToJavaTypeFunction[count];
                JavaTypeEncodeConsumer[] encodeList = new JavaTypeEncodeConsumer[count];
                for( int i = 1 ; i <= count ;i++){
//                    System.out.println("id " + i);
//                    System.out.println("getColumnName " + metaData.getColumnName(i));
//                    System.out.println("getColumnLabel " + metaData.getColumnLabel(i));
//                    System.out.println("getColumnType " + metaData.getColumnType(i));
//                    System.out.println("getColumnTypeName " + metaData.getColumnTypeName(i));
//                    System.out.println();
                    System.out.print(metaData.getColumnName(i) + " " + metaData.getColumnTypeName(i)+ " , " );
                    typeList[i-1] = RemoteTypeConverter.toJavaType(metaData.getColumnType(i));
                    encodeList[i-1] = ResultRowEncoderConverter.getEncoder(metaData.getColumnType(i));
                }
                System.out.println();
                byte[] result = new byte[0];
                while (rs.next()) {
                    FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
                    CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(outputStream);
                    for( int i = 1 ; i <= count ;i++){
                        //System.out.println("id " + i);
                        //System.out.println("ColumnName " + metaData.getColumnName(i));
                        System.out.println(typeList[i-1].apply(i,rs));
                        encodeList[i-1].apply(codedOutputStream,typeList[i-1].apply(i,rs));
                    }
                    codedOutputStream.flush();
                    result = outputStream.toByteArrayUnsafe();
                    //System.out.println(result);
                    //System.out.println("rs[1]=" + rs.getString("hahaha"));
                }
                System.out.println("//Decode");
                CodedInputStream input = CodedInputStream.newInstance(result);
                System.out.println(input.readSInt32());
                System.out.println(input.readString());
                System.out.println(input.readString());
                System.out.println(input.readString());
                System.out.println(input.readSInt32());
                System.out.println(input.readUInt64());
                System.out.println(input.readString());
                System.out.println(input.readString());
                System.out.println(input.readString());
            }
        }
    }

    @Test
    void fun2() throws IOException {
        byte[] rawBytes = new byte[50];
        CodedOutputStream output = CodedOutputStream.newInstance(rawBytes);
        output.writeInt64NoTag(1000L);
        output.writeSInt64NoTag( -123L);
        output.writeStringNoTag( "asdadwa");

        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        CodedOutputStream output1 = CodedOutputStream.newInstance(outputStream);
        output1.writeInt64NoTag(1000L);
        output1.writeSInt64NoTag( -123L);
        output1.writeStringNoTag( "asdadwa");
        output1.flush();
        byte[] bytes = outputStream.toByteArray();

        System.out.println(Arrays.toString(rawBytes));
        System.out.println(Arrays.toString(outputStream.toByteArray()));


        CodedInputStream input = CodedInputStream.newInstance(bytes);
        System.out.println(input.readInt64());
        System.out.println(input.readSInt64());
        System.out.println(input.readString());
    }

}