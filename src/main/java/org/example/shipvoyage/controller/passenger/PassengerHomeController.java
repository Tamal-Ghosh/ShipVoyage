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

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PassengerHomeController {

    @FXML
    private VBox mainContainer;

    @FXML
    private VBox heroSection;

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

    @FXML
    private MenuButton userMenuButton;

    @FXML
    private ScrollPane contentScroll;

    @FXML
    private VBox upcomingContainer;

    @FXML
    private FlowPane featuredContainer;


    private ObservableList<String> fromSuggestions;
    private ObservableList<String> toSuggestions;
    private List<TourInstance> allTourInstances = new java.util.ArrayList<>();
    private List<TourInstance> upcomingInstances = new java.util.ArrayList<>();
    private int currentPage = 0;
    private int upcomingIndex = 0;
    private Timeline upcomingTimeline;
    private static final int TOURS_PER_PAGE = 4;
    private java.util.List<Node> homeContentBackup;

    @FXML
    public void initialize() {
        loadSuggestions();
        setupAutoComplete(fromField, fromSuggestions);
        setupAutoComplete(toField, toSuggestions);
        searchButton.setOnAction(e -> searchTours());
        homeContentBackup = new java.util.ArrayList<>(centerVBox.getChildren());
        if (userMenuButton != null && Session.loggedInUser != null) {
            String name = Session.loggedInUser.getUsername();
            if (name == null || name.isBlank()) name = "User";
            userMenuButton.setText("👤 " + name);
        }
        if (prevButton != null) {
            prevButton.setOnAction(e -> showPreviousPage());
        }
        if (nextButton != null) {
            nextButton.setOnAction(e -> showNextPage());
        }

        loadUpcomingTrips();
        loadFeaturedPhotos();
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

        allTourInstances = new java.util.ArrayList<>(
            TourInstanceDAO.getAllTourInstances().stream()
                .filter(t -> {
                    var tour = TourDAO.getTourById(t.getTourId());
                    return tour != null &&
                            tour.getFrom().equalsIgnoreCase(finalFrom) &&
                            tour.getTo().equalsIgnoreCase(finalTo) &&
                            !t.getStartDate().isBefore(finalSelectedDate);
            }).toList()
        );

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
        
        resultsBox.setVisible(true);
        resultsBox.setManaged(true);
    }

    private void displayCurrentPage() {
        tourCardsContainer.getChildren().clear();
        
        tourCardsContainer.setVisible(true);
        tourCardsContainer.setManaged(true);
        
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
        
        boolean hasPrev = currentPage > 0;
        boolean hasNext = endIndex < allTourInstances.size();
        prevButton.setVisible(hasPrev);
        nextButton.setVisible(hasNext);
        prevButton.setManaged(hasPrev);
        nextButton.setManaged(hasNext);
    }

    private void loadUpcomingTrips() {
        if (upcomingContainer == null) return;
        upcomingContainer.getChildren().clear();
        LocalDate today = LocalDate.now();
        upcomingInstances = TourInstanceDAO.getAllTourInstances().stream()
            .filter(t -> !t.getStartDate().isBefore(today))
            .sorted(java.util.Comparator.comparing(TourInstance::getStartDate))
            .toList();

        if (upcomingTimeline != null) {
            upcomingTimeline.stop();
        }

        if (upcomingInstances.isEmpty()) {
            Label none = new Label("No upcoming trips found.");
            none.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 13px;");
            upcomingContainer.getChildren().add(none);
            return;
        }

        upcomingIndex = 0;
        showUpcomingCards();

        if (upcomingInstances.size() > 2) {
            upcomingTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> advanceUpcoming()));
            upcomingTimeline.setCycleCount(Timeline.INDEFINITE);
            upcomingTimeline.play();
        }
    }

    private void advanceUpcoming() {
        if (upcomingInstances.isEmpty()) return;
        upcomingIndex = (upcomingIndex + 1) % upcomingInstances.size();
        showUpcomingCards();
    }

    private void showUpcomingCards() {
        if (upcomingContainer == null || upcomingInstances.isEmpty()) return;
        upcomingContainer.getChildren().clear();
        int count = Math.min(2, upcomingInstances.size());
        for (int i = 0; i < count; i++) {
            int idx = (upcomingIndex + i) % upcomingInstances.size();
            TourInstance inst = upcomingInstances.get(idx);
            var tour = TourDAO.getTourById(inst.getTourId());
            var ship = ShipDAO.getShipById(inst.getShipId());
            if (tour == null || ship == null) {
                continue;
            }
            VBox card = buildUpcomingCard(tour.getTourName(), tour.getFrom(), tour.getTo(), inst.getStartDate(), inst.getEndDate(), ship.getShipName());
            upcomingContainer.getChildren().add(card);
        }
        if (upcomingContainer.getChildren().isEmpty()) {
            Label none = new Label("Upcoming trip details unavailable.");
            none.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 13px;");
            upcomingContainer.getChildren().add(none);
        }
    }

    private VBox buildUpcomingCard(String tourName, String from, String to, LocalDate startDate, LocalDate endDate, String shipName) {
        VBox card = new VBox();
        card.setSpacing(8);
        card.getStyleClass().add("trip-card");

        HBox header = new HBox(8);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label title = new Label(tourName);
        title.getStyleClass().add("trip-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        Label status = new Label("Upcoming");
        status.getStyleClass().addAll("status-badge", "status-pending");
        header.getChildren().addAll(title, spacer, status);

        Label route = new Label(from + " → " + to);
        route.getStyleClass().add("trip-subtitle");

        Label departLabel = new Label("Depart: " + startDate);
        departLabel.getStyleClass().add("trip-date");

        Label returnLabel = new Label("Return: " + endDate);
        returnLabel.getStyleClass().add("trip-date");

        Label shipLabel = new Label("Ship: " + shipName);
        shipLabel.getStyleClass().add("trip-date");

        card.getChildren().addAll(header, route, departLabel, returnLabel, shipLabel);
        return card;
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
        card.setStyle("-fx-border-color: #E5E7EB; -fx-border-width: 1; -fx-border-radius: 16; -fx-background-radius: 16; -fx-padding: 16; -fx-background-color: white; -fx-spacing: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);");
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
        
        Label price = new Label("৳" + (tour.getDuration() * 100));
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/passenger/profile.fxml"));
        Parent profileView = loader.load();
        if (heroSection != null) {
            heroSection.setVisible(false);
            heroSection.setManaged(false);
        }
        centerVBox.getChildren().setAll(profileView);
        if (contentScroll != null) {
            contentScroll.setStyle("-fx-background-color: transparent;");
        }
    }

    @FXML
    private void onHomeClick() {
        fromField.clear();
        toField.clear();
        datePicker.setValue(null);
        tourCardsContainer.getChildren().clear();
        allTourInstances = new java.util.ArrayList<>();
        currentPage = 0;
        resultsBox.setVisible(false);
        resultsBox.setManaged(false);
        tourCardsContainer.getChildren().clear();
        tourCardsContainer.setVisible(false);
        tourCardsContainer.setManaged(false);
        prevButton.setVisible(false);
        prevButton.setManaged(false);
        nextButton.setVisible(false);
        nextButton.setManaged(false);
        if (heroSection != null) {
            heroSection.setVisible(true);
            heroSection.setManaged(true);
        }
        if (homeContentBackup != null) {
            centerVBox.getChildren().setAll(homeContentBackup);
        }
        if (contentScroll != null) {
            contentScroll.setStyle("-fx-background-color: #F5F7FA;");
        }
    }

    @FXML
    private void onBookingsClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/passenger/show-passenger-booking.fxml"));
        Parent bookingsView = loader.load();
        if (heroSection != null) {
            heroSection.setVisible(false);
            heroSection.setManaged(false);
        }
        centerVBox.getChildren().setAll(bookingsView);
        try {
            ShowBookingController controller = loader.getController();
            controller.setPassengerId(Session.loggedInUser.getUserID());
        } catch (Exception ignored) { }
        if (contentScroll != null) {
            contentScroll.setStyle("-fx-background-color: transparent;");
        }
    }

    @FXML
    private void onSupportClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/passenger/support.fxml"));
        Parent supportView = loader.load();
        if (heroSection != null) {
            heroSection.setVisible(false);
            heroSection.setManaged(false);
        }
        centerVBox.getChildren().setAll(supportView);
        if (contentScroll != null) {
            contentScroll.setStyle("-fx-background-color: transparent;");
        }
    }

    private void loadFeaturedPhotos() {
        if (featuredContainer == null) return;
        featuredContainer.getChildren().clear();
        var photos = org.example.shipvoyage.dao.PhotoDAO.getFeaturedPhotos(6);
        if (photos.isEmpty()) {
            Label none = new Label("No photos yet.");
            none.setStyle("-fx-text-fill: #6B7280;");
            featuredContainer.getChildren().add(none);
            return;
        }
        for (var p : photos) {
            featuredContainer.getChildren().add(buildPhotoCard(p));
        }
    }

    private VBox buildPhotoCard(org.example.shipvoyage.model.FeaturedPhoto photo) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14; -fx-border-radius: 14; -fx-border-color: #E5E7EB; -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);");
        card.setPrefWidth(260);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(240);
        imageView.setFitHeight(140);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        if (photo.getImagePath() != null && !photo.getImagePath().isBlank()) {
            try {
                imageView.setImage(new Image("file:" + photo.getImagePath(), 240, 160, true, true));
            } catch (Exception ignored) {
                // fallback below
            }
        }
        if (imageView.getImage() == null) {
            imageView.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 12; -fx-alignment: center;");
            imageView.setFitHeight(120);
        }

        Label title = new Label(photo.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #1F2937; -fx-font-size: 14px;");
        Label desc = new Label(photo.getDescription());
        desc.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");
        desc.setWrapText(true);

        card.getChildren().addAll(imageView, title, desc);
        return card;
    }

    @FXML
    private void onViewAllUpcoming(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/shipvoyage/passenger/upcoming-tours.fxml"));
        Parent upcomingView = loader.load();
        if (heroSection != null) {
            heroSection.setVisible(false);
            heroSection.setManaged(false);
        }
        centerVBox.getChildren().setAll(upcomingView);
        if (contentScroll != null) {
            contentScroll.setStyle("-fx-background-color: transparent;");
        }
    }
}
