package com.example.porestaurant.model;

public class User {
    private String email;
    private String password;
    private String confirmPass;
    private int userID;
    private String fullName;
    private String role;
    private boolean isActive;
    private boolean rememberMe;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPass() {
        return confirmPass;
    }

    public void setConfirmPass(String confirmPass) {
        this.confirmPass = confirmPass;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    // Thêm biến static CREATE_TABLE cho lệnh SQL
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS User (" +
                    "userID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "fullName TEXT," +
                    "password TEXT," +
                    "email TEXT," +
                    "role TEXT," +
                    "isActive INTEGER," +
                    "rememberMe INTEGER" +
                    ")";
}
