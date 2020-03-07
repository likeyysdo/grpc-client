
import cn.orz.pascal.jdbc.MyConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author koduki
 */
public class Main {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        String url = "jdbc:myjdbc://localhost:80/testdb";

        Class.forName("cn.orz.pascal.jdbc.MyDriver");
        try (Connection con = DriverManager.getConnection(url)) {
            if (con instanceof MyConnection) {
                System.out.println("Load success!");
            }

        }
        System.out.println("End");
    }
}
