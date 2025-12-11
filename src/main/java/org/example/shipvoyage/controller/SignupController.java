package org.example.shipvoyage.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SignupController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleBox;

    @FXML
    private TextField usernameField;


    @FXML
    void handleSignup(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() ) {
            System.out.println("Missing Information");
            return;
        }

        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);

        FXMLLoader loader = new  FXMLLoader(getClass().getResource("/org/example/shipvoyage/login.fxml"));
        Scene loginScene = new Scene(loader.load());
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();

    }

    @FXML
    void goToLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new  FXMLLoader(getClass().getResource("/org/example/shipvoyage/login.fxml"));
        Scene loginScene = new Scene(loader.load());
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }
}
