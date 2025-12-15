package org.example.shipvoyage.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.shipvoyage.dao.BookingDAO;
import org.example.shipvoyage.dao.ShipDAO;
import org.example.shipvoyage.dao.TourInstanceDAO;
import org.example.shipvoyage.model.Booking;
import org.example.shipvoyage.model.Ship;
import org.example.shipvoyage.model.TourInstance;

import java.util.List;

public class ViewBookingsController {

    public ComboBox<Ship> shipComboBox;
    public ComboBox<TourInstance> tourInstanceComboBox;
    public TableView<Booking> bookingTable;

    public TableColumn<Booking, Integer> idColumn;
    public TableColumn<Booking, String> nameColumn;
    public TableColumn<Booking, String> emailColumn;
    public TableColumn<Booking, String> tourDateColumn;
    public TableColumn<Booking, String> roomColumn;
    public TableColumn<Booking, Double> totalPaymentColumn;
    public TableColumn<Booking, Double> duePaymentColumn;
    public TableColumn<Booking, String> statusColumn;
    public TableColumn<Booking, Void> actionColumn;

    private ObservableList<Booking> bookingList = FXCollections.observableArrayList();

    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        tourDateColumn.setCellValueFactory(new PropertyValueFactory<>("tourDate"));
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        totalPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("totalPayment"));
        duePaymentColumn.setCellValueFactory(new PropertyValueFactory<>("duePayment"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadShips();

        shipComboBox.setOnAction(this::onShipSelected);
        tourInstanceComboBox.setOnAction(event -> loadBookings());
    }

    private void loadShips() {
        List<Ship> ships = ShipDAO.getAllShips();
        shipComboBox.setItems(FXCollections.observableArrayList(ships));
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

    private void onShipSelected(ActionEvent event) {
        Ship ship = shipComboBox.getSelectionModel().getSelectedItem();
        if (ship != null) {
            List<TourInstance> instances = TourInstanceDAO.getTourInstancesByShip(ship.getId());
            tourInstanceComboBox.setItems(FXCollections.observableArrayList(instances));
            tourInstanceComboBox.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(TourInstance item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getStartDate().toString());
                }
            });
            tourInstanceComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(TourInstance item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getStartDate().toString());
                }
            });
        } else {
            tourInstanceComboBox.getItems().clear();
        }
        bookingList.clear();
        bookingTable.setItems(bookingList);
    }

    private void loadBookings() {
        Ship ship = shipComboBox.getSelectionModel().getSelectedItem();
        TourInstance instance = tourInstanceComboBox.getSelectionModel().getSelectedItem();
        if (ship != null && instance != null) {
            bookingList.clear();
            bookingList.addAll(BookingDAO.getBookingsByShipAndTour(ship.getId(), instance.getId()));
            bookingTable.setItems(bookingList);
        }
    }
}
