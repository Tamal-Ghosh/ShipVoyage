package org.example.shipvoyage.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private Button loginBtn;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Hyperlink regHyperlink;

    @FXML
    private TextField usernameField;

    @FXML
    void onClickRegLink(ActionEvent event) throws IOException {

        FXMLLoader loader = new  FXMLLoader(getClass().getResource("/org/example/shipvoyage/signup.fxml"));
        Scene loginScene = new Scene(loader.load());
        Stage stage = (Stage) passwordField.getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();

    }

    @FXML
    void onLogin(ActionEvent event) throws  IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Missing username or password");
            return;
        }

        FXMLLoader loader = new  FXMLLoader(getClass().getResource("/org/example/shipvoyage/hello-view.fxml"));
        Scene loginScene = new Scene(loader.load());
        Stage stage = (Stage) passwordField.getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();



    }

}
