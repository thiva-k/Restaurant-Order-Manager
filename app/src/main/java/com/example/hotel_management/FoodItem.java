package com.example.hotel_management;

// FoodItem.java
public class FoodItem {
    private String name;
    private String description;
    private Integer price;
    private String documentId;
    private String category;

    public FoodItem(String name, String description, Integer price, String documentId, String category) {
        this.name = name;
        this.description = description;
        this.price=price;
        this.documentId = documentId;
        this.category = category;
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

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    //getters and setters for category
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
}
