package com.example.porestaurant.network;

import com.example.porestaurant.model.Menu;
import com.example.porestaurant.model.Category;
import com.example.porestaurant.model.TableDTO;
import com.example.porestaurant.model.User;
import com.example.porestaurant.model.LoginRequest;
import com.example.porestaurant.model.GoogleLoginRequest;
import com.example.porestaurant.model.UpdateUserRequest;
import com.example.porestaurant.model.ForgotPasswordRequest;
import com.example.porestaurant.model.TableBookingRequest;
import com.example.porestaurant.model.TableStatusUpdateRequest;
import com.example.porestaurant.model.Admin;
import com.example.porestaurant.model.DirectionsResponse;
import com.example.porestaurant.model.VerifyOtpRequest;


import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

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

    // @POST("users/forgotpassapp/verify-otp")
    // Call<MessageResponse> verifyOtp(@Body VerifyOtpRequest request);
    @POST("users/forgotpassapp/request-otp")
    Call<ResponseBody> requestOtp(@Body ForgotPasswordRequest request);

    @POST("users/forgotpassapp/verify-otp")
    Call<ResponseBody> verifyOtp(@Body VerifyOtpRequest request);

    @Multipart
    @POST("menu")
    Call<Menu> createMenu(
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("price") RequestBody price,
            @Part("categoryId") RequestBody categoryId,
            @Part("isAvailable") RequestBody isAvailable,
            @Part MultipartBody.Part image);

    // ====== MENU ENDPOINTS ======
    @GET("Menu")
    Call<List<Menu>> getAllMenus();

    @GET("Menu/{id}")
    Call<Menu> getMenuById(@Path("id") int id);

    @DELETE("Menu/{id}")
    Call<Void> deleteMenu(@Path("id") int id);

    @Multipart
    @PUT("menu/{id}")
    Call<Menu> updateMenu(
            @Path("id") int id,
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("price") RequestBody price,
            @Part("categoryId") RequestBody categoryId,
            @Part("isAvailable") RequestBody isAvailable,
            @Part MultipartBody.Part image);

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

    // ==== TABLE BOOKING ====

    @GET("tables/available")
    Call<List<TableDTO>> getAvailableTables();

    @POST("tables/book")
    Call<TableDTO> bookTable(@Body TableBookingRequest request);

    @PUT("tables/{id}/status")
    Call<TableDTO> updateTableStatus(@Path("id") int id, @Body TableStatusUpdateRequest request);

    @PUT("tables/{id}/cancel")
    Call<TableDTO> cancelTable(@Path("id") int id);

    // ====== MAP ENDPOINTS ======
    @GET("maps/api/directions/json")
    Call<DirectionsResponse> getDirections(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("key") String apiKey);

    // --- Admin Statistical Endpoints ---
    @GET("admin/statistical/RevenueByMonth/{year}")
    Call<List<Admin.RevenueByMonthDto>> getRevenueByMonth(@Path("year") int year);

    @GET("admin/statistical/OrderCountByMonth/{year}")
    Call<List<Admin.OrderCountByMonthDto>> getOrderCountByMonth(@Path("year") int year);

    @GET("admin/statistical/TopMenuItems")
    Call<List<Admin.TopMenuItemDto>> getTopMenuItems(
            @Query("topN") int topN,
            @Query("from") String from, // "yyyy-MM-dd"
            @Query("to") String to);

    @GET("admin/statistical/TableOccupancy")
    Call<List<Admin.TableOccupancyDto>> getTableOccupancy(@Query("date") String date);

    @GET("admin/statistical/PaymentMethods")
    Call<List<Admin.PaymentMethodDto>> getPaymentMethods(
            @Query("from") String from,
            @Query("to") String to);

    @GET("admin/statistical/CustomerCountByMonth/{year}")
    Call<List<Admin.CustomerCountByMonthDto>> getCustomerCountByMonth(@Path("year") int year);

    @GET("admin/statistical/MenuStats/{year}")
    Call<Admin.MenuStatsDto> getMenuStats(@Path("year") int year);
}
