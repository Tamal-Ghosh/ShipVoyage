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
                payment_method TEXT,
                payment_status TEXT,
                totalPrice REAL DEFAULT 0,
                FOREIGN KEY (tour_instance_id) REFERENCES tour_instances(id) ON DELETE CASCADE,
                FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
                FOREIGN KEY (passenger_id) REFERENCES passengers(id) ON DELETE CASCADE,
                UNIQUE(tour_instance_id, room_id, passenger_id)
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
        String sql = "INSERT INTO bookings (tour_instance_id, room_id, room_number, passenger_id, payment_method, payment_status, totalPrice) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            for (int i = 0; i < booking.getRoomIds().size(); i++) {
                stmt.setInt(1, booking.getTourInstanceId());
                stmt.setInt(2, booking.getRoomIds().get(i));
                stmt.setString(3, booking.getRoomNumbers().get(i));
                stmt.setInt(4, booking.getPassengerId());
                stmt.setString(5, booking.getPaymentMethod());
                stmt.setString(6, booking.getPaymentStatus());
                stmt.setDouble(7, booking.getTotalPrice());
                stmt.addBatch();
            }
            int[] result = stmt.executeBatch();
            for (int r : result) if (r <= 0) return false;
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updatePaymentStatus(Booking booking) {
        String sql = """
        UPDATE bookings
        SET payment_method=?, payment_status=?, totalPrice=?
        WHERE tour_instance_id=? AND passenger_id=?
    """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, booking.getPaymentMethod());
            stmt.setString(2, booking.getPaymentStatus());
            stmt.setDouble(3, booking.getTotalPrice());
            stmt.setInt(4, booking.getTourInstanceId());
            stmt.setInt(5, booking.getPassengerId());

            int rows = stmt.executeUpdate();
            System.out.println("Payment update rows affected: " + rows);
            return rows > 0;
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
                Booking existingBooking = bookings.stream()
                        .filter(b -> {
                            try {
                                return b.getTourInstanceId() == rs.getInt("tour_instance_id");
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .findFirst()
                        .orElse(null);
                if (existingBooking != null) {
                    existingBooking.getRoomIds().add(rs.getInt("room_id"));
                    existingBooking.getRoomNumbers().add(rs.getString("room_number"));
                } else {
                    List<Integer> roomIds = new ArrayList<>();
                    List<String> roomNumbers = new ArrayList<>();
                    roomIds.add(rs.getInt("room_id"));
                    roomNumbers.add(rs.getString("room_number"));
                    bookings.add(new Booking(
                            rs.getInt("id"),
                            rs.getInt("tour_instance_id"),
                            passengerId,
                            roomIds,
                            roomNumbers,
                            rs.getDouble("totalPrice"),
                            "Booked",
                            rs.getString("payment_method"),
                            rs.getString("payment_status")
                    ));
                }
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
            SELECT b.id, b.tour_instance_id, b.room_id, b.room_number, b.passenger_id, b.payment_method, b.payment_status, b.totalPrice
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
                Booking existingBooking = bookings.stream()
                        .filter(b -> {
                            try {
                                return b.getPassengerId() == rs.getInt("passenger_id");
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .findFirst()
                        .orElse(null);
                if (existingBooking != null) {
                    existingBooking.getRoomIds().add(rs.getInt("room_id"));
                    existingBooking.getRoomNumbers().add(rs.getString("room_number"));
                } else {
                    List<Integer> roomIds = new ArrayList<>();
                    List<String> roomNumbers = new ArrayList<>();
                    roomIds.add(rs.getInt("room_id"));
                    roomNumbers.add(rs.getString("room_number"));
                    bookings.add(new Booking(
                            rs.getInt("id"),
                            rs.getInt("tour_instance_id"),
                            rs.getInt("passenger_id"),
                            roomIds,
                            roomNumbers,
                            rs.getDouble("totalPrice"),
                            "Booked",
                            rs.getString("payment_method"),
                            rs.getString("payment_status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public static boolean cancelBookingByInstanceAndPassenger(int tourInstanceId, int passengerId) {
        String sql = "DELETE FROM bookings WHERE tour_instance_id=? AND passenger_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, tourInstanceId);
            stmt.setInt(2, passengerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static List<Booking> getBookingsByTourInstance(int tourInstanceId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE tour_instance_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, tourInstanceId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Booking existing = bookings.stream()
                        .filter(b -> {
                            try {
                                return b.getPassengerId() == rs.getInt("passenger_id");
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .findFirst()
                        .orElse(null);

                if (existing != null) {
                    existing.getRoomIds().add(rs.getInt("room_id"));
                    existing.getRoomNumbers().add(rs.getString("room_number"));
                } else {
                    List<Integer> roomIds = new ArrayList<>();
                    List<String> roomNumbers = new ArrayList<>();
                    roomIds.add(rs.getInt("room_id"));
                    roomNumbers.add(rs.getString("room_number"));

                    bookings.add(new Booking(
                            rs.getInt("id"),
                            rs.getInt("tour_instance_id"),
                            rs.getInt("passenger_id"),
                            roomIds,
                            roomNumbers,
                            rs.getDouble("totalPrice"),
                            "Booked",
                            rs.getString("payment_method"),
                            rs.getString("payment_status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }



}
