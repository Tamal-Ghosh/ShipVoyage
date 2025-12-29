package org.example.shipvoyage.model;

public class Tour {
   private int id;
   private String tourName;
   private String from;
   private String to;
   private int duration;
   private String description;

    public Tour(int id, String tourName, String from,String to, int duration, String description) {
         this.id = id;
         this.tourName = tourName;
         this.from = from;
         this.to = to;
         this.duration = duration;
         this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTourName() { return tourName; }
    public void setTourName(String tourName) { this.tourName = tourName;}

    public  String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public  String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }





}
