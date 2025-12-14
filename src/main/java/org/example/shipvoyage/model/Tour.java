package org.example.shipvoyage.model;

public class Tour {
   private int id;
   private String tourName;
   private String route;
   private int duration;
   private String description;

    public Tour(int id, String tourName, String route, int duration, String description) {
         this.id = id;
         this.tourName = tourName;
         this.route = route;
         this.duration = duration;
         this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTourName() { return tourName; }
    public void setTourName(String tourName) { this.tourName = tourName;}

    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }



}
