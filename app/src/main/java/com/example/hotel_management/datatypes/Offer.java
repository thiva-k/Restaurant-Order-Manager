package com.example.hotel_management.datatypes;

import java.util.Date;

public class Offer {
    private String name;
    private String startDate;
    private String endDate;
    private int percentage;
    private String description;
    private String offerID;


    public Offer(String name, String startDate, String endDate, int percentage, String description, String offerID) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.percentage = percentage;
        this.description = description;
        this.offerID = offerID;
    }

    // Getter and setter methods for each field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getOfferID() {
        return offerID;
    }
}
