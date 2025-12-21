package org.example.shipvoyage.controller.passenger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.shipvoyage.dao.TourDAO;
import org.example.shipvoyage.dao.TourInstanceDAO;
import org.example.shipvoyage.model.Tour;
import org.example.shipvoyage.model.TourInstance;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.example.shipvoyage.dao.ShipDAO.getShipById;
import static org.example.shipvoyage.dao.TourDAO.getTourById;
import static org.example.shipvoyage.util.AlertUtil.showWarning;

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
                    })
                    .toList();

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
                    Tour tour = getTourById(t.getTourId());
                    return tour != null &&
                            tour.getFrom().equalsIgnoreCase(from) &&
                            tour.getTo().equalsIgnoreCase(to) &&
                            !t.getStartDate().isBefore(selectedDate);
                })
                .sorted(Comparator.comparing(TourInstance::getStartDate))
                .toList();

        for (TourInstance t : upcomingTourInstances) {
            resultsBox.getChildren().add(createTourCard(t));
        }

        if (resultsBox.getChildren().isEmpty()) {
            Label noResult = new Label("No upcoming tours found for this route.");
            noResult.getStyleClass().add("no-result");
            resultsBox.getChildren().add(noResult);
        }
    }


    private VBox createTourCard(TourInstance t) {

        VBox card = new VBox();
        card.getStyleClass().add("tour-card");

        Label title = new Label("Tour: " + getTourById(t.getTourId()).getTourName());
        title.getStyleClass().add("tour-title");

        Label ship = new Label("Ship: " + getShipById(t.getShipId()).getShipName());
        Label  From = new Label("From: " + getTourById(t.getTourId()).getFrom());
        Label  To = new Label("To: " + getTourById(t.getTourId()).getTo());
        Label duration = new Label("Duration: " + getTourById(t.getTourId()).getDuration() + " Days");
        Label start = new Label("Start: " + t.getStartDate());
        Label end = new Label("End: " + t.getEndDate());

        ship.getStyleClass().add("tour-text");
        From.getStyleClass().add("tour-text");
        To.getStyleClass().add("tour-text");
        duration.getStyleClass().add("tour-text");
        start.getStyleClass().add("tour-text");
        end.getStyleClass().add("tour-text");

        card.getChildren().addAll(
                title,
                ship,
                From,
                To,
                duration,
                start,
                end
        );

        //card.setOnMouseClicked(e -> openTourDetails(t));

        return card;
    }

//    private void openTourDetails(Tour tour) {
//        try {
//            FXMLLoader loader = new FXMLLoader(
//                    getClass().getResource(
//                            "/org/example/shipvoyage/view/passenger/tour-details.fxml"
//                    )
//            );
//            Parent root = loader.load();
//
//            TourDetailsController controller = loader.getController();
//            controller.setTour(tour);
//
//            Stage stage = new Stage();
//            stage.setTitle("Tour Details");
//            stage.setScene(new Scene(root));
//            stage.show();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            showWarning("Unable to open tour details.");
//        }
//    }
}
