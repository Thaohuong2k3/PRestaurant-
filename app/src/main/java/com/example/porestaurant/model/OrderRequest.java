package com.example.porestaurant.model;

import com.google.gson.annotations.SerializedName;

public class OrderRequest {
    @SerializedName("userId")
    private int userId;

    @SerializedName("tableId")
    private int tableId;

    @SerializedName("status")
    private String status;

    @SerializedName("checkin")
    private String checkin;

    @SerializedName("updatedAt")
    private String updatedAt;

    public OrderRequest(int userId, int tableId, String status, String checkin, String updatedAt) {
        this.userId = userId;
        this.tableId = tableId;
        this.status = status;
        this.checkin = checkin;
        this.updatedAt = updatedAt;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCheckin() { return checkin; }
    public void setCheckin(String checkin) { this.checkin = checkin; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}