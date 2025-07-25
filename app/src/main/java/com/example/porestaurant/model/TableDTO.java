package com.example.porestaurant.model;

import com.google.gson.annotations.SerializedName;

public class TableDTO {
    @SerializedName("tableId")
    private int tableId;

    @SerializedName("tableNumber")
    private String tableNumber;

    @SerializedName("capacity")
    private Integer capacity;

    @SerializedName("status")
    private String status;

    @SerializedName("checkin")
    private String checkin;

    @SerializedName("updatedAt")
    private String updatedAt;

    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }

    public String getTableNumber() { return tableNumber != null ? tableNumber : "Unknown"; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }

    public Integer getCapacity() { return capacity != null ? capacity : 0; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getStatus() { return status != null ? status.toUpperCase() : "UNKNOWN"; }
    public void setStatus(String status) { this.status = status; }

    public String getCheckin() { return checkin != null ? checkin : ""; }
    public void setCheckin(String checkin) { this.checkin = checkin; }

    public String getUpdatedAt() { return updatedAt != null ? updatedAt : ""; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "TableDTO{tableId=" + tableId + ", tableNumber='" + getTableNumber() + "', capacity=" + getCapacity() +
                ", status='" + getStatus() + "', checkin='" + getCheckin() + "', updatedAt='" + getUpdatedAt() + "'}";
    }
}