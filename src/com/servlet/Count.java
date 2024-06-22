package com.servlet;


import com.enty.User;
import com.uitls.SqlUtil;
import com.uitls.sql_tools;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.http.Cookie;
import java.sql.ResultSet;

import org.json.JSONObject;

@WebServlet("/count")
public class Count extends HttpServlet{


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       int managersCount=getManagersCount();
        int productsCount=getProductsCount();
        int ordersCount=getOrdersCount();
        int completedOrdersCount=getCompletedOrdersCount();
        int incompleteOrdersCount=getIncompleteOrdersCount();
        int managerOrderCount=getManagerOrderCount();
        int completedManagerOrderCount=getCompletedManagerOrderCount();
        int incompleteManagerOrderCount=getIncompleteManagerOrderCount();

        JSONObject countsJson = new JSONObject();
        countsJson.accumulate("managersCount", managersCount);
        countsJson.accumulate("productsCount", productsCount);
        countsJson.accumulate("ordersCount", ordersCount);
        countsJson.accumulate("completedOrdersCount", completedOrdersCount);
        countsJson.accumulate("incompleteOrdersCount", incompleteOrdersCount);
        countsJson.accumulate("managerOrderCount", managerOrderCount);
        countsJson.accumulate("completedManagerOrderCount", completedManagerOrderCount);
        countsJson.accumulate("incompleteManagerOrderCount", incompleteManagerOrderCount);
  

        String counts= new Gson().toJson(countsJson);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();
        out.print(counts);
        out.flush();
    }

    public static int getManagersCount() {
        return getCount("SELECT COUNT(*) FROM managers");
    }

    public static int getProductsCount() {
        return getCount("SELECT COUNT(*) FROM products");
    }

    public static int getOrdersCount() {
        return getCount("SELECT COUNT(*) FROM orders");
    }

    public static int getCompletedOrdersCount() {
        return getCount("SELECT COUNT(*) FROM orders WHERE is_completed = 1");
    }

    public static int getIncompleteOrdersCount() {
        return getCount("SELECT COUNT(*) FROM orders WHERE is_completed = 0");
    }

    public static int getManagerOrderCount() {
        return getCount("SELECT COUNT(*) FROM manager_order");
    }

    public static int getCompletedManagerOrderCount() {
        return getCount("SELECT COUNT(*) FROM manager_order WHERE is_completed = 1");
    }

    public static int getIncompleteManagerOrderCount() {
        return getCount("SELECT COUNT(*) FROM manager_order WHERE is_completed = 0");
    }
    static int getCount(String query) {
        int count = 0;
        try {
             Connection connnetion= SqlUtil.getConnection();
            PreparedStatement statement = connnetion.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            resultSet.close();
            statement.close();
            connnetion.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }



}
