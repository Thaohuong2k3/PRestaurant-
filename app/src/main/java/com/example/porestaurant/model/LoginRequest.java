package com.example.porestaurant.model;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
    // Getter, Setter nếu cần
}
