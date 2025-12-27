package org.example.shipvoyage.controller.passenger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.shipvoyage.dao.BookingDAO;
import org.example.shipvoyage.dao.TourDAO;
import org.example.shipvoyage.dao.TourInstanceDAO;
import org.example.shipvoyage.model.Booking;
import org.example.shipvoyage.model.Tour;
import org.example.shipvoyage.model.TourInstance;

import static org.example.shipvoyage.util.AlertUtil.*;

public class BookingCardController {

    @FXML private Label tourName;
    @FXML private Label from;
    @FXML private Label to;
    @FXML private Label startDate;
    @FXML private Label endDate;
    @FXML private Label totalPrice;
    @FXML private Button cancelButton;

    private Booking booking;
    private Runnable onCancel;

    public void setData(Booking booking, Runnable onCancel) {
        this.booking = booking;
        this.onCancel = onCancel;

        TourInstance instance = TourInstanceDAO.getTourInstanceById(booking.getTourInstanceId());
        Tour tour = TourDAO.getTourById(instance.getTourId());

        tourName.setText(tour.getTourName());
        from.setText(tour.getFrom());
        to.setText(tour.getTo());
        startDate.setText(instance.getStartDate().toString());
        endDate.setText(instance.getEndDate().toString());
        totalPrice.setText(String.format("%,.0f ৳", booking.getTotalPrice()));

        cancelButton.setOnAction(e -> cancelBooking(instance));
    }

    private void cancelBooking(TourInstance tourInstance) {
        if (tourInstance.getStartDate().isAfter(java.time.LocalDate.now().plusDays(1))) {
            boolean success = BookingDAO.cancelBookingByInstanceAndPassenger(
                    booking.getTourInstanceId(),
                    booking.getPassengerId()
            );
            if (success) {
                showInfo("Booking cancelled successfully");
                onCancel.run();
            } else {
                showWarning("Unable to cancel booking");
            }
        } else {
            showWarning("Booking cannot be cancelled less than 1 day before the tour starts");
        }
    }
}
