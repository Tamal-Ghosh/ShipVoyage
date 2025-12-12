package org.example.shipvoyage.model;

public class Passenger {
    private int passengerID;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String paymentInfo;
    private int tourID;
    private int roomID;

    public Passenger(int passengerID, String firstName, String lastName, String contactNumber,
                     String paymentInfo, int tourID, int roomID) {
        this.passengerID = passengerID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactNumber = contactNumber;
        this.paymentInfo = paymentInfo;
        this.tourID = tourID;
        this.roomID = roomID;
    }



    public int getPassengerID() { return passengerID; }
    public void setPassengerID(int passengerID) { this.passengerID = passengerID; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getPaymentInfo() { return paymentInfo; }
    public void setPaymentInfo(String paymentInfo) { this.paymentInfo = paymentInfo; }

    public int getTourID() { return tourID; }
    public void setTourID(int tourID) { this.tourID = tourID; }

    public int getRoomID() { return roomID; }
    public void setRoomID(int roomID) { this.roomID = roomID; }
}
