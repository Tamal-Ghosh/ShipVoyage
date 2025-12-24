package org.example.shipvoyage.model;

public class Booking {
    private int id;
    private int tourInstanceId;
    private int roomId;
    private String roomNumber;
    private int passengerId;

    public Booking(int id, int tourInstanceId, int roomId, String roomNumber, int passengerId) {
        this.id = id;
        this.tourInstanceId = tourInstanceId;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.passengerId = passengerId;
    }

    public int getId() { return id; }
    public int getTourInstanceId() { return tourInstanceId; }
    public int getRoomId() { return roomId; }
    public String getRoomNumber() { return roomNumber; }
    public int getPassengerId() { return passengerId; }
}
