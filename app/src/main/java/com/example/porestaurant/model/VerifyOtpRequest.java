package com.example.porestaurant.model;

public class VerifyOtpRequest {
    private String email;
    private String otp;
    private String newPassword;

    public VerifyOtpRequest(String email, String otp, String newPassword) {
        this.email = email;
        this.otp = otp;
        this.newPassword = newPassword;
    }
    public String getEmail() { return email; }
    public String getOtp() { return otp; }
    public String getNewPassword() { return newPassword; }
}
