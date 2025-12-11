package org.example.shipvoyage.model;

public class Room {
    private int roomID;
    private int tourID;
    private String roomNumber;
    private String status;

    public Room(int roomID, int tourID, String roomNumber, String status) {
        this.roomID = roomID;
        this.tourID = tourID;
        this.roomNumber = roomNumber;
        this.status = status;
    }



    public int getRoomID() { return roomID; }
    public void setRoomID(int roomID) { this.roomID = roomID; }

    public int getTourID() { return tourID; }
    public void setTourID(int tourID) { this.tourID = tourID; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
