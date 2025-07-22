package com.example.porestaurant.model;

import com.google.gson.annotations.SerializedName;

public class RestaurantTable {
    @SerializedName("tableId")
    private int tableId;

    @SerializedName("tableNumber")
    private String tableNumber;

    @SerializedName("capacity")
    private int capacity;

    @SerializedName("status")
    private String status;

    @SerializedName("checkin")
    private String checkin;

    @SerializedName("updatedAt")
    private String updatedAt;

    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }
    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCheckin() { return checkin; }
    public void setCheckin(String checkin) { this.checkin = checkin; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}