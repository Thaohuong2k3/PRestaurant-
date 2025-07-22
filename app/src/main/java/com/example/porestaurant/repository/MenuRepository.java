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
                    // decode ảnh Base64 nếu có
                    for (Menu menu : menuList) {
                        String base64 = menu.getImageDataBase64();
                        if (base64 != null && !base64.isEmpty()) {
                            try {
                                byte[] decoded = Base64.decode(base64, Base64.DEFAULT);
                                menu.setImageData(decoded);
                            } catch (IllegalArgumentException e) {
                                callback.onError("Không giải mã được ảnh: " + e.getMessage());
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
                    callback.onError("Menu not found. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Menu> call, Throwable t) {
                callback.onError("Connection error: " + t.getMessage());
            }
        });
    }

    public void createMenu(Menu menu, byte[] imageData, String imageMimeType, final SimpleCallback callback) {
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), menu.getName());
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), menu.getDescription());
        RequestBody price = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(menu.getPrice()));
        RequestBody categoryId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(menu.getCategoryId()));
        RequestBody isAvailable = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(menu.isAvailable()));

        MultipartBody.Part imagePart;
        if (imageData != null && imageData.length > 0) {
            RequestBody file = RequestBody.create(MediaType.parse(imageMimeType), imageData);
            imagePart = MultipartBody.Part.createFormData("imageFile", "image.jpg", file);
        } else {
            // gửi phần rỗng nếu không có ảnh để tránh lỗi server
            RequestBody empty = RequestBody.create(MediaType.parse("application/octet-stream"), new byte[0]);
            imagePart = MultipartBody.Part.createFormData("imageFile", "", empty);
        }

        apiService.createMenu(name, description, price, categoryId, isAvailable, imagePart)
                  .enqueue(new Callback<Menu>() {
            @Override
            public void onResponse(Call<Menu> call, Response<Menu> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Create failed. Code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Menu> call, Throwable t) {
                callback.onError("Connection error: " + t.getMessage());
            }
        });
    }

    public void updateMenu(int id,
                           Menu m,
                           byte[] imgData,
                           String mimeType,
                           final SimpleCallback callback) {
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), m.getName());
        RequestBody desc = RequestBody.create(MediaType.parse("text/plain"), m.getDescription());
        RequestBody price = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(m.getPrice()));
        RequestBody catId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(m.getCategoryId()));
        RequestBody avail = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(m.isAvailable()));

        // Biên dịch phần image
        MultipartBody.Part imagePart;
        if (imgData != null && imgData.length > 0 && mimeType != null) {
            RequestBody file = RequestBody.create(MediaType.parse(mimeType), imgData);
            imagePart = MultipartBody.Part.createFormData("imageFile", "upload.jpg", file);
        } else {
            RequestBody empty = RequestBody.create(MediaType.parse("application/octet-stream"), new byte[0]);
            imagePart = MultipartBody.Part.createFormData("imageFile", "", empty);
        }

        apiService.updateMenu(id, name, desc, price, catId, avail, imagePart)
                .enqueue(new Callback<Menu>() {
                    @Override
                    public void onResponse(Call<Menu> call, Response<Menu> response) {
                        if (response.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            callback.onError("Update failed. Code: " + response.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<Menu> call, Throwable t) {
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
                    callback.onError("Delete failed. Code: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Connection error: " + t.getMessage());
            }
        });
    }

    // Các interface callback
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
