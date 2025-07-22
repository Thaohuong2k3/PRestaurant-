package com.example.porestaurant.repository;

import android.util.Base64;

import com.example.porestaurant.model.Menu;
import com.example.porestaurant.network.ApiClient;
import com.example.porestaurant.network.ApiService;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuRepository {
    private final ApiService apiService;

    public MenuRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public void getAllMenus(final MenuCallback callback) {
        apiService.getAllMenus().enqueue(new Callback<List<Menu>>() {
            @Override
            public void onResponse(Call<List<Menu>> call, Response<List<Menu>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Menu> menuList = response.body();

                    for (Menu menu : menuList) {
                        String base64 = menu.getImageDataBase64();
                        if (base64 != null && !base64.isEmpty()) {
                            try {
                                byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
                                menu.setImageData(decodedBytes);
                            } catch (IllegalArgumentException e) {
                                callback.onError("Failed to decode image for menu: " + e.getMessage());
                                return;
                            }
                        }
                    }

                    callback.onSuccess(menuList);
                } else {
                    callback.onError("Failed to load menus. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Menu>> call, Throwable t) {
                callback.onError("Connection error: " + t.getMessage());
            }
        });
    }

    public void getMenuById(int id, final SingleMenuCallback callback) {
        apiService.getMenuById(id).enqueue(new Callback<Menu>() {
            @Override
            public void onResponse(Call<Menu> call, Response<Menu> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Menu not found.");
                }
            }

            @Override
            public void onFailure(Call<Menu> call, Throwable t) {
                callback.onError("Connection error: " + t.getMessage());
            }
        });
    }

    public void createMenu(Menu menu, byte[] imageData, String imageMimeType, final SimpleCallback callback) {
        // Convert fields to RequestBody
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), menu.getName());
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), menu.getDescription());
        RequestBody price = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(menu.getPrice()));
        RequestBody categoryId = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(menu.getCategoryId()));
        RequestBody isAvailable = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(menu.isAvailable()));

        MultipartBody.Part imagePart;

        if (imageData != null && imageData.length > 0) {
            RequestBody requestFile = RequestBody.create(MediaType.parse(imageMimeType), imageData);
            imagePart = MultipartBody.Part.createFormData("imageFile", "image.jpg", requestFile);
        } else {
            // Still send empty part to avoid server-side null error
            RequestBody emptyFile = RequestBody.create(MediaType.parse("application/octet-stream"), new byte[0]);
            imagePart = MultipartBody.Part.createFormData("imageFile", "", emptyFile);
        }

        // Call API with corrected parameter order
        apiService.createMenu(name, description, price, categoryId, isAvailable, imagePart)
                .enqueue(new Callback<Menu>() {
                    @Override
                    public void onResponse(Call<Menu> call, Response<Menu> response) {
                        if (response.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            callback.onError("Create failed. Status: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Menu> call, Throwable t) {
                        callback.onError("Connection error: " + t.getMessage());
                    }
                });
    }

    public void updateMenu(int id, Menu menu, final SimpleCallback callback) {
        apiService.updateMenu(id, menu).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Update failed.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Connection error: " + t.getMessage());
            }
        });
    }

    public void deleteMenu(int id, final SimpleCallback callback) {
        apiService.deleteMenu(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Delete failed.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Connection error: " + t.getMessage());
            }
        });
    }

    // Callback interfaces
    public interface MenuCallback {
        void onSuccess(List<Menu> menuList);

        void onError(String error);
    }

    public interface SingleMenuCallback {
        void onSuccess(Menu menu);

        void onError(String error);
    }

    public interface SimpleCallback {
        void onSuccess();

        void onError(String error);
    }

}
