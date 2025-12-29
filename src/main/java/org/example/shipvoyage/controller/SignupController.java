package org.example.shipvoyage.controller;

import java.io.IOException;

import org.example.shipvoyage.dao.UserDAO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
    private TextField passwordTextField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField confirmPasswordTextField;

    @FXML
    private Button togglePasswordBtn;

    @FXML
    private Button toggleConfirmPasswordBtn;

    @FXML
    private ComboBox<String> roleBox;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField phoneField;

    private boolean passwordVisible = false;
    private boolean confirmPasswordVisible = false;

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

    @FXML
    void toggleConfirmPasswordVisibility(ActionEvent event) {
        confirmPasswordVisible = !confirmPasswordVisible;
        if (confirmPasswordVisible) {
            confirmPasswordTextField.setText(confirmPasswordField.getText());
            confirmPasswordTextField.setVisible(true);
            confirmPasswordTextField.setManaged(true);
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
            toggleConfirmPasswordBtn.setText("🙈");
        } else {
            confirmPasswordField.setText(confirmPasswordTextField.getText());
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
            confirmPasswordTextField.setVisible(false);
            confirmPasswordTextField.setManaged(false);
            toggleConfirmPasswordBtn.setText("👁");
        }
    }

    @FXML
    void handleSignup(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordVisible ? passwordTextField.getText() : passwordField.getText();
        String confirmPassword = confirmPasswordVisible ? confirmPasswordTextField.getText() : confirmPasswordField.getText();
        String phone = phoneField.getText();



        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty() || phone.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return;
        }

        if (password.length() < 6) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Password");
            alert.setHeaderText(null);
            alert.setContentText("Password must be at least 6 characters long.");
            alert.showAndWait();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Password Mismatch");
            alert.setHeaderText(null);
            alert.setContentText("Passwords do not match. Please try again.");
            alert.showAndWait();
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
