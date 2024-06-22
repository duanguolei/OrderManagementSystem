package com.uitls;
import com.enty.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Creta_table_utils {
    public void creat_user_table() throws SQLException {
        Connection cnn=SqlUtil.getConnection();
        PreparedStatement stmt=null;
        String creata_table_sql="CREATE TABLE IF NOT EXISTS regsterusers (\n" +
                "    user_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    username VARCHAR(255) NOT NULL,\n" +
                "    email VARCHAR(255) NOT NULL,\n" +
                "    password VARCHAR(255) NOT NULL,\n" +
                "    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                ");";
        try {
            stmt=cnn.prepareStatement(creata_table_sql);
            stmt.execute();
            System.out.println("注册表已准备好");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            SqlUtil.close(stmt,cnn);
        }

    }
    public void  creat_table(String sql,String table_name){
        Connection cnn=SqlUtil.getConnection();
        PreparedStatement stmt=null;
        try {
            stmt=cnn.prepareStatement(sql);
            stmt.execute();
            System.out.println(table_name+"创建成功");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            SqlUtil.close(stmt,cnn);
        }
    }


    public static void main(String[] args) {
        String user_sql="CREATE TABLE IF NOT EXISTS regsterusers (\n" +
                "    user_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    username VARCHAR(255) NOT NULL,\n" +
                "    email VARCHAR(255) NOT NULL,\n" +
                "    password VARCHAR(255) NOT NULL,\n" +
                "    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                ");";

            String managers_sql="CREATE TABLE IF NOT EXISTS managers (\n" +
                    "    manager_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                    "    name VARCHAR(255) NOT NULL,\n" +
                    "    phone VARCHAR(15) NOT NULL\n" +
                    ");\n";
            String products_sql="CREATE TABLE IF NOT EXISTS  products (\n" +
                    "    product_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                    "    product_name VARCHAR(255) NOT NULL,\n" +
                    "    price DECIMAL(10, 2) NOT NULL,\n" +
                    "    quantity INT NOT NULL\n" +
                    ");\n";

        String orders_sql = "CREATE TABLE IF NOT EXISTS orders (\n" +
                "    order_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    order_name VARCHAR(255) NOT NULL,\n" +
                "    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                "    total_price DECIMAL(10, 2) DEFAULT 0,\n" +
                "    is_completed BOOLEAN DEFAULT 0\n" +
                ");\n";


        String manager_order_sql = "CREATE TABLE IF NOT EXISTS manager_order (\n" +
                "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    manager_id INT,\n" +
                "    order_id INT,\n" +
                "    product_id INT,\n" +
                "    is_completed BOOLEAN DEFAULT false,\n" +
                "    quantity INT NOT NULL,\n" +
                "    FOREIGN KEY (manager_id) REFERENCES managers(manager_id),\n" +
                "    FOREIGN KEY (product_id) REFERENCES products(product_id),\n" +
                "    FOREIGN KEY (order_id) REFERENCES orders(order_id)\n" +
                ");\n";


        Creta_table_utils tableUtils=new Creta_table_utils();
        tableUtils.creat_table(user_sql, "regsterusers");
        tableUtils.creat_table(managers_sql, "managers");
        tableUtils.creat_table(products_sql, "products");
        tableUtils.creat_table(orders_sql, "orders");
        tableUtils.creat_table(manager_order_sql, "manager_order");
        System.out.println("数据库表准备好");
    }



}
