package com.example.hotel_management.datatypes;

// FoodItem.java
public class FoodItem {
    private String name;
    private String description;
    private Integer price;


    private String documentId;
    private String type;

    public FoodItem(String name, String description, Integer price, String documentId, String type) {
        this.name = name;
        this.description = description;
        this.price=price;
        this.documentId = documentId;
        this.type = type;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    //getter methods for price
    public Integer getPrice() {
        return price;
    }
    public String getType() {
        return type;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    //getters and setters for category

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setType(String type) {
        this.type = type;
    }
}
