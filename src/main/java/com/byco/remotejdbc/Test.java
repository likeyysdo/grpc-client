package com.byco.remotejdbc;

import com.byco.remotejdbc.type.RemoteType;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * @Classname Test
 * @Description TODO
 * @Date 2022/6/28 16:58
 * @Created by byco
 */
public class Test {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        var url = "jdbc:mysql://localhost/dwh";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (var con = DriverManager.getConnection(url,"root","admin"); var st = con.createStatement()) {
            //st.execute("SELECT * FROM `bundle`");
            try (var rs = st.executeQuery("SELECT * from bundle")) {
                ResultSetMetaData metaData = rs.getMetaData();
                int count = metaData.getColumnCount();
                for( int i = 1 ; i <= count ;i++){
                    System.out.println("id " + i);
                    System.out.println("getColumnName " + metaData.getColumnName(i));
                    System.out.println("getColumnLabel " + metaData.getColumnLabel(i));
                    System.out.println("getColumnType " + metaData.getColumnType(i));
                    System.out.println("getColumnTypeName " + metaData.getColumnTypeName(i));
                    System.out.println();
                }
                while (rs.next()) {

                    //System.out.println("rs[1]=" + rs.getString("hahaha"));
                }
            }
        }
    }
}
