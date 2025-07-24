package com.example.porestaurant.model;

public class GoogleLoginRequest {
    private String email;
    private String fullName;
    private String password;
    private String idToken;

    public GoogleLoginRequest(String email, String password, String idToken, String fullName) {
        this.email = email;
        this.password = password;
        this.idToken = idToken;
        this.fullName = fullName;
    }

    public String getIdToken() { return idToken; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
}
