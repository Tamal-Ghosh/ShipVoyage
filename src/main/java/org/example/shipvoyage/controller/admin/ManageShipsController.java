package org.example.shipvoyage.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.example.shipvoyage.dao.RoomDAO;
import org.example.shipvoyage.dao.ShipDAO;
import org.example.shipvoyage.model.Room;
import org.example.shipvoyage.model.Ship;

import java.util.Optional;

public class ManageShipsController {

    public TableView<Ship> shipTable;
    public TableColumn<Ship, Integer> idColumn;
    public TableColumn<Ship, String> nameColumn;
    public TableColumn<Ship, Integer> capacityColumn;
    public TableColumn<Ship, Void> actionsColumn;

    public TextField nameField;
    public TextField capacityField;
    public Button saveButton;
    public Button cancelButton;

    private ObservableList<Ship> shipList = FXCollections.observableArrayList();
    private Ship selectedShip = null;

    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("shipName"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        loadShips();
        addActionButtons();
    }

    private void loadShips() {
        shipList.clear();
        shipList.addAll(ShipDAO.getAllShips());
        shipTable.setItems(shipList);
    }

    private void addActionButtons() {
        actionsColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Ship, Void> call(TableColumn<Ship, Void> param) {
                return new TableCell<>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button delBtn = new Button("Delete");

                    {
                        editBtn.setOnAction(event -> {
                            Ship ship = getTableView().getItems().get(getIndex());
                            nameField.setText(ship.getShipName());
                            capacityField.setText(String.valueOf(ship.getCapacity()));
                            selectedShip = ship;
                            saveButton.setText("Update");
                        });

                        delBtn.setOnAction(event -> {
                            Ship ship = getTableView().getItems().get(getIndex());
                            boolean deleted = ShipDAO.deleteShip(ship.getId());
                            if (deleted) {
                                shipList.remove(ship);
                                showAlert(Alert.AlertType.INFORMATION, "Deleted", "Ship deleted successfully!");
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete ship!");
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) setGraphic(null);
                        else setGraphic(new HBox(10, editBtn, delBtn));
                    }
                };
            }
        });
    }

    public void OnShipSaveClicked(ActionEvent actionEvent) {
        String name = nameField.getText();
        String capacityText = capacityField.getText();

        if (name.isEmpty() || capacityText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter ship name and capacity!");
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Capacity must be a number!");
            return;
        }

        if (selectedShip == null) {
            boolean inserted = ShipDAO.insertShip(name, capacity);
            if (inserted) {
                Ship newShip = ShipDAO.getAllShips().get(ShipDAO.getAllShips().size() - 1);
                addRoomsForShip(newShip.getId(), capacity);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Ship and rooms added successfully!");
                clearFields();
                loadShips();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add ship!");
            }
        } else {
            boolean updated = ShipDAO.updateShip(selectedShip.getId(), name, capacity);
            if (updated) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Ship updated successfully!");
                clearFields();
                loadShips();
                saveButton.setText("Save");
                selectedShip = null;
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update ship!");
            }
        }
    }

    private void addRoomsForShip(int shipId, int capacity) {
        for (int i = 1; i <= capacity; i++) {
            TextInputDialog roomNumberDialog = new TextInputDialog();
            roomNumberDialog.setTitle("Room Information");
            roomNumberDialog.setHeaderText("Enter room number for Room " + i);
            roomNumberDialog.setContentText("Room Number:");
            Optional<String> roomNumberOpt = roomNumberDialog.showAndWait();
            if (roomNumberOpt.isEmpty()) continue;
            String roomNumber = roomNumberOpt.get();

            ChoiceDialog<String> roomTypeDialog = new ChoiceDialog<>("Single", "Single", "Double");
            roomTypeDialog.setTitle("Room Type");
            roomTypeDialog.setHeaderText("Select room type for Room " + i);
            roomTypeDialog.setContentText("Room Type:");
            Optional<String> roomTypeOpt = roomTypeDialog.showAndWait();
            if (roomTypeOpt.isEmpty()) continue;
            String roomType = roomTypeOpt.get();

            TextInputDialog priceDialog = new TextInputDialog();
            priceDialog.setTitle("Room Price");
            priceDialog.setHeaderText("Enter price per night for Room " + i);
            priceDialog.setContentText("Price:");
            Optional<String> priceOpt = priceDialog.showAndWait();
            double price = 0;
            if (priceOpt.isPresent()) {
                try {
                    price = Double.parseDouble(priceOpt.get());
                } catch (NumberFormatException e) {
                    price = 0;
                }
            }

            RoomDAO.addRoom(new Room(0, shipId, roomNumber, roomType, price));
        }
    }

    public void OnShipCancelClicked(ActionEvent actionEvent) {
        clearFields();
        saveButton.setText("Save");
        selectedShip = null;
    }

    private void clearFields() {
        nameField.clear();
        capacityField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
