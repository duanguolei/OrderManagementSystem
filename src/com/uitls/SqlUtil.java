package com.uitls;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SqlUtil {
    public static Connection getConnection(){
        Connection connection=null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/text?useSSL=false",
                    "root", "123456");

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return connection;

    }
    public static void close(ResultSet rs, PreparedStatement preparedStatement, Connection conn){
        try {
//            判断传进来的对象是否存在
            if (rs != null) {
                rs.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void close(PreparedStatement preStatement, Connection conn) {
        try {
            if (preStatement != null) {
                preStatement.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
