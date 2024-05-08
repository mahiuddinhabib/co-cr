package com.habib.cocr.model;

public class Venue {
    private String venueId;
    private String name;

    public Venue(String venueId, String name) {
        this.venueId = venueId;
        this.name = name;
    }

    public Venue() {

    }

    public String getVenueId() {
        return venueId;
    }

    public String getName() {
        return name;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public void setName(String name) {
        this.name = name;
    }
}
