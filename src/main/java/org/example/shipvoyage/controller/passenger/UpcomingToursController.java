package org.example.shipvoyage.controller.passenger;

import java.time.LocalDate;
import java.util.List;

import javafx.application.Platform;
import org.example.shipvoyage.dao.ShipDAO;
import org.example.shipvoyage.dao.TourDAO;
import org.example.shipvoyage.dao.TourInstanceDAO;
import org.example.shipvoyage.model.TourInstance;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.shipvoyage.util.ThreadPool;

public class UpcomingToursController {

    @FXML
    private VBox upcomingListContainer;

    @FXML
    public void initialize() {
        loadUpcomingTours();
    }

    private void loadUpcomingTours() {
        if (upcomingListContainer == null) return;
        ThreadPool.getExecutor().execute(() -> {
            LocalDate today = LocalDate.now();

            List<TourInstance> instances = TourInstanceDAO.getAllTourInstances().stream()
                .filter(t -> !t.getStartDate().isBefore(today))
                .sorted(java.util.Comparator.comparing(TourInstance::getStartDate))
                .toList();

            Platform.runLater(() -> {
                upcomingListContainer.getChildren().clear();

                if (instances.isEmpty()) {
                    Label none = new Label("No upcoming trips available.");
                    none.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 13px;");
                    upcomingListContainer.getChildren().add(none);
                    return;
                }

                for (TourInstance inst : instances) {
                    var tour = TourDAO.getTourById(inst.getTourId());
                    var ship = ShipDAO.getShipById(inst.getShipId());
                    if (tour == null || ship == null) continue;

                    VBox card = new VBox();
                    card.setSpacing(10);
                    card.setStyle("-fx-border-color: #E5E7EB; -fx-border-width: 1; -fx-border-radius: 16; -fx-background-radius: 16; -fx-padding: 16; -fx-background-color: white; -fx-spacing: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);");

                    HBox header = new HBox(8);
                    header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    Label title = new Label(tour.getTourName());
                    title.getStyleClass().add("trip-title");
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                    Label status = new Label("Upcoming");
                    status.getStyleClass().addAll("status-badge", "status-pending");
                    header.getChildren().addAll(title, spacer, status);

                    Label route = new Label(tour.getFrom() + " → " + tour.getTo());
                    route.getStyleClass().add("trip-subtitle");

                    Label departLabel = new Label("Depart: " + inst.getStartDate());
                    departLabel.getStyleClass().add("trip-date");

                    Label returnLabel = new Label("Return: " + inst.getEndDate());
                    returnLabel.getStyleClass().add("trip-date");

                    Label shipLabel = new Label("Ship: " + ship.getShipName());
                    shipLabel.getStyleClass().add("trip-date");

                    javafx.scene.control.Button bookButton = new javafx.scene.control.Button("Book Now");
                    bookButton.setStyle("-fx-padding: 8 20; -fx-font-size: 12; -fx-background-color: #FF6B35; -fx-text-fill: white; -fx-border-radius: 5;");
                    bookButton.setOnAction(e -> PassengerHomeController.openBookingPage(inst));

                    card.getChildren().addAll(header, route, departLabel, returnLabel, shipLabel, bookButton);
                    upcomingListContainer.getChildren().add(card);
                }
            });
        });
    }
}
