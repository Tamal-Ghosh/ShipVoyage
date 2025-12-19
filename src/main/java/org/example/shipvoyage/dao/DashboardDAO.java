package org.example.shipvoyage.dao;

import org.example.shipvoyage.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardDAO {
    public static int getCount(String tableName) {
        int count = 0;
        String sql = "SELECT COUNT(*)  FROM " + tableName;

        try {

            Connection connection= DBConnection.getConnection();
            PreparedStatement Statement = connection.prepareStatement(sql);
            ResultSet rs = Statement.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }


        }catch (SQLException e){
            e.printStackTrace();
        }
        return count;
    }
}
