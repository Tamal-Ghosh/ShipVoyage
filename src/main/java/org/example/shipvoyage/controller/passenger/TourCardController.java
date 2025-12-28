package org.example.shipvoyage.controller.passenger;

import org.example.shipvoyage.dao.ShipDAO;
import org.example.shipvoyage.dao.TourDAO;
import org.example.shipvoyage.model.Ship;
import org.example.shipvoyage.model.Tour;
import org.example.shipvoyage.model.TourInstance;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class TourCardController {

    @FXML
    private Label tourName;

    @FXML
    private Label shipName;

    @FXML
    private Label from;

    @FXML
    private Label to;

    @FXML
    private Label duration;

    @FXML
    private Label startDate;

    @FXML
    private Label endDate;

    @FXML
    private Button bookButton;

    private TourInstance tourInstance;

    public void setData(TourInstance instance) {
        this.tourInstance = instance;
        Tour tour = TourDAO.getTourById(instance.getTourId());
        if (tour != null) {
            tourName.setText(tour.getTourName());
            from.setText(tour.getFrom());
            to.setText(tour.getTo());
            duration.setText(tour.getDuration() + " days");

            startDate.setText(instance.getStartDate().toString());
            endDate.setText(instance.getEndDate().toString());

            Ship ship = ShipDAO.getShipById(instance.getShipId());
            if (ship != null) {
                shipName.setText(ship.getShipName());
            } else {
                shipName.setText("Unknown Ship");
            }
        } else {
            tourName.setText("Tour Not Found");
            from.setText("N/A");
            to.setText("N/A");
            duration.setText("N/A");
            startDate.setText(instance.getStartDate().toString());
            endDate.setText(instance.getEndDate().toString());
            shipName.setText("N/A");
        }

        if (bookButton != null) {
            bookButton.setOnAction(e -> openBookingPage());
        }
    }

    private void openBookingPage() {
        PassengerHomeController.openBookingPage(tourInstance);
    }
}
