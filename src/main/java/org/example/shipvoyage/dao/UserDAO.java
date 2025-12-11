package org.example.shipvoyage.dao;

import org.example.shipvoyage.model.User;
import org.example.shipvoyage.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public static void createUserTable() {
       String sql="CREATE TABLE IF NOT EXISTS users (\n"
               + " userID INTEGER PRIMARY KEY AUTOINCREMENT,\n"
               + " username TEXT NOT NULL,\n"
               + " password TEXT NOT NULL,\n"
               + " email TEXT NOT NULL\n"
               + ");";
       try(Connection con = DBConnection.getConnection();
           PreparedStatement statement=con.prepareStatement(sql);) {
              statement.executeUpdate();

       }catch (SQLException e)
         {
              e.printStackTrace();
         }
    }

    public static boolean insertUser(String username, String password, String email) {
        String sql = "INSERT INTO users(username, password, email) VALUES(?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, email);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
