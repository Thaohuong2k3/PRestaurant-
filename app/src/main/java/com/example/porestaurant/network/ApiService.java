package com.example.porestaurant.network;

import com.example.porestaurant.model.LoginRequest;
import com.example.porestaurant.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("users/login")
    Call<User> login(@Body LoginRequest request);
}
