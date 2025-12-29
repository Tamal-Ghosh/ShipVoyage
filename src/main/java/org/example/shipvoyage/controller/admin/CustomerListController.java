package org.example.shipvoyage.controller.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.example.shipvoyage.dao.BookingDAO;
import org.example.shipvoyage.dao.TourDAO;
import org.example.shipvoyage.dao.TourInstanceDAO;
import org.example.shipvoyage.dao.UserDAO;
import org.example.shipvoyage.model.Booking;
import org.example.shipvoyage.model.TourInstance;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class CustomerListController {

    @FXML private ComboBox<TourInstance> instanceCombo;
    @FXML private TextField searchField;
    @FXML private TableView<Booking> customersTable;
    @FXML private TableColumn<Booking, String> colName;
    @FXML private TableColumn<Booking, String> colEmail;
    @FXML private TableColumn<Booking, String> colPhone;
    @FXML private TableColumn<Booking, String> colInstance;
    @FXML private TableColumn<Booking, String> colPayment;
    @FXML private TableColumn<Booking, Void> colActions;

    private ObservableList<Booking> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupInstanceCombo();
        setupTableColumns();
        loadAllBookings();
    }

    private void setupInstanceCombo() {
        List<TourInstance> instances = TourInstanceDAO.getAllTourInstances();
        instanceCombo.setItems(FXCollections.observableArrayList(instances));
        instanceCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(TourInstance ti) {
                if (ti == null) return "All Instances";
                var tour = TourDAO.getTourById(ti.getTourId());
                String name = tour != null ? tour.getTourName() : ("Instance #" + ti.getId());
                return name + " (" + ti.getStartDate() + " → " + ti.getEndDate() + ")";
            }
            @Override public TourInstance fromString(String s) { return null; }
        });
        instanceCombo.setPromptText("All Instances");
        instanceCombo.valueProperty().addListener((obs, old, val) -> { loadAllBookings(); filterTable(); });
    }

    private void setupTableColumns() {
        colName.setCellValueFactory(new PropertyValueFactory<>("passengerName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("passengerEmail"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("passengerPhone"));
        colInstance.setCellValueFactory(cell -> {
            Booking b = cell.getValue();
            TourInstance ti = TourInstanceDAO.getTourInstanceById(b.getTourInstanceId());
            var tour = ti != null ? TourDAO.getTourById(ti.getTourId()) : null;
            String name = tour != null ? tour.getTourName() : (ti != null ? ("Instance #"+ti.getId()) : "-");
            return new javafx.beans.property.SimpleStringProperty(name);
        });
        colPayment.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        colActions.setCellFactory(tc -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn = new Button("Delete");
            private final HBox box = new HBox(8, editBtn, delBtn);
            {
                editBtn.setOnAction(e -> {
                    Booking b = getTableView().getItems().get(getIndex());
                    openEditDialog(b);
                });
                delBtn.setStyle("-fx-text-fill:#DC2626;");
                delBtn.setOnAction(e -> {
                    Booking b = getTableView().getItems().get(getIndex());
                    boolean ok = BookingDAO.cancelBookingByInstanceAndPassenger(b.getTourInstanceId(), b.getPassengerId());
                    if (ok) {
                        masterData.remove(b);
                        filterTable();
                    } else {
                        showInfo("Failed to delete booking.");
                    }
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void loadAllBookings() {
        masterData.clear();
        TourInstance selected = instanceCombo.getValue();
        if (selected == null) {
            var users = UserDAO.getAllPassengers();
            for (var u : users) {
                Booking pseudo = new Booking(0, 0, u.getUserID(), new java.util.ArrayList<>(), new java.util.ArrayList<>(), 0, "", "", "-");
                pseudo.setPassengerName(u.getUsername());
                pseudo.setPassengerEmail(u.getEmail());
                pseudo.setPassengerPhone(u.getPhoneNumber());
                masterData.add(pseudo);
            }
        } else {
            masterData.addAll(BookingDAO.getBookingsByTourInstance(selected.getId()));
        }
        customersTable.setItems(FXCollections.observableArrayList(masterData));
    }

    @FXML
    private void onSearch(ActionEvent e) {
        filterTable();
    }

    private void filterTable() {
        String q = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        TourInstance selected = instanceCombo.getValue();
        List<Booking> filtered = masterData.stream()
                .filter(b -> selected == null || b.getTourInstanceId() == selected.getId())
                .filter(b -> q.isEmpty() ||
                        (b.getPassengerName() != null && b.getPassengerName().toLowerCase().contains(q)) ||
                        (b.getPassengerPhone() != null && b.getPassengerPhone().toLowerCase().contains(q)))
                .collect(Collectors.toList());
        customersTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private void openEditDialog(Booking b) {
        TextInputDialog emailDlg = new TextInputDialog(b.getPassengerEmail());
        emailDlg.setHeaderText("Update Email for " + b.getPassengerName());
        emailDlg.setContentText("Email:");
        var emailRes = emailDlg.showAndWait();
        if (emailRes.isPresent()) {
            boolean ok1 = UserDAO.updateUserEmail(b.getPassengerId(), emailRes.get());
            if (!ok1) { showInfo("Failed to update email."); return; }
        }
        TextInputDialog phoneDlg = new TextInputDialog(b.getPassengerPhone());
        phoneDlg.setHeaderText("Update Mobile for " + b.getPassengerName());
        phoneDlg.setContentText("Mobile:");
        var phoneRes = phoneDlg.showAndWait();
        if (phoneRes.isPresent()) {
            boolean ok2 = UserDAO.updatePhoneNumber(b.getPassengerId(), phoneRes.get());
            if (!ok2) { showInfo("Failed to update mobile."); return; }
        }
        loadAllBookings();
        filterTable();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }
}
