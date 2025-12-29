package org.example.shipvoyage.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.example.shipvoyage.model.FeaturedPhoto;
import org.example.shipvoyage.util.DBConnection;

public class PhotoDAO {
    public static List<FeaturedPhoto> getFeaturedPhotos(int limit) {
        List<FeaturedPhoto> photos = new ArrayList<>();
        String sql = "SELECT id, title, description, image_path FROM featured_photos ORDER BY id DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    photos.add(new FeaturedPhoto(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("image_path")
                    ));
                }
            }
        } catch (Exception e) {
            // fallback below
        }
        if (photos.isEmpty()) {
            // Fallback placeholders if table empty/not present
            photos.add(new FeaturedPhoto(1, "Sundarban Explorer", "Dense mangrove views & wildlife", ""));
            photos.add(new FeaturedPhoto(2, "Bay of Bengal Cruise", "Open-water sunsets aboard MV The Wave", ""));
            photos.add(new FeaturedPhoto(3, "Heritage Riverline", "Calm river cruise with cultural stops", ""));
            photos.add(new FeaturedPhoto(4, "Saint Martin Getaway", "Crystal waters and coral beaches", ""));
            photos.add(new FeaturedPhoto(5, "Cox's Bazar Sail", "World’s longest sea beach vistas", ""));
            photos.add(new FeaturedPhoto(6, "Kuakata Sunrise", "Panoramic dawn over the bay", ""));
        }
        return photos;
    }
}
