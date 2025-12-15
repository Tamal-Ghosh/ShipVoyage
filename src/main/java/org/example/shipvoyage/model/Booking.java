package org.example.shipvoyage.model;

public class Booking {
    private int id;
    private String customerName;
    private String email;
    private Ship ship;
    private TourInstance tourInstance;
    private String roomNumber;
    private double totalPayment;
    private double duePayment;
    private String status;

    public Booking() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Ship getShip() { return ship; }
    public void setShip(Ship ship) { this.ship = ship; }

    public TourInstance getTourInstance() { return tourInstance; }
    public void setTourInstance(TourInstance tourInstance) { this.tourInstance = tourInstance; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public double getTotalPayment() { return totalPayment; }
    public void setTotalPayment(double totalPayment) { this.totalPayment = totalPayment; }

    public double getDuePayment() { return duePayment; }
    public void setDuePayment(double duePayment) { this.duePayment = duePayment; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
