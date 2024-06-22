package com.servlet;


import com.enty.User;
import com.uitls.sql_tools;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.http.Cookie;


@WebServlet("/admin")
public class admin extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();
        String username=null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName())) {
                    username = cookie.getValue();
                    break;
                }
            }
        }

        if (username!=null) {

            req.getRequestDispatcher("/WEB-INF/admin.html").forward(req,resp);
    }
        else {
            req.getRequestDispatcher("/WEB-INF/login.html").forward(req,resp);
    }
    }}

