package com.habib.cocr.model;

import java.util.List;

public class Schedule {
    private String day;
    private List<Session> schedule;

    // Getters and Setters
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<Session> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<Session> schedule) {
        this.schedule = schedule;
    }
}