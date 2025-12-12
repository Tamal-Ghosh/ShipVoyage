package org.example.shipvoyage.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class UserTypeController {

    @FXML
    private Button adminLogin;

    @FXML
    private Button passengerLogin;

    @FXML
    void onAdminClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/login.fxml"));
        Scene scene =new Scene(loader.load());
        LoginController loginController = loader.getController();
        loginController.setRole("admin");
        Stage stage = (Stage) adminLogin.getScene().getWindow();
        stage.setScene(scene);
    }

    @FXML
    void onPassengerClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/login.fxml"));
        Scene scene =new Scene(loader.load());
        LoginController loginController = loader.getController();
        loginController.setRole("passenger");
        Stage stage = (Stage) adminLogin.getScene().getWindow();
        stage.setScene(scene);
    }

}
