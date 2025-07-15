package com.example.porestaurant.network;

import com.example.porestaurant.model.GoogleLoginRequest;
import com.example.porestaurant.model.LoginRequest;
import com.example.porestaurant.model.UpdateUserRequest;
import com.example.porestaurant.model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("users/login")
    Call<User> login(@Body LoginRequest request);
    @POST("users/google-login")
    Call<User> loginWithGoogle(@Body GoogleLoginRequest request);
    @POST("users/register")
    Call<User> register(@Body User user);
    @PUT("users/{id}")
    Call<ResponseBody> updateUser(
            @Path("id") int userId,
            @Body UpdateUserRequest request
    );
}
