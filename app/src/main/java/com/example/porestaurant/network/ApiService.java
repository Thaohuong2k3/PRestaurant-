package com.example.porestaurant.network;

import com.example.porestaurant.model.ForgotPasswordRequest;
import com.example.porestaurant.model.GoogleLoginRequest;
import com.example.porestaurant.model.LoginRequest;
import com.example.porestaurant.model.MessageResponse;
import com.example.porestaurant.model.UpdateUserRequest;
import com.example.porestaurant.model.User;
import com.example.porestaurant.model.Menu;
import com.example.porestaurant.model.Category;
import com.example.porestaurant.model.TableDTO;
import com.example.porestaurant.model.TableBookingRequest;
import com.example.porestaurant.model.TableStatusUpdateRequest;

import java.util.List;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // LOGIN
    @POST("users/login")
    Call<User> login(@Body LoginRequest request);

    @POST("users/google-login")
    Call<User> loginWithGoogle(@Body GoogleLoginRequest request);

    @POST("users/register")
    Call<User> register(@Body User user);

    @PUT("users/{id}")
    Call<ResponseBody> updateUser(
            @Path("id") int userId,
            @Body UpdateUserRequest request);

    @POST("users/forgot-password")
    Call<ResponseBody> forgotPassword(@Body String email);

    // ====== MENU ENDPOINTS ======
    @GET("Menu")
    Call<List<Menu>> getAllMenus();

    @GET("Menu/{id}")
    Call<Menu> getMenuById(@Path("id") int id);

    @Multipart
    @POST("menu")
    Call<Menu> createMenu(
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("price") RequestBody price,
            @Part("categoryId") RequestBody categoryId,
            @Part("isAvailable") RequestBody isAvailable,
            @Part MultipartBody.Part image
    );

    @PUT("Menu/{id}")
    Call<Void> updateMenu(@Path("id") int id, @Body Menu menu);

    @DELETE("Menu/{id}")
    Call<Void> deleteMenu(@Path("id") int id);

    // ====== CATEGORY ENDPOINTS ======
    @GET("Category")
    Call<List<Category>> getAllCategories();

    @GET("Category/{id}")
    Call<Category> getCategoryById(@Path("id") int id);

    @POST("Category")
    Call<Category> createCategory(@Body Category category);

    @PUT("Category/{id}")
    Call<Void> updateCategory(@Path("id") int id, @Body Category category);

    @DELETE("Category/{id}")
    Call<Void> deleteCategory(@Path("id") int id);

    @GET("tables/available")
    Call<List<TableDTO>> getAvailableTables();

    @POST("tables/book")
    Call<TableDTO> bookTable(@Body TableBookingRequest request);

    @PUT("tables/{id}/status")
    Call<TableDTO> updateTableStatus(@Path("id") int id, @Body TableStatusUpdateRequest request);

    @PUT("tables/{id}/cancel")
    Call<TableDTO> cancelTable(@Path("id") int id);
}
