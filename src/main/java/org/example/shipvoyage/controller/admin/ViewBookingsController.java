package org.example.shipvoyage.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.shipvoyage.dao.BookingDAO;
import org.example.shipvoyage.dao.TourInstanceDAO;
import org.example.shipvoyage.model.Booking;
import org.example.shipvoyage.model.TourInstance;

public class ViewBookingsController {

    @FXML
    private ComboBox<TourInstance> tourInstanceComboBox;

    @FXML
    private TableView<Booking> bookingTable;

    @FXML
    private TableColumn<Booking, Integer> idColumn;

    @FXML
    private TableColumn<Booking, String> nameColumn;

    @FXML
    private TableColumn<Booking, String> emailColumn;

    @FXML
    private TableColumn<Booking, String> tourDateColumn;

    @FXML
    private TableColumn<Booking, String> roomColumn;

    @FXML
    private TableColumn<Booking, Double> totalPaymentColumn;

    @FXML
    private TableColumn<Booking, String> statusColumn;

    @FXML
    private TableColumn<Booking, Void> actionColumn;

    private final ObservableList<Booking> bookingList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("passengerName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("passengerEmail"));
        tourDateColumn.setCellValueFactory(new PropertyValueFactory<>("tourDate"));
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumbersAsString"));
        totalPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        bookingTable.setItems(bookingList);

        tourInstanceComboBox.setItems(
                FXCollections.observableArrayList(TourInstanceDAO.getAllTourInstances())
        );

        tourInstanceComboBox.setOnAction(e -> loadBookings());
    }

    private void loadBookings() {
        TourInstance instance = tourInstanceComboBox.getValue();
        if (instance == null) {
            bookingList.clear();
            return;
        }
        bookingList.setAll(
                BookingDAO.getBookingsByShipAndTour(instance.getShipId(), instance.getId())
        );
    }
}
