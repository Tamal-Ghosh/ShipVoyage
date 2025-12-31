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
    public Button saveButton;
    public Spinner<Integer> priceSpinner;

    private ObservableList<Room> roomList = FXCollections.observableArrayList();
    private Room selectedRoom = null;

    public void initialize() {
        priceSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0, 1)
        );
        priceSpinner.setEditable(true);

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
                            priceSpinner.getValueFactory().setValue((int) selectedRoom.getPricePerNight());
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
        if (ship == null) {
            AlertUtil.showWarning("Please select a ship first!");
            return;
        }

        if (ship.getCapacity() <= 0) {
            AlertUtil.showWarning("Ship capacity must be greater than 0!");
            return;
        }

        int currentRoomCount = RoomDAO.getRoomsByShip(ship.getId()).size();

        if (selectedRoom == null && currentRoomCount >= ship.getCapacity()) {
            AlertUtil.showWarning("Cannot add more rooms. Ship capacity reached! Current: " + currentRoomCount + "/" + ship.getCapacity());
            return;
        }

        String roomNumber = roomNumberField.getText();
        String roomType = roomTypeComboBox.getValue();
        double price = priceSpinner.getValue();

        if (roomNumber.isEmpty()) {
            AlertUtil.showWarning("Please enter room number!");
            return;
        }

        if (roomType == null) {
            AlertUtil.showWarning("Please select room type!");
            return;
        }

        if (price <= 0) {
            AlertUtil.showWarning("Please enter valid price!");
            return;
        }

        if (selectedRoom == null) {
            boolean available = true;
            boolean success = RoomDAO.addRoom(new Room(0, ship.getId(), roomNumber, roomType, price, available));
            if (success) {
                AlertUtil.showInfo("Room added successfully!");
                clearFields();
                loadRooms();
            } else {
                AlertUtil.showError("Failed to add room. Room number may already exist for this ship!");
            }
        } else {
            selectedRoom.setRoomNumber(roomNumber);
            selectedRoom.setRoomType(roomType);
            selectedRoom.setPricePerNight(price);
            boolean success = RoomDAO.updateRoom(selectedRoom);
            if (success) {
                AlertUtil.showInfo("Room updated successfully!");
                selectedRoom = null;
                saveButton.setText("Save");
                clearFields();
                loadRooms();
            } else {
                AlertUtil.showError("Failed to update room. Room number may already exist for this ship!");
            }
        }
    }

    private void clearFields() {
        roomNumberField.clear();
        roomTypeComboBox.getSelectionModel().clearSelection();
        priceSpinner.getValueFactory().setValue(0);
    }

    public void onClearRoom(ActionEvent e) {
        clearFields();
        selectedRoom = null;
        saveButton.setText("Save");
    }
}
