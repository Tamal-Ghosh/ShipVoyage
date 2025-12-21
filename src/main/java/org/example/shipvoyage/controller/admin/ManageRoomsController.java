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
import org.example.shipvoyage.util.AlertUtil;

public class ManageRoomsController {

    public TableView<Room> roomTable;
    public TableColumn<Room, Integer> idColumn;
    public TableColumn<Room, String> roomNumberColumn;
    public TableColumn<Room, String> roomTypeColumn;
    public TableColumn<Room, Double> priceColumn;
    public TableColumn<Room, Void> actionColumn;
    public ComboBox<Ship> shipComboBox;
    public TextField roomNumberField;
    public ComboBox<String> roomTypeComboBox;
    public TextField priceField;
    public Button saveButton;

    private ObservableList<Room> roomList = FXCollections.observableArrayList();
    private Room selectedRoom = null;

    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));

        roomTypeComboBox.setItems(FXCollections.observableArrayList("Single", "Double"));

        loadShips();
        shipComboBox.setOnAction(e -> loadRooms());
        addActionButtons();
    }

    private void loadShips() {
        shipComboBox.setItems(FXCollections.observableArrayList(ShipDAO.getAllShips()));
        shipComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Ship ship, boolean empty) {
                super.updateItem(ship, empty);
                setText(empty || ship == null ? null : ship.getShipName());
            }
        });
        shipComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Ship ship, boolean empty) {
                super.updateItem(ship, empty);
                setText(empty || ship == null ? null : ship.getShipName());
            }
        });
    }

    private void loadRooms() {
        Ship ship = shipComboBox.getValue();
        if (ship == null) {
            roomTable.setItems(FXCollections.observableArrayList());
            return;
        }
        roomList.setAll(RoomDAO.getRoomsByShip(ship.getId()));
        roomTable.setItems(roomList);
    }

    private void addActionButtons() {
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Room, Void> call(TableColumn<Room, Void> param) {
                return new TableCell<>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button delBtn = new Button("Delete");

                    {
                        editBtn.setOnAction(e -> {
                            selectedRoom = getTableView().getItems().get(getIndex());
                            roomNumberField.setText(selectedRoom.getRoomNumber());
                            roomTypeComboBox.setValue(selectedRoom.getRoomType());
                            priceField.setText(String.valueOf(selectedRoom.getPricePerNight()));
                            saveButton.setText("Update");
                        });
                        delBtn.setOnAction(e -> {
                            Room r = getTableView().getItems().get(getIndex());
                            if (RoomDAO.deleteRoom(r.getId())) {
                                roomList.remove(r);
                                AlertUtil.showInfo("Room deleted successfully!");
                            } else {
                                AlertUtil.showWarning("Failed to delete room!");
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : new HBox(10, editBtn, delBtn));
                    }
                };
            }
        });
    }

    public void onSaveRoom(ActionEvent e) {
        Ship ship = shipComboBox.getValue();
        if (ship == null) return;

        int currentRoomCount = RoomDAO.getRoomsByShip(ship.getId()).size();

        if (selectedRoom == null && currentRoomCount >= ship.getCapacity()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Capacity Reached");
            alert.setHeaderText(null);
            alert.setContentText("Cannot add more rooms. Ship capacity reached!");
            alert.showAndWait();
            return;
        }

        String roomNumber = roomNumberField.getText();
        String roomType = roomTypeComboBox.getValue();
        double price;

        if (roomNumber.isEmpty() || roomType == null || priceField.getText().isEmpty()) return;

        try {
            price = Double.parseDouble(priceField.getText());
        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Price");
            alert.setHeaderText(null);
            alert.setContentText("Price must be a valid number!");
            alert.showAndWait();
            return;
        }

        if (selectedRoom == null) {
            RoomDAO.addRoom(new Room(0, ship.getId(), roomNumber, roomType, price));
            AlertUtil.showInfo("Room added successfully!");
        } else {
            selectedRoom.setRoomNumber(roomNumber);
            selectedRoom.setRoomType(roomType);
            selectedRoom.setPricePerNight(price);
            RoomDAO.updateRoom(selectedRoom);
            selectedRoom = null;
            saveButton.setText("Save");
            AlertUtil.showInfo("Room updated successfully!");
        }

        clearFields();
        loadRooms();
    }

    private void clearFields() {
        roomNumberField.clear();
        roomTypeComboBox.getSelectionModel().clearSelection();
        priceField.clear();
    }
}
