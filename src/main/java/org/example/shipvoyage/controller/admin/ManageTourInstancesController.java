package org.example.shipvoyage.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.example.shipvoyage.dao.ShipDAO;
import org.example.shipvoyage.dao.TourDAO;
import org.example.shipvoyage.dao.TourInstanceDAO;
import org.example.shipvoyage.model.Ship;
import org.example.shipvoyage.model.Tour;
import org.example.shipvoyage.model.TourInstance;
import org.example.shipvoyage.util.AlertUtil;

import java.time.LocalDate;
import java.util.List;

public class ManageTourInstancesController {

    @FXML private TableView<TourInstance> instanceTable;
    @FXML private TableColumn<TourInstance, Integer> idColumn;
    @FXML private TableColumn<TourInstance, String> tourIdColumn;
    @FXML private TableColumn<TourInstance, String> shipIdColumn;
    @FXML private TableColumn<TourInstance, LocalDate> startDateColumn;
    @FXML private TableColumn<TourInstance, LocalDate> endDateColumn;
    @FXML private TableColumn<TourInstance, Void> actionColumn;

    @FXML private ComboBox<Tour> tourComboBox;
    @FXML private ComboBox<Ship> shipComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML private Button saveButton;
    @FXML private Button clearButton;

    private ObservableList<TourInstance> instanceList = FXCollections.observableArrayList();
    private TourInstance selectedInstance = null;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        tourIdColumn.setCellValueFactory(cellData -> {
            int tourId = cellData.getValue().getTourId();
            Tour tour = TourDAO.getTourById(tourId);
            String name = tour != null ? tour.getTourName() : "";
            return new javafx.beans.property.SimpleStringProperty(name);
        });

        shipIdColumn.setCellValueFactory(cellData -> {
            int shipId = cellData.getValue().getShipId();
            Ship ship = ShipDAO.getShipById(shipId);
            String name = ship != null ? ship.getShipName() : "";
            return new javafx.beans.property.SimpleStringProperty(name);
        });

        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        loadTours();
        loadShips();
        loadInstances();
        addActionButtons();

        startDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> computeEndDate());
        tourComboBox.valueProperty().addListener((obs, oldTour, newTour) -> computeEndDate());
    }

    private void loadTours() {
        List<Tour> tours = TourDAO.getAllTours();
        ObservableList<Tour> tourList = FXCollections.observableArrayList(tours);
        tourComboBox.setItems(tourList);

        tourComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Tour item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTourName());
            }
        });
        tourComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Tour item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTourName());
            }
        });
    }

    private void loadShips() {
        List<Ship> ships = ShipDAO.getAllShips();
        ObservableList<Ship> shipList = FXCollections.observableArrayList(ships);
        shipComboBox.setItems(shipList);

        shipComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Ship item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getShipName());
            }
        });
        shipComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Ship item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getShipName());
            }
        });
    }

    private void computeEndDate() {
        Tour selectedTour = tourComboBox.getValue();
        LocalDate start = startDatePicker.getValue();
        if (selectedTour == null || start == null) return;

        int duration = selectedTour.getDuration();
        if (duration <= 0) {
            AlertUtil.showWarning("Invalid tour duration.");
            return;
        }

        endDatePicker.setValue(start.plusDays(duration));
        endDatePicker.setEditable(false);
    }

    private void loadInstances() {
        instanceList.clear();
        instanceList.addAll(TourInstanceDAO.getAllTourInstances());
        instanceTable.setItems(instanceList);
    }

    private void addActionButtons() {
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<TourInstance, Void> call(TableColumn<TourInstance, Void> param) {
                return new TableCell<>() {

                    private final Button editBtn = new Button("Edit");
                    private final Button delBtn = new Button("Delete");

                    {
                        editBtn.setOnAction(e -> {
                            TourInstance ti = getTableView().getItems().get(getIndex());
                            tourComboBox.setValue(TourDAO.getTourById(ti.getTourId()));
                            shipComboBox.setValue(ShipDAO.getShipById(ti.getShipId()));
                            startDatePicker.setValue(ti.getStartDate());
                            endDatePicker.setValue(ti.getEndDate());
                            selectedInstance = ti;
                            saveButton.setText("Update");
                        });

                        delBtn.setOnAction(e -> {
                            TourInstance ti = getTableView().getItems().get(getIndex());
                            boolean deleted = TourInstanceDAO.deleteTourInstance(ti);
                            if (deleted) {
                                instanceList.remove(ti);
                                AlertUtil.showInfo("Tour instance deleted successfully!");
                            } else {
                                AlertUtil.showError("Failed to delete tour instance!");
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) setGraphic(null);
                        else setGraphic(new HBox(10, editBtn, delBtn));
                    }
                };
            }
        });
    }

    @FXML
    public void onSaveInstance(ActionEvent event) {
        Tour selectedTour = tourComboBox.getValue();
        Ship selectedShip = shipComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (selectedTour == null || selectedShip == null || startDate == null || endDate == null) {
            AlertUtil.showWarning("Please select tour, ship, and start date!");
            return;
        }

        int tourId = selectedTour.getId();
        int shipId = selectedShip.getId();

        if (selectedInstance == null) {
            boolean inserted = TourInstanceDAO.addTourInstance(tourId, shipId, startDate, endDate);
            if (inserted) AlertUtil.showInfo("Tour instance added successfully!");
            else AlertUtil.showError("Failed to add tour instance!");
        } else {
            selectedInstance.setTourId(tourId);
            selectedInstance.setShipId(shipId);
            selectedInstance.setStartDate(startDate);
            selectedInstance.setEndDate(endDate);

            boolean updated = TourInstanceDAO.updateTourInstance(selectedInstance);
            if (updated) AlertUtil.showInfo("Tour instance updated successfully!");
            else AlertUtil.showError("Failed to update tour instance!");

            saveButton.setText("Save");
            selectedInstance = null;
        }

        clearFields();
        loadInstances();
    }

    @FXML
    public void onClearForm(ActionEvent event) {
        clearFields();
        saveButton.setText("Save");
        selectedInstance = null;
    }

    private void clearFields() {
        tourComboBox.setValue(null);
        shipComboBox.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }
}
