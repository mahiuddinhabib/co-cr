package com.habib.cocr.model;

public class Institution {
    private String institutionId;
    private String name;
    private String acronym;
    private String location;
    private String website;

    // Constructor
    public Institution(String institutionId, String name, String acronym, String location, String website) {
        this.institutionId = institutionId;
        this.name = name;
        this.acronym = acronym;
        this.location = location;
        this.website = website;
    }

    // Getters and Setters
    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
