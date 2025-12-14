package org.example.shipvoyage.controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

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
    private BorderPane dashRootPane;

    @FXML
    void onManageShipsClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/admin/manage-ships.fxml"));
        dashRootPane.setCenter(loader.load());
    }

    @FXML
    void onManageToursClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/admin/manage-tours.fxml"));
        dashRootPane.setCenter(loader.load());
    }
}
