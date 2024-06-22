package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.uitls.SqlUtil;
import java.util.Iterator;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.servlet.annotation.WebServlet;
import com.google.gson.reflect.TypeToken;
import java.util.Enumeration;
import java.sql.Statement;

@WebServlet("/api")
public class table_data extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");



        String sheetName = request.getParameter("sheetname");
        String type = request.getParameter("type");
        System.out.println(request.getParameter("datas"));

        String datasString = request.getParameter("datas");
        // 解析JSON字符串
        System.out.println(sheetName);

        JSONObject jsonObject=new JSONObject(datasString);

        String result = DataOperationService.executeOperation(sheetName, type, jsonObject);
        System.out.println(result);

        response.setContentType("application/json");

        PrintWriter out = response.getWriter();
        out.print(result);
        out.flush();

    }




static class DataOperationService {
    private static final String INSERT_SQL = "INSERT INTO %s (%s) VALUES (%s)";
    private static final String DELETE_SQL = "DELETE FROM %s WHERE %s";
//    private static final String SELECT_SQL = "SELECT * FROM %s WHERE %s";
    private static final String SELECT_SQL = "SELECT * FROM %s";
    private static final String UPDATE_SQL = "UPDATE %s SET %s WHERE %s";



    public static void updateOrdersTotalPrice(Connection connection) {

            String updateSql = "UPDATE orders o " +
                    "SET o.total_price = (" +
                    "    SELECT SUM(p.price * mo.quantity) " +
                    "    FROM manager_order mo " +
                    "    JOIN products p ON mo.product_id = p.product_id " +
                    "    WHERE mo.order_id = o.order_id" +
                    ") " ;

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
            int result = preparedStatement.executeUpdate();
            System.out.println("计算总价成功");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("计算总价失败");
        }

    }
    public static void updateOrdersCompletion(Connection connection) {
        String updateSql = "UPDATE orders o\n" +
                "SET o.is_completed = (\n" +
                "    SELECT\n" +
                "        CASE\n" +
                "            WHEN COUNT(*) = SUM(mo.is_completed) THEN 1\n" +
                "            ELSE 0\n" +
                "        END\n" +
                "    FROM\n" +
                "        manager_order mo\n" +
                "    WHERE\n" +
                "        mo.order_id = o.order_id\n" +
                "    GROUP BY\n" +
                "        mo.order_id\n" +
                ");\n";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
            int result = preparedStatement.executeUpdate();
            System.out.println("更新订单完成");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("更新状态失败");
        }
    }

    public static void updateprductQurity(Connection connection) {

        String updateSql = "UPDATE products p\n" +
                "SET p.quantity = p.quantity - (\n" +
                "    SELECT SUM(mo.quantity)\n" +
                "    FROM manager_order mo\n" +
                "    WHERE mo.product_id = p.product_id\n" +
                ")\n" +
                "WHERE EXISTS (\n" +
                "    SELECT 1\n" +
                "    FROM manager_order mo\n" +
                "    WHERE mo.product_id = p.product_id\n" +
                "    AND mo.is_completed=true \n" +
                ");\n";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
            int result = preparedStatement.executeUpdate();
            System.out.println("产品数量减少成功");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("产品数量减少失败");
        }

    }



    public static String findId(Connection connection, String sheetname, String key, Object  value) {
        // 构建 SQL 查询语句
        String id_label=null;
        if(sheetname.equals("managers")){
            id_label="manager_id";
        }
        else if(sheetname.equals("products")){
            id_label="product_id";
        }
        else if(sheetname.equals("orders")){
            id_label="order_id";
        }


        String sql = "SELECT "+id_label+" FROM " + sheetname + " WHERE " + key + " = '"+value+"'";
        System.out.println(sql);
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();


            if (resultSet.next()) {
                String id = resultSet.getString(id_label);
                return id;
            } else {
                System.out.println("在 " + sheetname + " 中未找到匹配的记录");
                return "-1"; // 表示未找到
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL 查询失败");
            return "-1"; // 表示查询失败
        }
    }



    public static String executeOperation(String sheetName, String type,   JSONObject  data) {
        Connection connection = SqlUtil.getConnection();

        System.out.println(sheetName);
        updateOrdersCompletion(connection);
        updateOrdersTotalPrice(connection);
        switch (type) {
            case "add":

                return executeInsert(sheetName, data, connection);
            case "delete":

                return executeDelete(sheetName, data, connection);
            case "search":
                return executeSelect(sheetName, data, connection);
            case "update":
                return executeUpdate(sheetName, data, connection);
            default:
                return "不支持的操作类型";

        }


    }

    private static String executeInsert(String sheetName,    JSONObject  data, Connection connection) {
        // 构建插入SQL语句
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
//
        Iterator<String> key_inter = data.keys();

        // 遍历键值对
        while (key_inter.hasNext()) {
            String key = key_inter.next();
            Object value = data.get(key);
            if(sheetName.equals("manager_order")){

                if(key.equals("name")){
                    value=findId(connection,"managers",key,value);
                    key="manager_id";
                }
                else if(key.equals("product_name")){
                    value=findId(connection,"products",key,value);
                    key="product_id";
                }
                else if(key.equals("order_name")){
                    value=findId(connection,"orders",key,value);
                    key="order_id";

                }
                System.out.println("Key: " + key + ", Value: " + value);
                columns.append(key).append(", ");
                values.append(value).append(", ");
            }
            else {
            System.out.println("Key: " + key + ", Value: " + value);
            columns.append(key).append(", ");
            values.append ("'").append(value).append("', ");
        }}


        columns.setLength(columns.length() - 2);
        values.setLength(values.length() - 2);

        String sql = String.format(INSERT_SQL, sheetName, columns.toString(), values.toString());

        System.out.println(sql);
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();
            if(sheetName.equals("manager_order")){
                updateOrdersTotalPrice(connection);
                updateprductQurity(connection);
            }
            return "成功插入数据";
        } catch (SQLException e) {
            e.printStackTrace();
            return "插入数据失败";
//        }

    }}

    private static String executeDelete(String sheetName, JSONObject  data, Connection connection) {
        System.out.println("执行删除");


        Iterator<String> keys = data.keys();
        String conditions = null;
        // 遍历键值对
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = data.get(key);

            System.out.println("Key: " + key + ", Value: " + value);
            conditions = String.format("%s =%s", key, value);
        }



        String sql = String.format(DELETE_SQL, sheetName, conditions);
        System.out.println(sql);
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int result = preparedStatement.executeUpdate();
            return "删除成功";
        } catch (SQLException e) {
            e.printStackTrace();
            return "删除数据失败";
        }
    }


    private static String executeSelect(String sheetName,   JSONObject data, Connection connection) {
        // 构建查询SQL语句

        System.out.println(sheetName);
        String sql;
        if(sheetName.equals("manager_order")){
            sql="SELECT\n" +
                    "    manager_order.id,\n" +
                    "    manager_order.quantity,\n" +
                    "    manager_order.is_completed,\n" +
                    "    managers.name AS manager_name,\n" +
                    "    orders.order_name AS order_name,\n" +
                    "    products.product_name AS product_name\n" +
                    "FROM manager_order\n" +
                    "JOIN managers ON manager_order.manager_id = managers.manager_id\n" +
                    "JOIN orders ON manager_order.order_id = orders.order_id\n" +
                    "JOIN products ON manager_order.product_id = products.product_id;\n";
        }
        else {
           sql = String.format(SELECT_SQL, sheetName);
        }


        System.out.println(sql);
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println(resultSet);


            return processResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            return "查询数据失败";
        }
    }

    private static String executeUpdate(String sheetName,     JSONObject data, Connection connection) {
        // 构建更新SQL语句

        Iterator<String> key_inter = data.keys();
        StringBuilder wherecolumns = new StringBuilder();
        StringBuilder setvalues = new StringBuilder();

        while (key_inter.hasNext()) {
            String key = key_inter.next();
            Object value = data.get(key);
            if(sheetName.equals("manager_order")){

                if(key.equals("user_name")){
                    value=findId(connection,"managers","name",value);
                    key="manager_id";
                }
                else if(key.equals("product_name")){
                    value=findId(connection,"products",key,value);
                    key="product_id";
                }
                else if(key.equals("order_name")){
                    value=findId(connection,"orders",key,value);
                    key="order_id";

                }
                else if(key.equals("is_completed")){
                    System.out.println("da");
                    System.out.println(value);

                    if(value.equals("true")){
                        value=1;

                    }
                    else {
                        value=0;
                    }
                }
                System.out.println("Key: " + key + ", Value: " + value);


                if(key.equals("id")){
                    wherecolumns.append(key).append("=").append(value);
                }
                else {
                    setvalues.append(key).append("='").append(value).append("', ");
                }


            }
            else {
                System.out.println("Key: " + key + ", Value: " + value);
                if(key.contains("id")){
                    wherecolumns.append(key).append("=").append(value);
                }
                else {
                    setvalues.append(key).append("='").append(value).append("', ");
                }
            }}
        setvalues.setLength(setvalues.length() - 2);


        String sql = String.format(UPDATE_SQL, sheetName, setvalues.toString(), wherecolumns.toString());
        System.out.println(sql);
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();
            if(sheetName.equals("manager_order")){
                updateOrdersTotalPrice(connection);
                updateprductQurity(connection);
            }
            return "更新成功";
        } catch (SQLException e) {
            e.printStackTrace();
            return "更新数据失败";
        }

    }

    private static String processResultSet(ResultSet resultSet) throws SQLException {
        JSONArray jsonArray = new JSONArray();

        while (resultSet.next()) {
            JSONObject rowObject = new JSONObject();

            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                String columnName = resultSet.getMetaData().getColumnName(i);
                String columnValue = resultSet.getString(i);
                rowObject.put(columnName, columnValue);
            }

            jsonArray.put(rowObject);
        }

        JSONObject resultOBject=new JSONObject();
        resultOBject.put("result",jsonArray);

        return resultOBject.toString();
    }
}}