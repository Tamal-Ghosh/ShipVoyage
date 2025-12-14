package org.example.shipvoyage.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.example.shipvoyage.dao.TourDAO;
import org.example.shipvoyage.model.Tour;
import org.example.shipvoyage.util.AlertUtil;

public class ManageToursController {

    public TableView<Tour> tourTable;
    public TableColumn<Tour, Integer> idColumn;
    public TableColumn<Tour, String> nameColumn;
    public TableColumn<Tour, String> routeColumn;
    public TableColumn<Tour, Integer> durationColumn;
    public TableColumn<Tour, Void> actionColumn;

    public TextField nameField;
    public TextField routeField;
    public TextField durationField;
    public TextField descriptionField;

    public Button saveButton;
    public Button clearButton;

    private ObservableList<Tour> tourList = FXCollections.observableArrayList();
    private Tour selectedTour = null;

    public void initialize() {
        TourDAO.createTourTable();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("tourName"));
        routeColumn.setCellValueFactory(new PropertyValueFactory<>("route"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        loadTours();
        addActionButtons();
    }

    private void loadTours() {
        tourList.clear();
        tourList.addAll(TourDAO.getAllTours());
        tourTable.setItems(tourList);
    }

    private void addActionButtons() {
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Tour, Void> call(final TableColumn<Tour, Void> param) {
                return new TableCell<>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button delBtn = new Button("Delete");

                    {
                        editBtn.setOnAction(event -> {
                            Tour tour = getTableView().getItems().get(getIndex());
                            nameField.setText(tour.getTourName());
                            routeField.setText(tour.getRoute());
                            durationField.setText(String.valueOf(tour.getDuration()));
                            descriptionField.setText(tour.getDescription());
                            selectedTour = tour;
                            saveButton.setText("Update");
                        });

                        delBtn.setOnAction(event -> {
                            Tour tour = getTableView().getItems().get(getIndex());
                            boolean deleted = TourDAO.deleteTour(tour.getId());
                            if (deleted) {
                                tourList.remove(tour);
                                AlertUtil.showInfo("Tour deleted successfully!");
                            } else {
                                AlertUtil.showError("Failed to delete tour!");
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

    public void onSaveTour(ActionEvent actionEvent) {
        String name = nameField.getText();
        String route = routeField.getText();
        String durationText = durationField.getText();
        String description = descriptionField.getText();

        if (name.isEmpty() || route.isEmpty() || durationText.isEmpty()) {
            AlertUtil.showWarning("Please fill all fields!");
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationText);
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Duration must be a number!");
            return;
        }

        if (selectedTour == null) {
            Tour tour = new Tour(0, name, route, duration, description);
            TourDAO.addTour(tour);
            AlertUtil.showInfo("Tour added successfully!");
        } else {
            selectedTour.setTourName(name);
            selectedTour.setRoute(route);
            selectedTour.setDuration(duration);
            selectedTour.setDescription(description);

            boolean updated = TourDAO.updateTour(selectedTour);
            if (updated) {
                AlertUtil.showInfo("Tour updated successfully!");
            } else {
                AlertUtil.showError("Failed to update tour!");
            }
            saveButton.setText("Save");
            selectedTour = null;
        }

        clearFields();
        loadTours();
    }

    public void onClearForm(ActionEvent actionEvent) {
        clearFields();
        saveButton.setText("Save");
        selectedTour = null;
    }

    private void clearFields() {
        nameField.clear();
        routeField.clear();
        durationField.clear();
        descriptionField.clear();
    }
}
