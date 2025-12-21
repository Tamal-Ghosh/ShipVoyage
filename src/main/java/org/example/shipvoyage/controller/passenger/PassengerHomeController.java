package org.example.shipvoyage.controller.passenger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.example.shipvoyage.dao.TourDAO;
import org.example.shipvoyage.model.Tour;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PassengerHomeController {

    @FXML
    private TextField fromField;

    @FXML
    private TextField toField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button searchButton;

    @FXML
    private VBox centerVBox;

    @FXML
    private VBox resultsBox;

    private ObservableList<String> fromSuggestions;
    private ObservableList<String> toSuggestions;

    @FXML
    public void initialize() {
        loadSuggestions();
        setupAutoComplete(fromField, fromSuggestions);
        setupAutoComplete(toField, toSuggestions);
        searchButton.setOnAction(e -> searchTours());
    }

    private void loadSuggestions() {
        Set<String> fromSet = new HashSet<>();
        Set<String> toSet = new HashSet<>();
        for (Tour t : TourDAO.getAllTours()) {
            fromSet.add(t.getFrom());
            toSet.add(t.getTo());
        }
        fromSuggestions = FXCollections.observableArrayList(fromSet);
        toSuggestions = FXCollections.observableArrayList(toSet);
    }

    private void setupAutoComplete(TextField textField, ObservableList<String> suggestions) {
        ContextMenu contextMenu = new ContextMenu();
        textField.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            String input = textField.getText().toLowerCase();
            if (input.isEmpty()) {
                contextMenu.hide();
                return;
            }
            ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();
            for (String s : suggestions.stream()
                    .filter(s -> s.toLowerCase().startsWith(input))
                    .collect(Collectors.toList())) {
                MenuItem item = new MenuItem(s);
                item.setOnAction(e -> {
                    textField.setText(s);
                    textField.positionCaret(s.length());
                    contextMenu.hide();
                });
                menuItems.add(item);
            }
            if (!menuItems.isEmpty()) {
                contextMenu.getItems().setAll(menuItems);
                if (!contextMenu.isShowing()) contextMenu.show(textField, Side.BOTTOM, 0, 0);
            } else {
                contextMenu.hide();
            }
        });
    }

    private void searchTours() {
        String from = fromField.getText().trim();
        String to = toField.getText().trim();
        if (datePicker.getValue() == null) {
            showAlert("Please select a date.");
            return;
        }

        String date = datePicker.getValue().toString();

        if (from.isEmpty() || to.isEmpty()) {
            showAlert("Please fill From and To locations.");
            return;
        }

        resultsBox.getChildren().clear();

        for (Tour t : TourDAO.getAllTours()) {
            if (t.getFrom().equalsIgnoreCase(from) && t.getTo().equalsIgnoreCase(to)) {
                VBox tourBox = new VBox(5);
                tourBox.setPadding(new Insets(10));
                tourBox.setStyle("-fx-border-color: gray; -fx-border-radius: 5; -fx-border-width: 1; -fx-background-radius: 5;");
                Label nameLabel = new Label("Tour: " + t.getTourName());
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                Label fromLabel = new Label("From: " + t.getFrom());
                Label toLabel = new Label("To: " + t.getTo());
                Label durationLabel = new Label("Duration: " + t.getDuration() + "Days");
                Label descLabel = new Label("Description: " + t.getDescription());
                tourBox.getChildren().addAll(nameLabel, fromLabel, toLabel, durationLabel, descLabel);
                resultsBox.getChildren().add(tourBox);
            }
        }

        if (resultsBox.getChildren().isEmpty()) {
            Label noResult = new Label("No tours found for selected criteria.");
            resultsBox.getChildren().add(noResult);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
