package com.byco.remotejdbc.encode;

import com.byco.remotejdbc.decode.ResultRowDecodeFactory;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * @Classname ResultRowEncodeFactoryTest
 * @Description TODO
 * @Date 2022/7/7 22:27
 * @Created by byco
 */
class ResultRowEncodeFactoryTest {


    @Test
    void fun1() throws Exception{
        var url = "jdbc:mysql://localhost/dwh";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (var con = DriverManager.getConnection(url,"root","admin"); var st = con.createStatement()) {
            //st.execute("SELECT * FROM `bundle`");
            try (var rs = st.executeQuery("SELECT * from bundle1")) {
                ResultRowEncodeFactory factory = new ResultRowEncodeFactory.Builder( rs.getMetaData()).build();
                byte[] result = new byte[0];
                while (rs.next()) {
                    factory.add(rs);
                    result = factory.encode();
                }

                System.out.println("//Decode");
                ResultRowDecodeFactory factory1 = new ResultRowDecodeFactory.Builder( rs.getMetaData()).build();
                Object[] rr = factory1.read(result);
                System.out.println(Arrays.toString(rr));
//                CodedInputStream input = CodedInputStream.newInstance(result);
//                System.out.println(input.readSInt32());
//                System.out.println(input.readString());
//                System.out.println(input.readString());
//                System.out.println(input.readString());
//                System.out.println(input.readSInt32());
//                System.out.println(input.readUInt64());
//                System.out.println(input.readString());
//                System.out.println(input.readString());
//                System.out.println(input.readString());
            }
        }
    }

}