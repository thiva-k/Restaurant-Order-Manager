package com.example.hotel_management.datatypes;

public class Table {
    private Integer tableID;
    private String status;
    private String lastSessionID;

    public Table(Integer tableID, String status, String lastSessionID) {
        this.tableID = tableID;
        this.status = status;
        this.lastSessionID = lastSessionID;
    }

    public Integer getTableID() {
        return tableID;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getLastSessionID() {
        return lastSessionID;
    }
}
