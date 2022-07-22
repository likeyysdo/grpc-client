package com.lncn.remotejdbc;

import com.lncn.remotejdbc.type.JdbcToJavaTypeFunction;
import com.lncn.remotejdbc.type.RemoteType;
import com.lncn.remotejdbc.type.RemoteTypeConverter;

import java.math.BigDecimal;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.util.Date;
import org.junit.jupiter.api.Test;

/**
 * @Classname TestTest
 * @Description TODO
 * @Date 2022/7/6 13:53
 * @Created by byco
 */
class TestTest {

    @Test
    void mainfun() throws  Exception{
        var url = "jdbc:mysql://localhost/dwh";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (var con = DriverManager.getConnection(url,"root","admin"); var st = con.createStatement()) {
            //st.execute("SELECT * FROM `bundle`");
            try (var rs = st.executeQuery("SELECT * from bundle")) {
                ResultSetMetaData metaData = rs.getMetaData();
                int count = metaData.getColumnCount();
                JdbcToJavaTypeFunction[] fun = new JdbcToJavaTypeFunction[count];
                for( int i = 1 ; i <= count ;i++){
                    System.out.println("id " + i);
                    System.out.println("getColumnName " + metaData.getColumnName(i));
                    System.out.println("getColumnLabel " + metaData.getColumnLabel(i));
                    System.out.println("getColumnType " + metaData.getColumnType(i));
                    System.out.println("getColumnTypeName " + metaData.getColumnTypeName(i));
                    System.out.println();
                    fun[i-1] = RemoteTypeConverter.toJavaType(metaData.getColumnType(i));
                }
                while (rs.next()) {
                    for( int i = 1 ; i <= count ;i++){
                        System.out.println("id " + i);
                        System.out.println("ColumnName " + metaData.getColumnName(i));
                        System.out.println(fun[i-1].apply(i,rs));
                    }
                    System.out.println();
                    //System.out.println("rs[1]=" + rs.getString("hahaha"));
                }
            }
        }
    }


    @Test
    void fun1(){
        System.out.println(RemoteType.BIGINT.jdbcType);
        System.out.println(RemoteType.getType(4).ordinal());
        Date d = new Date();
        System.out.println(d.getTime());
        java.sql.Date d1 = new java.sql.Date(d.getTime());
        System.out.println(d1.toString());
        BigDecimal b = new BigDecimal("-123.2323");
        System.out.println(b.unscaledValue().longValue());
        System.out.println((long)(123.4232D*1000));
    }

    @Test
    void fun2(){
        System.out.println(Long.MAX_VALUE);
        System.out.println(Long.MAX_VALUE/10000000);
    }

}