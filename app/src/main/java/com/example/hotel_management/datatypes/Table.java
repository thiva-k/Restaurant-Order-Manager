package com.example.hotel_management.datatypes;

public class Table {
    public Integer tableID;
    public String status;
    public String lastSessionID;

    public Table(Integer tableID, String status, String lastSessionID) {
        this.tableID = tableID;
        this.status = status;
        this.lastSessionID = lastSessionID;
    }
}
