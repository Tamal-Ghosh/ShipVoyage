package org.example.shipvoyage.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.example.shipvoyage.model.User;
import org.example.shipvoyage.util.DBConnection;


public class UserDAO {

    public static java.util.List<org.example.shipvoyage.model.User> getAllPassengers() {
        java.util.List<org.example.shipvoyage.model.User> users = new java.util.ArrayList<>();
        String sql = "SELECT userID, username, password, email, role, phone_number FROM users WHERE LOWER(role)='passenger'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                org.example.shipvoyage.model.User u = new org.example.shipvoyage.model.User(
                        rs.getInt("userID"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("role")
                );
                try { u.setPhoneNumber(rs.getString("phone_number")); } catch (Exception ignored) {}
                users.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static void createUserTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "userID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "email TEXT NOT NULL," +
                "role TEXT NOT NULL," +
                "phone_number TEXT," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "profile_image_path TEXT" +
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





    public static void ensureUserSchema() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement("PRAGMA table_info(users)");) {
            var rs = stmt.executeQuery();
            java.util.HashSet<String> cols = new java.util.HashSet<>();
            while (rs.next()) cols.add(rs.getString("name"));
            rs.close();
            if (!cols.contains("created_at")) {
                try (PreparedStatement add = con.prepareStatement("ALTER TABLE users ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP")) { add.executeUpdate(); }
            }
            if (!cols.contains("profile_image_path")) {
                try (PreparedStatement add = con.prepareStatement("ALTER TABLE users ADD COLUMN profile_image_path TEXT")) { add.executeUpdate(); }
            }
            if (!cols.contains("phone_number")) {
                try (PreparedStatement add = con.prepareStatement("ALTER TABLE users ADD COLUMN phone_number TEXT")) { add.executeUpdate(); }
            }
            try (PreparedStatement upd = con.prepareStatement("UPDATE users SET created_at = COALESCE(created_at, CURRENT_TIMESTAMP)")) {
                upd.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean insertUser(String username, String password, String email, String role, String phone) {
        String sql = "INSERT INTO users(username, password, email, role, phone_number) VALUES(?,?,?,?,?)";
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, email);
            statement.setString(4, role);
            statement.setString(5, phone);
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
                User u = new User(userID, username, password, email, role);
                try {
                    PreparedStatement stmt2 = con.prepareStatement("SELECT created_at, profile_image_path, phone_number FROM users WHERE userID=?");
                    stmt2.setInt(1, userID);
                    var rs2 = stmt2.executeQuery();
                    if (rs2.next()) {
                        u.setCreatedAt(rs2.getString("created_at"));
                        u.setProfileImagePath(rs2.getString("profile_image_path"));
                        u.setPhoneNumber(rs2.getString("phone_number"));
                    }
                    rs2.close();
                    stmt2.close();
                } catch (SQLException ignored) {}
                return u;
            }
            else
                return null;

        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public static boolean updateUserEmail(int userId, String email) {
        String sql = "UPDATE users SET email=? WHERE userID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateProfileImagePath(int userId, String path) {
        String sql = "UPDATE users SET profile_image_path=? WHERE userID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, path);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updatePhoneNumber(int userId, String phone) {
        String sql = "UPDATE users SET phone_number=? WHERE userID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, phone);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updatePassword(int userId, String password) {
        String sql = "UPDATE users SET password=? WHERE userID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, password);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}