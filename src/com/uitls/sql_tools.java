package com.uitls;

import com.enty.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class sql_tools {

    public Boolean register_user(User user) throws SQLException {
        Connection cnn=SqlUtil.getConnection();
        PreparedStatement stmt=null;
        String sql="INSERT INTO regsterusers(username,email,password) values(?,?,?);";
        int result=0;
        try {
            stmt=cnn.prepareStatement(sql);
            stmt.setString(1,user.getUsername());
            stmt.setString(2,user.getEmail());
            stmt.setString(3,user.getPassword());
            result=stmt.executeUpdate();

        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            SqlUtil.close(stmt,cnn);
        }
        if(result>0){
            return true;
        }
        else{
            return false;
        }

    }


    public Boolean login_user(User user) throws SQLException{
        Connection cnn=SqlUtil.getConnection();
        PreparedStatement stmt=null;
        String sql="select password from regsterusers where username=?";
        boolean result=false;
        try {
            stmt=cnn.prepareStatement(sql);
            stmt.setString(1,user.getUsername());
            ResultSet res=stmt.executeQuery();
            if(res.next()){
                String storedPassword = res.getString("password");
                if (storedPassword.equals(user.getPassword())) {
                    result = true;
                }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            SqlUtil.close(stmt,cnn);
        }

        return result;

    }


    public boolean user_in_table(String username) throws SQLException {
        Connection cnn=SqlUtil.getConnection();
        PreparedStatement stmt=null;
        boolean result=false;
        String sql="select count(*) as usercount from regsterusers where username=?";
        try {
            stmt = cnn.prepareStatement(sql);
            stmt.setString(1, username);

            ResultSet resultSet=stmt.executeQuery();

            while (resultSet.next()) {
                int count = resultSet.getInt("usercount");

                if (count == 0) {
                    result = true;
                }
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {

            SqlUtil.close(stmt,cnn);
        }


        return result;

    }
}
