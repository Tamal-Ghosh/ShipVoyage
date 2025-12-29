package org.example.shipvoyage.controller.passenger;

import org.example.shipvoyage.dao.BookingDAO;
import org.example.shipvoyage.model.Booking;
import org.example.shipvoyage.util.AlertUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PaymentController {

    @FXML
    private Label amountLabel;

    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField cardHolderField;
    @FXML
    private TextField expiryField;
    @FXML
    private TextField cvvField;

    @FXML
    private TextField phoneField;
    @FXML
    private TextField pinField;

    private Booking booking;
    private double amount;

    public void setBooking(Booking booking) {
        this.booking = booking;
        this.amount = booking.getTotalPrice();
        amountLabel.setText("Amount: " + amount + " BDT");
    }

    @FXML
    private void onPayVisa() {
        if (cardNumberField.getText().isEmpty() || cardHolderField.getText().isEmpty() ||
                expiryField.getText().isEmpty() || cvvField.getText().isEmpty()) {
            AlertUtil.showWarning("Please fill all Visa details");
            return;
        }
        processPayment("Visa");
    }

    @FXML
    private void onPayBkash() {
        if (phoneField.getText().isEmpty() || pinField.getText().isEmpty()) {
            AlertUtil.showWarning("Please fill bKash details");
            return;
        }
        processPayment("bKash");
    }

    private void processPayment(String method) {
        booking.setPaymentMethod(method);
        booking.setPaymentStatus("Paid");
        booking.setStatus("Confirmed");
        booking.setTotalPrice(amount);
        boolean success = BookingDAO.addBooking(booking);
        
        if (success) {
            AlertUtil.showInfo("Payment successful! Your booking is confirmed.");
            Stage stage = (Stage) amountLabel.getScene().getWindow();
            stage.close();
        } else {
            AlertUtil.showWarning("Payment failed or rooms already booked. Please try again!");
        }
    }
}
