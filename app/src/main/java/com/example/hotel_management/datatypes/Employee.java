package com.example.hotel_management.datatypes;

public class Employee {
    private String name;
    private String email;
    private String userType;

    public Employee(String name, String email, String userType) {
        this.name = name;
        this.email = email;
        this.userType = userType;
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
}
