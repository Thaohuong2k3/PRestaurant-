package com.example.porestaurant.network;

import com.example.porestaurant.model.GoogleLoginRequest;
import com.example.porestaurant.model.LoginRequest;
import com.example.porestaurant.model.UpdateUserRequest;
import com.example.porestaurant.model.User;
import com.example.porestaurant.model.Menu;
import com.example.porestaurant.model.Category;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    // ====== MENU ENDPOINTS ======
    @GET("Menu")
    Call<List<Menu>> getAllMenus();

    @GET("Menu/{id}")
    Call<Menu> getMenuById(@Path("id") int id);

    @POST("Menu")
    Call<Menu> createMenu(@Body Menu menu);

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

}
