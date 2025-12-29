package org.example.shipvoyage.controller.passenger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.shipvoyage.dao.BookingDAO;
import org.example.shipvoyage.dao.RoomDAO;
import org.example.shipvoyage.model.Booking;
import org.example.shipvoyage.model.Room;
import org.example.shipvoyage.model.TourInstance;
import org.example.shipvoyage.model.User;
import static org.example.shipvoyage.util.AlertUtil.showWarning;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BookingController {

    @FXML
    private VBox mainVBox;

    @FXML
    private GridPane roomGrid;

    @FXML
    private Label totalLabel;

    @FXML
    private Button confirmBooking;

    @FXML
    private RadioButton visaRadio;

    @FXML
    private RadioButton bkashRadio;

    private ToggleGroup paymentToggleGroup;

    private TourInstance tourInstance;
    private Map<Room, ToggleButton> roomButtons = new HashMap<>();
    private double totalAmount = 0;
    private User currentUser = Session.loggedInUser;

    @FXML
    public void initialize() {
        paymentToggleGroup = new ToggleGroup();
        visaRadio.setToggleGroup(paymentToggleGroup);
        bkashRadio.setToggleGroup(paymentToggleGroup);
        visaRadio.setSelected(true);
    }

    public void setTourInstance(TourInstance instance) {
        this.tourInstance = instance;
        loadRooms();
    }

    private void loadRooms() {
        List<Room> rooms = RoomDAO.getRoomsByShip(tourInstance.getShipId());
        List<Integer> bookedRoomIds = BookingDAO.getBookedRoomIds(tourInstance.getId());

        int cols = 4, row = 0, col = 0;

        for (Room room : rooms) {
            ToggleButton btn = new ToggleButton(room.getRoomNumber());
            btn.setPrefSize(80, 80);
            btn.setStyle(getColorForRoom(room, false));

            if (!room.isAvailable() || bookedRoomIds.contains(room.getId())) btn.setDisable(true);

            btn.setOnAction(e -> handleSelection(room, btn));
            roomGrid.add(btn, col, row);
            roomButtons.put(room, btn);

            col++;
            if (col == cols) { col = 0; row++; }
        }

        confirmBooking.setOnAction(e -> onConfirmBooking());
    }

    private void handleSelection(Room room, ToggleButton btn) {
        boolean isSelected = btn.isSelected();
        if (isSelected) totalAmount += room.getPricePerNight();
        else totalAmount -= room.getPricePerNight();
        btn.setStyle(getColorForRoom(room, isSelected));
        totalLabel.setText("Total: ৳" + totalAmount);
    }

    private String getColorForRoom(Room room, boolean selected) {
        if (selected) return "-fx-background-color: #2a73ff; -fx-text-fill: white;";
        if (!room.isAvailable()) return "-fx-background-color: orange; -fx-text-fill: white;";
        if ("Single".equalsIgnoreCase(room.getRoomType())) return "-fx-background-color: green; -fx-text-fill: white;";
        return "-fx-background-color: purple; -fx-text-fill: white;";
    }

    @FXML
    private void onConfirmBooking() {
        List<Room> selectedRooms = roomButtons.entrySet().stream()
                .filter(e -> e.getValue().isSelected())
                .map(Map.Entry::getKey)
                .toList();

        if (selectedRooms.isEmpty()) {
            showWarning("Please select at least one room to book.");
            return;
        }

        List<Integer> roomIds = new ArrayList<>();
        List<String> roomNumbers = new ArrayList<>();

        for (Room room : selectedRooms) {
            roomIds.add(room.getId());
            roomNumbers.add(room.getRoomNumber());
        }

        String selectedPaymentMethod;
        if (visaRadio.isSelected()) selectedPaymentMethod = "Visa";
        else if (bkashRadio.isSelected()) selectedPaymentMethod = "bKash";
        else {
            showWarning("Please select a payment method.");
            return;
        }

        Booking booking = new Booking(
            0,
            tourInstance.getId(),
            currentUser.getUserID(),
            roomIds,
            roomNumbers,
            totalAmount,
            "Confirmed",
            selectedPaymentMethod,
            "Paid"
        );

        try {
            FXMLLoader loader;
            if ("Visa".equals(selectedPaymentMethod)) {
                loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/passenger/visa-payment.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/passenger/bkash-payment.fxml"));
            }
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) mainVBox.getScene().getWindow();
            stage.setTitle(selectedPaymentMethod + " Payment");
            stage.setScene(scene);
            PaymentController controller = loader.getController();
            controller.setBooking(booking);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showWarning("Could not open payment page.");
        }
    }

}
