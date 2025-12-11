package org.example.shipvoyage.model;

public class Tour {
    private int tourID;
    private String name;
    private String description;
    private double price;
    private int duration;

    public Tour(int tourID, String name, String description, double price, int duration) {
        this.tourID = tourID;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
    }



    public int getTourID() { return tourID; }
    public void setTourID(int tourID) { this.tourID = tourID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
}
