package org.example.shipvoyage.controller;

import java.io.IOException;

import org.example.shipvoyage.dao.UserDAO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignupController {
    String role;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleBox;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField phoneField;


    @FXML
    void handleSignup(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String phone = phoneField.getText();



        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty() || phone.isEmpty()) {
            System.out.println("Missing Information");
            return;
        }

        boolean added= UserDAO.insertUser(username, password, email, role, phone);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if(added){
            alert.setContentText("Signup Successful");
            alert.showAndWait();
            
            FXMLLoader loader = new  FXMLLoader(getClass().getResource("/org/example/shipvoyage/login.fxml"));
            Scene loginScene = new Scene(loader.load());
            LoginController loginController = loader.getController();
            loginController.setRole(this.role);
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        }else{
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Signup Failed");
            alert.setHeaderText("Username already exists");
            alert.setContentText("This username is already taken. Please choose a different username.");
            alert.showAndWait();
        }

    }

    @FXML
    void goToLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new  FXMLLoader(getClass().getResource("/org/example/shipvoyage/login.fxml"));
        Scene loginScene = new Scene(loader.load());
        LoginController loginController = loader.getController();
        loginController.setRole(this.role);
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }

    public void setRole(String role) {
        this.role = role;
    }


}
