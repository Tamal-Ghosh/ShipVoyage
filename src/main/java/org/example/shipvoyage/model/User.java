package org.example.shipvoyage.model;

import javafx.scene.image.Image;

public class User {
    private int userID;
    private String username;
    private String email;
    private String password;
    private String role;
    private Image profileImage;

    public User(int userID, String username, String password, String email, String role) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Image getProfileImage() { return profileImage; }
    public void setProfileImage(Image profileImage) { this.profileImage = profileImage; }



}
