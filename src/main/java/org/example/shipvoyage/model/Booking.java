package org.example.shipvoyage.model;

import java.util.List;

public class Booking {
    private int id;
    private int tourInstanceId;
    private int passengerId;
    private List<Integer> roomIds;       // List of booked room IDs
    private List<String> roomNumbers;    // Optional: store room numbers for easy display
    private double totalPrice;
    private String status;               // e.g., "Booked", "Cancelled", "Pending"

    public Booking(int id, int tourInstanceId, int passengerId,
                   List<Integer> roomIds, List<String> roomNumbers,
                   double totalPrice, String status) {
        this.id = id;
        this.tourInstanceId = tourInstanceId;
        this.passengerId = passengerId;
        this.roomIds = roomIds;
        this.roomNumbers = roomNumbers;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public int getId() { return id; }
    public int getTourInstanceId() { return tourInstanceId; }
    public int getPassengerId() { return passengerId; }
    public List<Integer> getRoomIds() { return roomIds; }
    public List<String> getRoomNumbers() { return roomNumbers; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }

    public void setRoomIds(List<Integer> roomIds) { this.roomIds = roomIds; }
    public void setRoomNumbers(List<String> roomNumbers) { this.roomNumbers = roomNumbers; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setStatus(String status) { this.status = status; }
}
