package org.example.shipvoyage.dao;

import org.example.shipvoyage.model.User;
import org.example.shipvoyage.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;


public class UserDAO {

    public static void createUserTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "userID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "email TEXT NOT NULL," +
                "role TEXT NOT NULL" +
                ");";
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement statement = con.prepareStatement(sql);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean insertUser(String username, String password, String email, String role) {
        String sql = "INSERT INTO users(username, password, email,role) VALUES(?,?,?,?)";
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, email);
            statement.setString(4, role);
            int rowsInserted = statement.executeUpdate();
            statement.close();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static User searchLoginUser(String username, String password) {
        String sql="SELECT * FROM users WHERE username =? AND password = ?";
        try(Connection con =DBConnection.getConnection();
        PreparedStatement statement = con.prepareStatement(sql))
        {
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet rs=statement.executeQuery();

            if(rs.next()){
                int userID=rs.getInt("userID");
                String email=rs.getString("email");
                String role=rs.getString("role");
                rs.close();
                return new User(userID, username, password, email, role);
            }
            else
                return null;

        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }


}
