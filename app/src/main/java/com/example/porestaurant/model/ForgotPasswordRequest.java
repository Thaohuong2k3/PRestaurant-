package com.example.porestaurant.model;

public class ForgotPasswordRequest {
    private String email;

    public ForgotPasswordRequest(String email) {
        this.email = email;
    }

    // Getter + Setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
