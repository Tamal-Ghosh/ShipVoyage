package org.example.shipvoyage.model;

import java.time.LocalDate;

public class TourInstance {
    private int id;
    private int tourId;
    private int shipId;
    private LocalDate startDate;
    private LocalDate endDate;

    public  TourInstance(int id, int tourId, int shipId, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.tourId = tourId;
        this.shipId = shipId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTourId() { return tourId; }
    public void setTourId(int tourId) { this.tourId = tourId;}

    public int getShipId() { return shipId; }
    public void setShipId(int shipId) { this.shipId = shipId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate;}

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate;}
}
