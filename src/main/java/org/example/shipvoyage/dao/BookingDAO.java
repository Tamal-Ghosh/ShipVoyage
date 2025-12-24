package org.example.shipvoyage.dao;

import org.example.shipvoyage.model.Booking;
import org.example.shipvoyage.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public static void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS bookings (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tour_instance_id INTEGER NOT NULL,
                room_id INTEGER NOT NULL,
                room_number TEXT NOT NULL,
                passenger_id INTEGER NOT NULL,
                booked_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (tour_instance_id) REFERENCES tour_instances(id) ON DELETE CASCADE,
                FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
                FOREIGN KEY (passenger_id) REFERENCES passengers(id) ON DELETE CASCADE,
                UNIQUE(tour_instance_id, room_id)
            );
        """;
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addBooking(Booking booking) {
        String sql = "INSERT INTO bookings (tour_instance_id, room_id, room_number, passenger_id) VALUES (?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, booking.getTourInstanceId());
            stmt.setInt(2, booking.getRoomId());
            stmt.setString(3, booking.getRoomNumber());
            stmt.setInt(4, booking.getPassengerId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Integer> getBookedRoomIds(int tourInstanceId) {
        List<Integer> booked = new ArrayList<>();
        String sql = "SELECT room_id FROM bookings WHERE tour_instance_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, tourInstanceId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) booked.add(rs.getInt("room_id"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return booked;
    }

    public static List<Booking> getBookingsByPassenger(int passengerId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE passenger_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, passengerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(new Booking(
                        rs.getInt("id"),
                        rs.getInt("tour_instance_id"),
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getInt("passenger_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public static boolean cancelBooking(int bookingId) {
        String sql = "DELETE FROM bookings WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Booking> getBookingsByShipAndTour(int shipId, int tourInstanceId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
        SELECT b.id, b.tour_instance_id, b.room_id, b.room_number, b.passenger_id
        FROM bookings b
        JOIN tour_instances t ON b.tour_instance_id = t.id
        WHERE t.ship_id = ? AND t.id = ?
    """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, shipId);
            stmt.setInt(2, tourInstanceId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(new Booking(
                        rs.getInt("id"),
                        rs.getInt("tour_instance_id"),
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getInt("passenger_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }
}
