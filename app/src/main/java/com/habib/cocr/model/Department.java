package com.habib.cocr.model;

public class Department {
    private String departmentId;
    private String name;
    private String acronym;
    private String institutionId;
    private String chairmanId;

    // Constructor
    public Department(String departmentId, String name, String acronym, String institutionId, Institution institution) {
        this.departmentId = departmentId;
        this.name = name;
        this.acronym = acronym;
        this.institutionId = institutionId;
    }

    // Getters and Setters
    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
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

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

}
