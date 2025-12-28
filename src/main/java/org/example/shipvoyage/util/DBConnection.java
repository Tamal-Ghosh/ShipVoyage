package org.example.shipvoyage.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.example.shipvoyage.dao.BookingDAO;
import org.example.shipvoyage.dao.RoomDAO;
import org.example.shipvoyage.dao.ShipDAO;
import org.example.shipvoyage.dao.TourDAO;
import org.example.shipvoyage.dao.TourInstanceDAO;
import org.example.shipvoyage.dao.UserDAO;

public class DBConnection {

    private static final String URL = "jdbc:sqlite:shipvoyage.db";
    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
    public static void createAllTables() {
        UserDAO.createUserTable();
        UserDAO.ensureUserSchema();
        TourDAO.createTourTable();
        ShipDAO.createShipTable();
        TourInstanceDAO.createTable();
        RoomDAO.createTable();
        BookingDAO.createTable();

    }
}
