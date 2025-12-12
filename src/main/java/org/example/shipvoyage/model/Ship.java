package org.example.shipvoyage.model;

public class Ship {
    private int id;
    private String shipName;
    private int capacity;
    public Ship(int id, String shipName, int capacity) {
        this.id = id;
        this.shipName = shipName;
        this.capacity = capacity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getShipName() { return shipName; }
    public void setShipName(String shipName) { this.shipName = shipName; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

}

