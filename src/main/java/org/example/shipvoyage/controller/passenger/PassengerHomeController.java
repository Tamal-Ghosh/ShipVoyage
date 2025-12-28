package org.example.shipvoyage.controller.passenger;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.example.shipvoyage.dao.ShipDAO;
import org.example.shipvoyage.dao.TourDAO;
import org.example.shipvoyage.dao.TourInstanceDAO;
import org.example.shipvoyage.model.TourInstance;
import static org.example.shipvoyage.util.AlertUtil.showWarning;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PassengerHomeController {

    @FXML
    private VBox mainContainer;

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

    @FXML
    private VBox centerVBox;

    @FXML
    private HBox tourCardsContainer;

    @FXML
    private Button prevButton;

    @FXML
    private Button nextButton;

    private ObservableList<String> fromSuggestions;
    private ObservableList<String> toSuggestions;
    private List<TourInstance> allTourInstances = new java.util.ArrayList<>();
    private int currentPage = 0;
    private static final int TOURS_PER_PAGE = 4;

    @FXML
    public void initialize() {
        loadSuggestions();
        setupAutoComplete(fromField, fromSuggestions);
        setupAutoComplete(toField, toSuggestions);
        searchButton.setOnAction(e -> searchTours());
        
        // Setup navigation buttons
        if (prevButton != null) {
            prevButton.setOnAction(e -> showPreviousPage());
        }
        if (nextButton != null) {
            nextButton.setOnAction(e -> showNextPage());
        }
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



    private void searchTours() {
        String from = fromField.getText();
        String to = toField.getText();
        if (from == null) from = "";
        if (to == null) to = "";
        from = from.trim();
        to = to.trim();
        final String finalFrom = from;
        final String finalTo = to;
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            showWarning("Please select a date.");
            return;
        }
        if (finalFrom.isEmpty() || finalTo.isEmpty()) {
            showWarning("Please fill From and To.");
            return;
        }

        tourCardsContainer.getChildren().clear();
        final LocalDate finalSelectedDate = selectedDate;

        allTourInstances = TourInstanceDAO.getAllTourInstances().stream()
                .filter(t -> {
                    var tour = TourDAO.getTourById(t.getTourId());
                    return tour != null &&
                            tour.getFrom().equalsIgnoreCase(finalFrom) &&
                            tour.getTo().equalsIgnoreCase(finalTo) &&
                            !t.getStartDate().isBefore(finalSelectedDate);
                }).toList();

        if (allTourInstances.isEmpty()) {
            Label noToursLabel = new Label("No upcoming tours found for this route.");
            noToursLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14px; -fx-padding: 20;");
            tourCardsContainer.getChildren().add(noToursLabel);
            resultsBox.setVisible(true);
            resultsBox.setManaged(true);
            prevButton.setVisible(false);
            nextButton.setVisible(false);
            return;
        }

        currentPage = 0;
        displayCurrentPage();
        
        // Show results section
        resultsBox.setVisible(true);
        resultsBox.setManaged(true);
    }

    private void displayCurrentPage() {
        tourCardsContainer.getChildren().clear();
        
        int startIndex = currentPage * TOURS_PER_PAGE;
        int endIndex = Math.min(startIndex + TOURS_PER_PAGE, allTourInstances.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            TourInstance t = allTourInstances.get(i);
            var tour = TourDAO.getTourById(t.getTourId());
            var ship = ShipDAO.getShipById(t.getShipId());
            
            if (tour != null && ship != null) {
                VBox card = createTourCard(tour, t, ship);
                tourCardsContainer.getChildren().add(card);
            }
        }
        
        // Update navigation buttons visibility
        prevButton.setVisible(currentPage > 0);
        nextButton.setVisible(endIndex < allTourInstances.size());
    }

    private void showPreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            displayCurrentPage();
        }
    }

    private void showNextPage() {
        int maxPage = (allTourInstances.size() - 1) / TOURS_PER_PAGE;
        if (currentPage < maxPage) {
            currentPage++;
            displayCurrentPage();
        }
    }

    private VBox createTourCard(org.example.shipvoyage.model.Tour tour, TourInstance instance, org.example.shipvoyage.model.Ship ship) {
        VBox card = new VBox();
        card.setStyle("-fx-border-color: #DDD; -fx-border-radius: 8; -fx-padding: 15; -fx-background-color: white; -fx-spacing: 8;");
        card.setPrefWidth(280);
        
        Label tourName = new Label(tour.getTourName());
        tourName.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");
        
        Label route = new Label(tour.getFrom() + " → " + tour.getTo());
        route.setStyle("-fx-text-fill: #666; -fx-font-size: 12;");
        
        Label departLabel = new Label("Depart: " + instance.getStartDate());
        departLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11;");
        
        Label returnLabel = new Label("Return: " + instance.getEndDate());
        returnLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11;");
        
        Label duration = new Label("Duration: " + tour.getDuration() + " days");
        duration.setStyle("-fx-text-fill: #666; -fx-font-size: 11;");
        
        Label shipLabel = new Label("Ship: " + ship.getShipName());
        shipLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11;");
        
        Label price = new Label("$" + (tour.getDuration() * 100));
        price.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #FF6B35;");
        
        Button bookButton = new Button("Book Now");
        bookButton.setStyle("-fx-padding: 8 20; -fx-font-size: 12; -fx-background-color: #FF6B35; -fx-text-fill: white; -fx-border-radius: 5;");
        bookButton.setOnAction(e -> PassengerHomeController.openBookingPage(instance));
        
        card.getChildren().addAll(tourName, route, departLabel, returnLabel, duration, shipLabel, price, bookButton);
        return card;
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
        Stage stage = (Stage) mainContainer.getScene().getWindow();
        stage.setScene(new javafx.scene.Scene(loader.load()));
        stage.show();
    }

    @FXML
    private void onProfileClick() throws IOException {
        // TODO: Navigate to profile page or show in dialog
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/passenger/profile.fxml"));
        Stage stage = new Stage();
        stage.setTitle("My Profile");
        stage.setScene(new javafx.scene.Scene(loader.load()));
        stage.show();
    }

    @FXML
    private void onHomeClick() {
        fromField.clear();
        toField.clear();
        datePicker.setValue(null);
        tourCardsContainer.getChildren().clear();
        allTourInstances.clear();
        currentPage = 0;
        resultsBox.setVisible(false);
        resultsBox.setManaged(false);
        prevButton.setVisible(false);
        nextButton.setVisible(false);
    }

    @FXML
    private void onBookingsClick() throws IOException {
        // TODO: Navigate to bookings page or show in dialog
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/passenger/show-passenger-booking.fxml"));
        Stage stage = new Stage();
        stage.setTitle("My Bookings");
        stage.setScene(new javafx.scene.Scene(loader.load()));
        ShowBookingController controller = loader.getController();
        controller.setPassengerId(Session.loggedInUser.getUserID());
        stage.show();
    }
}
