package org.example.shipvoyage.controller.admin;

import org.example.shipvoyage.dao.BookingDAO;
import org.example.shipvoyage.dao.TourDAO;
import org.example.shipvoyage.dao.TourInstanceDAO;
import org.example.shipvoyage.model.Booking;
import org.example.shipvoyage.model.TourInstance;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private TableColumn<Booking, String> phoneColumn;

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
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("passengerPhone"));
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumbersAsString"));
        totalPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        bookingTable.setItems(bookingList);

        ObservableList<TourInstance> instances = FXCollections.observableArrayList(TourInstanceDAO.getAllTourInstances());
        tourInstanceComboBox.setItems(instances);

        tourInstanceComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(TourInstance item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(TourDAO.getTourById(item.getTourId()).getTourName() + " (Start: " + item.getStartDate() + ")");
                }
            }
        });

        tourInstanceComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(TourInstance item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(TourDAO.getTourById(item.getTourId()).getTourName() + " (Start: " + item.getStartDate() + ")");
                }
            }
        });

        tourInstanceComboBox.setOnAction(e -> loadBookings());

        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Cancel");

            {
                btn.setOnAction(e -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    BookingDAO.cancelBookingByInstanceAndPassenger(
                            booking.getTourInstanceId(),
                            booking.getPassengerId()
                    );
                    loadBookings();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void loadBookings() {
        TourInstance instance = tourInstanceComboBox.getValue();
        if (instance == null) {
            bookingList.clear();
            return;
        }
        bookingList.setAll(
                BookingDAO.getBookingsByTourInstance(instance.getId())
        );
    }
}
