package com.byco.remotejdbc.encode;

import com.byco.remotejdbc.decode.ResultRowDecodeFactory;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
            DatabaseMetaData m = con.getMetaData();
            System.out.println(m.getDatabaseProductName());
            System.out.println(m.getDriverName());
            System.out.println(m.getURL());
            //System.out.println(m.getTypeInfo());
            ResultSet set = m.getTypeInfo();
            ResultSetMetaData rm = set.getMetaData();
            System.out.println(rm.getColumnCount());
            for( int i = 1 ; i <= rm.getColumnCount();i++){
                System.out.print(rm.getColumnName(i));
                System.out.print(" ");
            }
            System.out.println();
            while( set.next() ){
                for( int i = 1 ; i <= rm.getColumnCount();i++){
                    System.out.print(set.getString(i));
                    System.out.print(" ");
                }
                System.out.println();
            }
//            try (var rs = st.executeQuery("SELECT * from bundle1")) {
//                ResultRowEncodeFactory factory = new ResultRowEncodeFactory.Builder( rs.getMetaData()).build();
//                //System.out.println(rs.getWarnings().toString());
//                byte[] result = new byte[0];
//                while (rs.next()) {
//                    factory.add(rs);
//                    result = factory.encode();
//                }
//                System.out.println("//Decode");
//                ResultRowDecodeFactory factory1 = new ResultRowDecodeFactory.Builder( rs.getMetaData()).build();
//                Object[] rr = factory1.read(result);
//                System.out.println(Arrays.toString(rr));
//         }
        }
    }

}