package org.example.shipvoyage.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.shipvoyage.dao.UserDAO;
import org.example.shipvoyage.model.User;

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
        User user= new UserDAO().searchLoginUser(username, password);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if(user==null){
            System.out.println("Invalid username or password");
            alert.setContentText("Invalid username or password");
            alert.showAndWait();
            return;
        }
        alert.setContentText("Successfully Logged In");
        alert.showAndWait();
        FXMLLoader loader = new  FXMLLoader(getClass().getResource("/org/example/shipvoyage/hello-view.fxml"));
        Scene loginScene = new Scene(loader.load());
        Stage stage = (Stage) passwordField.getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();



    }

}
