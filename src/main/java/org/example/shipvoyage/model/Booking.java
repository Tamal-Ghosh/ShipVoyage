package org.example.shipvoyage.model;

import java.util.List;

public class Booking {
    private int id;
    private int tourInstanceId;
    private int passengerId;
    private String passengerName;
    private String passengerEmail;
    private List<Integer> roomIds;
    private List<String> roomNumbers;
    private double totalPrice;
    private String status;
    private String paymentMethod;
    private String paymentStatus;

    public Booking(int id, int tourInstanceId, int passengerId,
                   List<Integer> roomIds, List<String> roomNumbers,
                   double totalPrice, String status,
                   String paymentMethod, String paymentStatus) {
        this.id = id;
        this.tourInstanceId = tourInstanceId;
        this.passengerId = passengerId;
        this.roomIds = roomIds;
        this.roomNumbers = roomNumbers;
        this.totalPrice = totalPrice;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }

    public int getId() { return id; }
    public int getTourInstanceId() { return tourInstanceId; }
    public int getPassengerId() { return passengerId; }
    public List<Integer> getRoomIds() { return roomIds; }
    public List<String> getRoomNumbers() { return roomNumbers; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }

    public void setRoomIds(List<Integer> roomIds) { this.roomIds = roomIds; }
    public void setRoomNumbers(List<String> roomNumbers) { this.roomNumbers = roomNumbers; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setStatus(String status) { this.status = status; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getRoomNumbersAsString() {
        return String.join(", ", roomNumbers);
    }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }
    public String getPassengerName() { return passengerName; }
    public String getPassengerEmail() { return passengerEmail; }


}
