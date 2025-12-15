package org.example.shipvoyage.dao;

import org.example.shipvoyage.model.Booking;
import org.example.shipvoyage.model.Ship;
import org.example.shipvoyage.model.TourInstance;
import org.example.shipvoyage.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public static void createBookingTable() {
        String sql = "CREATE TABLE IF NOT EXISTS bookings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "customerName TEXT NOT NULL," +
                "email TEXT NOT NULL," +
                "shipId INTEGER NOT NULL," +
                "tourInstanceId INTEGER NOT NULL," +
                "roomNumber TEXT," +
                "totalPayment REAL," +
                "duePayment REAL," +
                "status TEXT," +
                "FOREIGN KEY(shipId) REFERENCES ships(id)," +
                "FOREIGN KEY(tourInstanceId) REFERENCES tour_instances(id)" +
                ");";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Booking> getBookingsByShipAndTour(int shipId, int tourInstanceId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE shipId=? AND tourInstanceId=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, shipId);
            stmt.setInt(2, tourInstanceId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Booking b = new Booking();
                b.setId(rs.getInt("id"));
                b.setCustomerName(rs.getString("customerName"));
                b.setEmail(rs.getString("email"));
                b.setRoomNumber(rs.getString("roomNumber"));
                b.setTotalPayment(rs.getDouble("totalPayment"));
                b.setDuePayment(rs.getDouble("duePayment"));
                b.setStatus(rs.getString("status"));

                Ship ship = ShipDAO.getShipById(rs.getInt("shipId"));
                b.setShip(ship);

                TourInstance tourInstance = TourInstanceDAO.getTourInstanceById(rs.getInt("tourInstanceId"));
                b.setTourInstance(tourInstance);

                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
