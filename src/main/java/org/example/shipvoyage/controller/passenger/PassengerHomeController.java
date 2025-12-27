package org.example.shipvoyage.controller.passenger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.shipvoyage.dao.TourDAO;
import org.example.shipvoyage.dao.TourInstanceDAO;
import org.example.shipvoyage.model.TourInstance;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.example.shipvoyage.util.AlertUtil.showWarning;

public class PassengerHomeController {

    @FXML
    private BorderPane homeBorderPane;

    @FXML
    private TextField fromField;

    @FXML
    private TextField toField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button searchButton;

    @FXML
    private VBox resultsBox;

    private Node homeCenter;
    private ObservableList<String> fromSuggestions;
    private ObservableList<String> toSuggestions;

    @FXML
    public void initialize() {
        homeCenter = homeBorderPane.getCenter();
        loadSuggestions();
        setupAutoComplete(fromField, fromSuggestions);
        setupAutoComplete(toField, toSuggestions);
        searchButton.setOnAction(e -> searchTours());
    }

    private void loadSuggestions() {
        Set<String> fromSet = new HashSet<>();
        Set<String> toSet = new HashSet<>();
        for (var t : TourDAO.getAllTours()) {
            fromSet.add(t.getFrom());
            toSet.add(t.getTo());
        }
        fromSuggestions = FXCollections.observableArrayList(fromSet);
        toSuggestions = FXCollections.observableArrayList(toSet);
    }

    private void setupAutoComplete(TextField field, ObservableList<String> suggestions) {
        ContextMenu menu = new ContextMenu();
        field.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            String input = field.getText().toLowerCase();
            if (input.isEmpty()) {
                menu.hide();
                return;
            }
            List<MenuItem> items = suggestions.stream()
                    .filter(s -> s.toLowerCase().startsWith(input))
                    .map(s -> {
                        MenuItem item = new MenuItem(s);
                        item.setOnAction(ev -> {
                            field.setText(s);
                            field.positionCaret(s.length());
                            menu.hide();
                        });
                        return item;
                    }).toList();
            if (!items.isEmpty()) {
                menu.getItems().setAll(items);
                menu.show(field, Side.BOTTOM, 0, 0);
            } else {
                menu.hide();
            }
        });
    }

    private void searchTours() {
        String from = fromField.getText().trim();
        String to = toField.getText().trim();
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            showWarning("Please select a date.");
            return;
        }
        if (from.isEmpty() || to.isEmpty()) {
            showWarning("Please fill From and To.");
            return;
        }

        resultsBox.getChildren().clear();

        List<TourInstance> upcomingTourInstances = TourInstanceDAO.getAllTourInstances().stream()
                .filter(t -> {
                    var tour = TourDAO.getTourById(t.getTourId());
                    return tour != null &&
                            tour.getFrom().equalsIgnoreCase(from) &&
                            tour.getTo().equalsIgnoreCase(to) &&
                            !t.getStartDate().isBefore(selectedDate);
                }).toList();

        if (upcomingTourInstances.isEmpty()) {
            Label noToursLabel = new Label("No upcoming tours found for this route.");
            noToursLabel.setStyle("-fx-text-fill: #F4F4F4; -fx-font-size: 16px; -fx-font-weight: bold;");
            resultsBox.getChildren().add(noToursLabel);
            return;
        }

        for (TourInstance t : upcomingTourInstances) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/passenger/tour-card.fxml"));
                Parent card = loader.load();
                card.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 15; -fx-padding: 10;");
                TourCardController controller = loader.getController();
                controller.setData(t);
                resultsBox.getChildren().add(card);
            } catch (Exception e) {
                e.printStackTrace();
                showWarning("Error loading tour card.");
            }
        }
    }



    public static void openBookingPage(TourInstance instance) {
        try {
            FXMLLoader loader = new FXMLLoader(PassengerHomeController.class.getResource("/org/example/shipvoyage/passenger/passenger-booking.fxml"));
            Parent root = loader.load();
            BookingController controller = loader.getController();
            controller.setTourInstance(instance);
            Stage stage = new Stage();
            stage.setTitle("Book Rooms");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showWarning("Unable to open booking page.");
        }
    }

    @FXML
    private void onLogoutClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/user-type.fxml"));
        Stage stage = (Stage) homeBorderPane.getScene().getWindow();
        stage.setScene(new javafx.scene.Scene(loader.load()));
        stage.show();
    }

    @FXML
    private void onProfileClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/passenger/profile.fxml"));
        Parent profileView = loader.load();
        homeBorderPane.setCenter(profileView);
    }

    @FXML
    private void onHomeClick() {
        fromField.clear();
        toField.clear();
        datePicker.setValue(null);
        resultsBox.getChildren().clear();
        homeBorderPane.setCenter(homeCenter);
    }

    @FXML
    private void onBookingsClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/passenger/show-passenger-booking.fxml"));
        Parent bookingsView = loader.load();
        ShowBookingController controller = loader.getController();
        controller.setPassengerId(Session.loggedInUser.getUserID());
        homeBorderPane.setCenter(bookingsView);
    }
}
