package org.example.shipvoyage.model;

public class Room {
    private int roomID;
    private int shipID;
    private String roomNumber;
    private String status;
    private String type;
    private int price;


    public Room(int roomID, int shipID, String roomNumber, String status, String type, int price) {
        this.roomID = roomID;
        this.shipID = shipID;
        this.roomNumber = roomNumber;
        this.status = status;
        this.type = type;
        this.price = price;
    }


    public Room(int shipID, String roomNumber, String type, int price) {
        this.shipID = shipID;
        this.roomNumber = roomNumber;
        this.status = "AVAILABLE";
        this.type = type;
        this.price = price;
    }


    public int getRoomID() { return roomID; }
    public void setRoomID(int roomID) { this.roomID = roomID; }

    public int getShipID() { return shipID; }
    public void setShipID(int shipID) { this.shipID = shipID; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    @Override
    public String toString() {
        return "Room{" +
                "roomID=" + roomID +
                ", shipID=" + shipID +
                ", roomNumber='" + roomNumber + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                '}';
    }
}
