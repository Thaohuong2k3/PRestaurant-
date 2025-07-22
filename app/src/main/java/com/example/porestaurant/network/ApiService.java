package com.example.porestaurant.network;

import com.example.porestaurant.model.LoginRequest;
import com.example.porestaurant.model.User;

import com.example.porestaurant.model.Admin.MenuDTO;
import com.example.porestaurant.model.Admin.TableDTO;
import com.example.porestaurant.model.Admin.TableBookingRequest;
import com.example.porestaurant.model.Admin.TableStatusUpdateRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Multipart;
import retrofit2.http.POST;


import com.example.porestaurant.model.Admin;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface ApiService {
    @POST("users/login")
    Call<User> login(@Body LoginRequest request);
    // --- Admin Statistical Endpoints ---
    @GET("admin/statistical/RevenueByMonth/{year}")
    Call<List<Admin.RevenueByMonthDto>>
    getRevenueByMonth(@Path("year") int year);

    @GET("admin/statistical/OrderCountByMonth/{year}")
    Call<List<Admin.OrderCountByMonthDto>>
    getOrderCountByMonth(@Path("year") int year);

    @GET("admin/statistical/TopMenuItems")
    Call<List<Admin.TopMenuItemDto>> getTopMenuItems(
            @Query("topN") int topN,
            @Query("from") String from,   // "yyyy-MM-dd"
            @Query("to")   String to
    );

    @GET("admin/statistical/TableOccupancy")
    Call<List<Admin.TableOccupancyDto>>
    getTableOccupancy(@Query("date") String date);

    @GET("admin/statistical/PaymentMethods")
    Call<List<Admin.PaymentMethodDto>> getPaymentMethods(
            @Query("from") String from,
            @Query("to")   String to
    );

    @GET("admin/statistical/CustomerCountByMonth/{year}")
    Call<List<Admin.CustomerCountByMonthDto>>
    getCustomerCountByMonth(@Path("year") int year);

    @GET("admin/statistical/MenuStats/{year}")
    Call<Admin.MenuStatsDto>
    getMenuStats(@Path("year") int year);
    // Menu CRUD
    @GET("Menu")
    Call<List<MenuDTO>> getMenus();

    @GET("Menu/{id}")
    Call<MenuDTO> getMenuById(@Path("id") int id);

    @Multipart
    @POST("Menu")
    Call<MenuDTO> createMenu(
            @Part("Name") RequestBody name,
            @Part("Price") RequestBody price,
            @Part MultipartBody.Part imageFile  // tên phải khớp với tham số action của API
    );

    @Multipart
    @PUT("Menu/{id}")
    Call<Void> updateMenu(
            @Path("id") int id,
            @Part("Name") RequestBody name,
            @Part("Price") RequestBody price,
            @Part MultipartBody.Part imageFile
    );

    @DELETE("Menu/{id}")
    Call<Void> deleteMenu(@Path("id") int id);
    // Table management
    @GET("Tables")
    Call<List<TableDTO>> getAllTables();

    @GET("Tables/available")
    Call<List<TableDTO>> getAvailableTables();

    @POST("Tables/book")
    Call<TableDTO> bookTable(@Body TableBookingRequest req);

    @PUT("Tables/{id}/status")
    Call<TableDTO> updateTableStatus(
            @Path("id") int id,
            @Body TableStatusUpdateRequest req
    );

    @PUT("Tables/{id}/cancel")
    Call<TableDTO> cancelTable(@Path("id") int id);

}
