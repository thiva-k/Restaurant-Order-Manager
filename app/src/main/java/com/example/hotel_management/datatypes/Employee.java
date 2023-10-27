package com.example.hotel_management.datatypes;

public class Employee {
    private String name;
    private String email;
    private String userType;
    private String userID;

    public Employee(String name, String email, String userType, String userID) {
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUserType() {
        return userType;
    }
    public String getUserID() {
        return userID;
    }
}

