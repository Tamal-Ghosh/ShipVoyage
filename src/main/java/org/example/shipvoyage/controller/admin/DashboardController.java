package org.example.shipvoyage.controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Label lblTotalRooms;

    @FXML
    private Label lblTotalShips;

    @FXML
    private Label lblTotalTours;

    @FXML
    private Label lblTourInstances;

    @FXML
    void onManaeShipsClick(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/admin/manage-ships.fxml"));
        Scene scene =new Scene(loader.load());
        Stage stage = (Stage) lblTourInstances.getScene().getWindow();
        stage.setScene(scene);
        stage.show();

    }

}
