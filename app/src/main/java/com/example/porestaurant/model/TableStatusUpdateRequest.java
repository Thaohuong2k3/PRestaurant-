package com.example.porestaurant.model;

import com.google.gson.annotations.SerializedName;

public class TableStatusUpdateRequest {
    @SerializedName("newStatus")
    private String newStatus;

    public TableStatusUpdateRequest(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
}