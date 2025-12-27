package org.example.shipvoyage.controller.passenger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.example.shipvoyage.dao.BookingDAO;
import org.example.shipvoyage.model.Booking;

import java.util.List;

public class ShowBookingController {

    @FXML
    private VBox bookingContainer;

    @FXML
    private Label noBookingsLabel;

    private int passengerId;

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
        loadBookings();
    }

    private void loadBookings() {
        bookingContainer.getChildren().clear();

        List<Booking> bookings = BookingDAO.getBookingsByPassenger(passengerId);

        if (bookings.isEmpty()) {
            noBookingsLabel.setVisible(true);
            noBookingsLabel.setManaged(true);
            return;
        } else {
            noBookingsLabel.setVisible(false);
            noBookingsLabel.setManaged(false);
        }

        for (Booking booking : bookings) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/org/example/shipvoyage/passenger/show-booking-card.fxml")
                );
                VBox card = loader.load();
                BookingCardController controller = loader.getController();
                controller.setData(booking, this::loadBookings);
                bookingContainer.getChildren().add(card);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
