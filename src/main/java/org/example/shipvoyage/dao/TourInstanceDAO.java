package org.example.shipvoyage.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.example.shipvoyage.model.TourInstance;
import org.example.shipvoyage.util.DBConnection;


public class TourInstanceDAO {

    public static void createTable() {
        String sql = " CREATE TABLE IF NOT EXISTS tour_instances ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " tour_id INTEGER NOT NULL,"
                + " ship_id INTEGER NOT NULL,"
                + " start_date DATE NOT NULL,"
                + " end_date DATE NOT NULL,"
                + " FOREIGN KEY (tour_id) REFERENCES tours(id),"
                + " FOREIGN KEY (ship_id) REFERENCES ships(id)"
                + ");";
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement statement = con.prepareStatement(sql);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static boolean addTourInstance(int tourId, int shipid, LocalDate startDate, LocalDate endDate) {
        String sql = " INSERT INTO tour_instances(tour_id, ship_id, start_date, end_date) VALUES(?,?,?,?)";
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, tourId);
            statement.setInt(2, shipid);
            statement.setString(3, startDate.toString());
            statement.setString(4, endDate.toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static List<TourInstance> getAllTourInstances() {
        List<TourInstance> tourInstances = new ArrayList<>();
        String sql = "SELECT * FROM tour_instances";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement statement = con.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                tourInstances.add(new TourInstance(
                        rs.getInt("id"),
                        rs.getInt("tour_id"),
                        rs.getInt("ship_id"),
                        LocalDate.parse(rs.getString("start_date")),
                        LocalDate.parse(rs.getString("end_date"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tourInstances;
    }
    public static List<TourInstance> getTourInstancesByShip(int shipId) {
        List<TourInstance> tourInstances = new ArrayList<>();
        String sql = "SELECT * FROM tour_instances WHERE ship_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setInt(1, shipId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                tourInstances.add(new TourInstance(
                        rs.getInt("id"),
                        rs.getInt("tour_id"),
                        rs.getInt("ship_id"),
                        LocalDate.parse(rs.getString("start_date")),
                        LocalDate.parse(rs.getString("end_date"))
                ));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tourInstances;
    }
    public static TourInstance getTourInstanceById(int id) {
        String sql = "SELECT * FROM tour_instances WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new TourInstance(
                        rs.getInt("id"),
                        rs.getInt("tour_id"),
                        rs.getInt("ship_id"),
                        LocalDate.parse(rs.getString("start_date")),
                        LocalDate.parse(rs.getString("end_date"))
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static boolean updateTourInstance(TourInstance tourInstance) {
        String sql = "UPDATE tour_instances SET tour_id=?, ship_id=?, start_date=?, end_date=? WHERE id=?";
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, tourInstance.getTourId());
            statement.setInt(2, tourInstance.getShipId());
            statement.setString(3, tourInstance.getStartDate().toString());
            statement.setString(4, tourInstance.getEndDate().toString());
            statement.setInt(5, tourInstance.getId());
            int rowsUpdated = statement.executeUpdate();
            statement.close();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


    }
    public static  boolean deleteTourInstance(TourInstance tourInstance) {
        String sql = "DELETE FROM tour_instances WHERE id=?";
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, tourInstance.getId());
            int rowsDeleted = statement.executeUpdate();
            statement.close();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}