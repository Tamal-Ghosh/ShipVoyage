package org.example.shipvoyage.controller;

import java.io.IOException;

import org.controlsfx.control.Notifications;
import org.example.shipvoyage.controller.passenger.Session;
import org.example.shipvoyage.dao.UserDAO;
import org.example.shipvoyage.model.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController {

    String role;

    @FXML
    private Button loginBtn;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button togglePasswordBtn;

    @FXML
    private Hyperlink regHyperlink;

    @FXML
    private TextField usernameField;

    private boolean passwordVisible = false;

    @FXML
    void togglePasswordVisibility(ActionEvent event) {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordBtn.setText("🙈");
        } else {
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
            togglePasswordBtn.setText("👁");
        }
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    @FXML
    void onClickRegLink(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/signup.fxml"));
        Scene signupScene = new Scene(loader.load());
        SignupController signupController = loader.getController();
        signupController.role = this.role;
        Stage stage = (Stage) passwordField.getScene().getWindow();
        stage.setScene(signupScene);
        stage.show();
    }

    @FXML
    void onLogin(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordVisible ? passwordTextField.getText() : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Missing username or password");
            return;
        }

        User user = new UserDAO().searchLoginUser(username, password);

        if (user == null) {
            System.out.println("Invalid username or password");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Invalid username or password");
            alert.showAndWait();
            return;
        }



        Stage stage = (Stage) passwordField.getScene().getWindow();

        if ("admin".equalsIgnoreCase(user.getRole()) && "admin".equalsIgnoreCase(this.role)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/admin/dashboard.fxml"));
            Scene adminScene = new Scene(loader.load());
            stage.setScene(adminScene);
        } else if ("passenger".equalsIgnoreCase(user.getRole()) && "passenger".equalsIgnoreCase(this.role)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/passenger/passenger-home.fxml"));
            Scene passengerScene = new Scene(loader.load());
            stage.setScene(passengerScene);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Role mismatch. Please login with the correct user type.");
            alert.showAndWait();
            return;
        }
        Session.loggedInUser = user;

        stage.show();

        Notifications.create()
            .title(null)
            .text("Login successful")
            .position(Pos.BOTTOM_CENTER)
            .owner(stage)
            .hideAfter(Duration.seconds(2.2))
            .graphic(null)
            .darkStyle()
            .showConfirm();
    }
}
