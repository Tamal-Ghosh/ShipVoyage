package org.example.shipvoyage.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.shipvoyage.dao.UserDAO;
import org.example.shipvoyage.util.DBConnection;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        UserDAO.createUserTable();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/org/example/shipvoyage/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
