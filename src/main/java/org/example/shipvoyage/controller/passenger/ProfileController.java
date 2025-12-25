package org.example.shipvoyage.controller.passenger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.shipvoyage.model.User;

import java.io.File;

public class ProfileController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Button editButton;

    @FXML
    private Button saveButton;

    @FXML
    public void initialize() {
        User user = Session.loggedInUser;
        usernameField.setText(user.getUsername());
        emailField.setText(user.getEmail());
        usernameField.setEditable(false);
        emailField.setEditable(false);
        saveButton.setDisable(true);

        if (user.getProfileImage() != null) {
            profileImageView.setImage(user.getProfileImage());
        }
    }

    @FXML
    private void onEditProfile() {
        emailField.setEditable(true);
        saveButton.setDisable(false);
    }

    @FXML
    private void onSaveProfile() {
        User user = Session.loggedInUser;
        user.setEmail(emailField.getText());
        emailField.setEditable(false);
        saveButton.setDisable(true);
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
            Image img = new Image(selectedFile.toURI().toString());
            profileImageView.setImage(img);
            Session.loggedInUser.setProfileImage(img);
        }
    }
}
