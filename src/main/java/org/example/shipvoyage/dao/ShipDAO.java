package org.example.shipvoyage.dao;

import org.example.shipvoyage.model.Ship;
import org.example.shipvoyage.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShipDAO {

    public static void createShipTable() {
        String sql = "CREATE TABLE IF NOT EXISTS ships (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "shipName TEXT NOT NULL," +
                "capacity INTEGER NOT NULL" +
                ");";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean insertShip(String shipName, int capacity) {
        String sql = "INSERT INTO ships(shipName, capacity) VALUES(?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, shipName);
            stmt.setInt(2, capacity);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateShip(int id, String shipName, int capacity) {
        String sql = "UPDATE ships SET shipName=?, capacity=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, shipName);
            stmt.setInt(2, capacity);
            stmt.setInt(3, id);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteShip(int id) {
        String sql = "DELETE FROM ships WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Ship> getAllShips() {
        List<Ship> ships = new ArrayList<>();
        String sql = "SELECT * FROM ships";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ships.add(new Ship(
                        rs.getInt("id"),
                        rs.getString("shipName"),
                        rs.getInt("capacity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ships;
    }

    public static  Ship getShipById(int id) {
        String sql = "SELECT * FROM ships WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Ship(
                            rs.getInt("id"),
                            rs.getString("shipName"),
                            rs.getInt("capacity")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }
}
