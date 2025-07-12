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
