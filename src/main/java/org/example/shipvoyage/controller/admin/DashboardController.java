package org.example.shipvoyage.controller.admin;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

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
    private Label lblUpcomingTours;

    @FXML
    private Label lblCurrentTours;

    @FXML
    private Label lblTotalBookings;

    @FXML
    private Label lblTotalCustomers;

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
        java.time.LocalDate today = java.time.LocalDate.now();
        int upcoming = org.example.shipvoyage.dao.TourInstanceDAO.getAllTourInstances().stream()
            .map(org.example.shipvoyage.model.TourInstance::getStartDate)
            .filter(d -> d.isAfter(today))
            .toList().size();
        int current = org.example.shipvoyage.dao.TourInstanceDAO.getAllTourInstances().stream()
            .filter(ti -> !ti.getStartDate().isAfter(today) && !ti.getEndDate().isBefore(today))
            .toList().size();
        if (lblUpcomingTours != null) lblUpcomingTours.setText(String.valueOf(upcoming));
        if (lblCurrentTours != null) lblCurrentTours.setText(String.valueOf(current));

        if (lblTotalBookings != null) lblTotalBookings.setText(String.valueOf(org.example.shipvoyage.dao.DashboardDAO.getCount("bookings")));
        if (lblTotalCustomers != null) lblTotalCustomers.setText(String.valueOf(org.example.shipvoyage.dao.DashboardDAO.getPassengerCount()));
    }

        @FXML
        void onManageCustomersClick(ActionEvent event) throws IOException {
        loadCenter("/org/example/shipvoyage/admin/customers.fxml");
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

    @FXML
    void onManageFeaturedClick(ActionEvent event) throws IOException {
        loadCenter("/org/example/shipvoyage/admin/manage-featured-photos.fxml");
    }

    @FXML
    void onAdminProfileClick(ActionEvent event) throws IOException {
        loadCenter("/org/example/shipvoyage/admin/admin-profile.fxml");
    }

    private void loadCenter(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        dashRootPane.setCenter(loader.load());
    }

    @FXML
    private void onLogoutClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/user-type.fxml"));
        javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
        javafx.stage.Stage stage = (javafx.stage.Stage) dashRootPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
