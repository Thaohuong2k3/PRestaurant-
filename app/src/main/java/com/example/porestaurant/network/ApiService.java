package com.example.porestaurant.network;

import com.example.porestaurant.model.LoginRequest;
import com.example.porestaurant.model.User;
import com.example.porestaurant.model.TableDTO;
import com.example.porestaurant.model.TableBookingRequest;
import com.example.porestaurant.model.TableStatusUpdateRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("users/login")
    Call<User> login(@Body LoginRequest request);

    @GET("tables/available")
    Call<List<TableDTO>> getAvailableTables();

    @POST("tables/book")
    Call<TableDTO> bookTable(@Body TableBookingRequest request);

    @PUT("tables/{id}/status")
    Call<TableDTO> updateTableStatus(@Path("id") int id, @Body TableStatusUpdateRequest request);

    @PUT("tables/{id}/cancel")
    Call<TableDTO> cancelTable(@Path("id") int id);
}