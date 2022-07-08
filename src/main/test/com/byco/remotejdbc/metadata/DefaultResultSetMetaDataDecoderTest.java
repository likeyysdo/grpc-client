package com.byco.remotejdbc.metadata;

import com.byco.remotejdbc.decode.ResultRowDecodeFactory;
import com.byco.remotejdbc.encode.ResultRowEncodeFactory;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * @Classname DefaultResultSetMetaDataDecoderTest
 * @Description TODO
 * @Date 2022/7/8 15:38
 * @Created by byco
 */
class DefaultResultSetMetaDataDecoderTest {
    @Test
    void fun1() throws Exception{
        var url = "jdbc:mysql://localhost/dwh";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (var con = DriverManager.getConnection(url,"root","admin"); var st = con.createStatement()) {
            //st.execute("SELECT * FROM `bundle`");
            try (var rs = st.executeQuery("SELECT * from bundle1")) {
                ResultSetMetaData metaData = rs.getMetaData();
                byte[] meta = new DefaultResultSetMetaDataEncoder().encode(metaData);



                ResultRowEncodeFactory factory = new ResultRowEncodeFactory.Builder( rs.getMetaData()).build();
                byte[] result = new byte[0];
                while (rs.next()) {
                    factory.add(rs);
                    result = factory.encode();
                }

                System.out.println("//Decode");
                ResultSetMetaData decodeMeta = new DefaultResultSetMetaDataDecoder().decode(meta);
                System.out.println(decodeMeta);
                ResultRowDecodeFactory factory1 = new ResultRowDecodeFactory.Builder(decodeMeta).build();
                Object[] rr = factory1.read(result);
                System.out.println(Arrays.toString(rr));

            }
        }
    }

}