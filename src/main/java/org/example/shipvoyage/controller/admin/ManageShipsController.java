package org.example.shipvoyage.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.example.shipvoyage.dao.ShipDAO;
import org.example.shipvoyage.model.Ship;

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
            public TableCell<Ship, Void> call(final TableColumn<Ship, Void> param) {
                return new TableCell<>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button delBtn = new Button("Delete");

                    {
                        editBtn.setOnAction((ActionEvent event) -> {
                            Ship ship = getTableView().getItems().get(getIndex());
                            nameField.setText(ship.getShipName());
                            capacityField.setText(String.valueOf(ship.getCapacity()));
                            selectedShip = ship;
                            saveButton.setText("Update");
                        });

                        delBtn.setOnAction((ActionEvent event) -> {
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
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hBox = new HBox(10, editBtn, delBtn);
                            setGraphic(hBox);
                        }
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
                showAlert(Alert.AlertType.INFORMATION, "Success", "Ship added successfully!");
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
