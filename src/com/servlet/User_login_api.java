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

@WebServlet("/login")
public class User_login_api extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        String username=req.getParameter("username");
        String password=req.getParameter("password");
        System.out.println(username);
        sql_tools user_login=new sql_tools();
        try {

            boolean result=user_login.user_in_table(username);

            if(!result){
                User user=new User();
                user.setUsername(username);
                user.setPassword(password);
                result=user_login.login_user(user);
                if(result){
                    resp.getWriter().write("登录成功");
                    Cookie usernameCookie = new Cookie("username", username);
                    usernameCookie.setMaxAge(60 * 60*60);
                    resp.addCookie(usernameCookie);


                }
                else{
                    resp.getWriter().write("密码错误,请重新尝试");
//
                }
            }
            else {
                resp.getWriter().write("用户名不存在，请重新输入或注册");
//
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName())) {
                    String username = cookie.getValue();

                    break;
                }
            }
        }
        req.getRequestDispatcher("/WEB-INF/login.html").forward(req,resp);
    }
}
