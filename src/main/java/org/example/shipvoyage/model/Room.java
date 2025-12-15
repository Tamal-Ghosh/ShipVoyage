package org.example.shipvoyage.model;

public class Room {
    private int id;
    private int shipId;
    private String roomNumber;
    private String roomType;
    private double pricePerNight;

    public Room(int id, int shipId, String roomNumber, String roomType, double pricePerNight) {
        this.id = id;
        this.shipId = shipId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getShipId() { return shipId; }
    public void setShipId(int shipId) { this.shipId = shipId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }
}
