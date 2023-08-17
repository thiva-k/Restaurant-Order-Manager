package com.example.hotel_management;

// FoodItem.java
public class FoodItem {
    private String name;
    private String description;
    private Integer price;

    public FoodItem(String name, String description, Integer price) {
        this.name = name;
        this.description = description;
        this.price=price;
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
}
