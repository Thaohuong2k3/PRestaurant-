package com.example.porestaurant.model;

public class UpdateUserRequest {
    private int userId;
    private String fullName;
    private String newPassword;

    public UpdateUserRequest(int userId, String fullName, String newPassword) {
        this.userId = userId;
        this.fullName = fullName;
        this.newPassword = newPassword;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    // Getter/setter nếu cần
}

