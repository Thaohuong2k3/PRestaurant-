package com.example.porestaurant.model;

import com.google.gson.annotations.SerializedName;

public class TableBookingRequest {
    @SerializedName("tableId")
    private int tableId;

    @SerializedName("checkin")
    private String checkin;

    public TableBookingRequest(int tableId, String checkin) {
        this.tableId = tableId;
        this.checkin = checkin;
    }

    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }
    public String getCheckin() { return checkin; }
    public void setCheckin(String checkin) { this.checkin = checkin; }
}