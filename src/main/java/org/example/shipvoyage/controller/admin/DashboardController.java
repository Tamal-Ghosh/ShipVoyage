package org.example.shipvoyage.controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class DashboardController {

    @FXML
    private BorderPane dashRootPane;

    @FXML
    private Label lblTotalShips;

    @FXML
    private Label lblTotalTours;

    @FXML
    private Label lblTourInstances;

    @FXML
    private Label lblTotalRooms;

    private Node dashboardCenter;

    @FXML
    public void initialize() {
        dashboardCenter = dashRootPane.getCenter();
        loadCounts();

    }

    private void loadCounts() {
        lblTotalShips.setText(String.valueOf(org.example.shipvoyage.dao.DashboardDAO.getCount("ships")));
        lblTotalTours.setText(String.valueOf(org.example.shipvoyage.dao.DashboardDAO.getCount("tours")));
        lblTourInstances.setText(String.valueOf(org.example.shipvoyage.dao.DashboardDAO.getCount("tour_instances")));
        lblTotalRooms.setText(String.valueOf(org.example.shipvoyage.dao.DashboardDAO.getCount("rooms")));
    }

    @FXML
    void onDashboardClick(ActionEvent event) {
        loadCounts();
        dashRootPane.setCenter(dashboardCenter);
    }

    @FXML
    void onManageShipsClick(ActionEvent event) throws IOException {
        loadCenter("/org/example/shipvoyage/admin/manage-ships.fxml");
    }

    @FXML
    void onManageToursClick(ActionEvent event) throws IOException {
        loadCenter("/org/example/shipvoyage/admin/manage-tours.fxml");
    }

    @FXML
    void onManageTourInstanceClick(ActionEvent event) throws IOException {
        loadCenter("/org/example/shipvoyage/admin/manage-tour-instances.fxml");
    }

    @FXML
    void onManageRoomsClick(ActionEvent event) throws IOException {
        loadCenter("/org/example/shipvoyage/admin/manage-rooms.fxml");
    }

    @FXML
    void onViewBookingsClick(ActionEvent event) throws IOException {
        loadCenter("/org/example/shipvoyage/admin/view-bookings.fxml");
    }

    private void loadCenter(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        dashRootPane.setCenter(loader.load());
    }
}
