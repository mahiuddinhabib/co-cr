package com.habib.cocr.model;

import java.util.Map;

public class Classes {
    private String name;
    private Map<String, CR> CRs;
    private Map<String, Schedule> schedules;
    private Map<String, Notice> notices;
    private Map<String, Event> events;
    private Map<String, Vacation> vacations;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, CR> getCRs() {
        return CRs;
    }

    public void setCRs(Map<String, CR> CRs) {
        this.CRs = CRs;
    }

    public Map<String, Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(Map<String, Schedule> schedules) {
        this.schedules = schedules;
    }

    public Map<String, Notice> getNotices() {
        return notices;
    }

    public void setNotices(Map<String, Notice> notices) {
        this.notices = notices;
    }

    public Map<String, Event> getEvents() {
        return events;
    }

    public void setEvents(Map<String, Event> events) {
        this.events = events;
    }

    public Map<String, Vacation> getVacations() {
        return vacations;
    }

    public void setVacations(Map<String, Vacation> vacations) {
        this.vacations = vacations;
    }
}
