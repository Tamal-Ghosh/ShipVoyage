package org.example.shipvoyage.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.shipvoyage.dao.UserDAO;

import java.io.IOException;

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
    void handleSignup(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();



        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {
            System.out.println("Missing Information");
            return;
        }

//        System.out.println("Username: " + username);
//        System.out.println("Email: " + email);
//        System.out.println("Password: " + password);
        boolean added= UserDAO.insertUser(username, password, email, role);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if(added){
            alert.setContentText("Signup Successful");
            alert.showAndWait();
            //System.out.println("User Added Successfully");
        }else{
            alert.setContentText("Signup Failed");
            alert.showAndWait();
            	//System.out.println("Error Adding User");
                return;
        }

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

    public void setRole(String role) {
        this.role = role;
    }


}
