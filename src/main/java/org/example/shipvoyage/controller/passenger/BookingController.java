package org.example.shipvoyage.controller.passenger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.example.shipvoyage.dao.BookingDAO;
import org.example.shipvoyage.dao.RoomDAO;
import org.example.shipvoyage.model.Booking;
import org.example.shipvoyage.model.Room;
import org.example.shipvoyage.model.TourInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.shipvoyage.util.AlertUtil.showWarning;
import static org.example.shipvoyage.util.AlertUtil.showInfo;

public class BookingController {

    @FXML
    private VBox mainVBox;

    @FXML
    private GridPane roomGrid;

    @FXML
    private Label totalLabel;

    @FXML
    private Button Confirm_Booking;

    private TourInstance tourInstance;

    private Map<Room, ToggleButton> roomButtons = new HashMap<>();
    private double totalAmount = 0;

    public void setTourInstance(TourInstance instance) {
        this.tourInstance = instance;
        loadRooms();
    }

    private void loadRooms() {
        List<Room> rooms = RoomDAO.getRoomsByShip(tourInstance.getShipId());
        List<Integer> bookedRoomIds = BookingDAO.getBookedRoomIds(tourInstance.getId());
        int cols = 4;
        int row = 0, col = 0;
        for (Room room : rooms) {
            ToggleButton btn = new ToggleButton(room.getRoomNumber());
            btn.setPrefSize(60, 60);
            btn.setStyle(getColorForRoom(room, false));
            if (!room.isAvailable() || bookedRoomIds.contains(room.getId())) {
                btn.setDisable(true);
            }
            btn.setOnAction(e -> handleSelection(room, btn));
            roomGrid.add(btn, col, row);
            roomButtons.put(room, btn);
            col++;
            if (col == cols) {
                col = 0;
                row++;
            }
        }
        Confirm_Booking.setOnAction(e -> onConfirmBooking());
    }

    private void handleSelection(Room room, ToggleButton btn) {
        boolean isSelected = btn.isSelected();
        if (isSelected) {
            totalAmount += room.getPricePerNight();
        } else {
            totalAmount -= room.getPricePerNight();
        }
        btn.setStyle(getColorForRoom(room, isSelected));
        totalLabel.setText("Total: $" + totalAmount);
    }

    private String getColorForRoom(Room room, boolean selected) {
        if (selected) return "-fx-background-color: #2a73ff; -fx-text-fill: white;";
        if (!room.isAvailable()) return "-fx-background-color: orange; -fx-text-fill: white;";
        if (room.getRoomType().equalsIgnoreCase("Single")) return "-fx-background-color: green; -fx-text-fill: white;";
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

        boolean allSuccess = true;
        for (Room room : selectedRooms) {
            Booking booking = new Booking(
                    0,
                    tourInstance.getId(),
                    room.getId(),
                    room.getRoomNumber(),
                    1
            );
            boolean success = BookingDAO.addBooking(booking);
            if (!success) allSuccess = false;
            else roomButtons.get(room).setDisable(true);
        }

        if (allSuccess) showInfo("Booking confirmed successfully!");
        else showWarning("Some rooms could not be booked as they are already taken.");

        totalAmount = 0;
        totalLabel.setText("Total: $0");
        roomButtons.values().forEach(btn -> btn.setSelected(false));
    }
}
