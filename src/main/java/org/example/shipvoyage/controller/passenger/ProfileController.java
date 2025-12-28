package org.example.shipvoyage.controller.passenger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.example.shipvoyage.dao.BookingDAO;
import org.example.shipvoyage.dao.UserDAO;
import org.example.shipvoyage.model.User;
import static org.example.shipvoyage.util.AlertUtil.showError;
import static org.example.shipvoyage.util.AlertUtil.showInfo;
import static org.example.shipvoyage.util.AlertUtil.showWarning;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ProfileController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Label avatarPlaceholder;

    @FXML
    private Button editButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button changePhotoButton;

    @FXML
    private Label memberSinceLabel;

    @FXML
    private Label totalBookingsLabel;

    @FXML
    public void initialize() {
        User user = Session.loggedInUser;
        usernameField.setText(user.getUsername());
        emailField.setText(user.getEmail());
        usernameField.setEditable(false);
        emailField.setEditable(false);
        saveButton.setDisable(true);
        if (changePhotoButton != null) {
            changePhotoButton.setVisible(false);
            changePhotoButton.setManaged(false);
        }

        if (user.getProfileImage() != null) {
            profileImageView.setImage(user.getProfileImage());
            if (avatarPlaceholder != null) avatarPlaceholder.setVisible(false);
        } else if (user.getProfileImagePath() != null && !user.getProfileImagePath().isEmpty()) {
            try {
                profileImageView.setImage(new Image(new java.io.File(user.getProfileImagePath()).toURI().toString()));
                if (avatarPlaceholder != null) avatarPlaceholder.setVisible(false);
            } catch (Exception ignored) {}
        } else {
            if (avatarPlaceholder != null) avatarPlaceholder.setVisible(true);
        }

        if (memberSinceLabel != null) {
            String since = user.getCreatedAt() != null ? user.getCreatedAt() : "";
            memberSinceLabel.setText(since);
        }
        if (totalBookingsLabel != null) {
            int count = BookingDAO.getBookingsByPassenger(user.getUserID()).size();
            totalBookingsLabel.setText(count + " cruises");
        }
    }

    @FXML
    private void onEditProfile() {
        emailField.setEditable(true);
        saveButton.setDisable(false);
        if (changePhotoButton != null) {
            changePhotoButton.setVisible(true);
            changePhotoButton.setManaged(true);
        }
    }

    @FXML
    private void onSaveProfile() {
        User user = Session.loggedInUser;
        String newEmail = emailField.getText() == null ? "" : emailField.getText().trim();
        if (newEmail.isEmpty()) {
            showWarning("Email cannot be empty.");
            return;
        }
        boolean ok = UserDAO.updateUserEmail(user.getUserID(), newEmail);
        if (ok) {
            user.setEmail(newEmail);
            showInfo("Profile updated successfully.");
            emailField.setEditable(false);
            saveButton.setDisable(true);
            if (changePhotoButton != null) {
                changePhotoButton.setVisible(false);
                changePhotoButton.setManaged(false);
            }
        } else {
            showError("Failed to update email.");
        }
    }

    @FXML
    private void onChangeImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            // Copy to app-managed directory for stable access
            try {
                Path imagesDir = Path.of("profile-images");
                Files.createDirectories(imagesDir);
                String ext = getExtension(selectedFile.getName());
                if (ext == null || ext.isBlank()) ext = "png";
                Path dest = imagesDir.resolve("user-" + Session.loggedInUser.getUserID() + "." + ext);
                Files.copy(selectedFile.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

                Image img = new Image(dest.toUri().toString());
                profileImageView.setImage(img);
                Session.loggedInUser.setProfileImage(img);
                // Persist managed file path
                UserDAO.updateProfileImagePath(Session.loggedInUser.getUserID(), dest.toAbsolutePath().toString());
                Session.loggedInUser.setProfileImagePath(dest.toAbsolutePath().toString());
                if (avatarPlaceholder != null) avatarPlaceholder.setVisible(false);
            } catch (IOException ex) {
                showError("Failed to save profile image.");
            }
        }
    }

    private static String getExtension(String name) {
        int i = name.lastIndexOf('.');
        if (i == -1) return null;
        return name.substring(i + 1).toLowerCase();
    }
}
