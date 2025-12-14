package org.example.shipvoyage.dao;

import org.example.shipvoyage.model.Tour;
import org.example.shipvoyage.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TourDAO {

    public static void createTourTable() {

        String sql = "CREATE TABLE IF NOT EXISTS tours (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tour_name  TEXT NOT NULL," +
                "route TEXT NOT NULL," +
                "duration INTEGER NOT NULL," +
                "description TEXT" +
                ");";

         try{
             Connection con = DBConnection.getConnection();
             PreparedStatement statement= con.prepareStatement(sql);
             statement.executeUpdate();
             statement.close();
         }catch(SQLException e){
                e.printStackTrace();
         }

    }

    public static void addTour(Tour tour) {
        String sql = "INSERT INTO tours(tour_name, route, duration, description) VALUES(?,?,?,?)";
        try{
            Connection con = DBConnection.getConnection();
            PreparedStatement statement= con.prepareStatement(sql);
            statement.setString(1,tour.getTourName());
            statement.setString(2,tour.getRoute());
            statement.setInt(3,tour.getDuration());
            statement.setString(4,tour.getDescription());
            statement.executeUpdate();
            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static List<Tour> getAllTours() {
       List<Tour> tours = new ArrayList<>();
       String sql= "SELECT * FROM tours";
       try(Connection con = DBConnection.getConnection();
           PreparedStatement statement= con.prepareStatement(sql);
           ResultSet rs=statement.executeQuery();) {
           while (rs.next()){
               tours.add(new Tour(
                       rs.getInt("id"),
                       rs.getString("tour_name"),
                       rs.getString("route"),
                       rs.getInt("duration"),
                       rs.getString("description")
               ));
           }


       }catch (SQLException e)
       {
           e.printStackTrace();
       }
       return tours;
    }

    public static boolean updateTour(Tour tour) {
        String sql = "UPDATE tours SET tour_name=?, route=?, duration=?, description=? WHERE id=?";
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, tour.getTourName());
            statement.setString(2, tour.getRoute());
            statement.setInt(3, tour.getDuration());
            statement.setString(4, tour.getDescription());
            statement.setInt(5, tour.getId());
            int rowsUpdated = statement.executeUpdate();
            statement.close();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

        public static boolean deleteTour(int id) {
            String sql = "DELETE FROM tours WHERE id=?";
            try {
                Connection con = DBConnection.getConnection();
                PreparedStatement statement = con.prepareStatement(sql);
                statement.setInt(1, id);
                int rowsDeleted = statement.executeUpdate();
                statement.close();
                return rowsDeleted > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        public static Tour getTourById(int id) {
            String sql = "SELECT * FROM tours WHERE id=?";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement statement = con.prepareStatement(sql)) {
                statement.setInt(1, id);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    return new Tour(
                            rs.getInt("id"),
                            rs.getString("tour_name"),
                            rs.getString("route"),
                            rs.getInt("duration"),
                            rs.getString("description")
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

}
