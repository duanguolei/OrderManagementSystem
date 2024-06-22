package com.servlet;


import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import com.enty.User;
import com.uitls.sql_tools;


@WebServlet("/register")
public class User_register_api extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        String username=req.getParameter("username");
        String email=req.getParameter("email");
        String password=req.getParameter("password");
        System.out.println(username);
        sql_tools user_register=new sql_tools();
        if(username!=null && password!=null) {
            try {
//            user_register.creat_user_table();
                boolean result = user_register.user_in_table(username);
                System.out.println(result);
                if (result) {
                    User user = new User();
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPassword(password);
                    result = user_register.register_user(user);
                    if (result) {
                        resp.getWriter().write("注册成功");

                    } else {
                        resp.getWriter().write("注册失败,请重新注册");
//
                    }
                } else {
                    resp.getWriter().write("用户名存在，请重新输入");
//
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else {
            resp.getWriter().write("用户名或密码不能为空，请重新输入");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.getRequestDispatcher("/WEB-INF/register.html").forward(req,resp);
    }
}
