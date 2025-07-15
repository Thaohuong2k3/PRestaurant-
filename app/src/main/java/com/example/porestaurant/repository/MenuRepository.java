package com.example.porestaurant.repository;

import com.example.porestaurant.model.Menu;
import com.example.porestaurant.network.ApiClient;
import com.example.porestaurant.network.ApiService;

import java.util.List;

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
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load menus.");
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

    public void createMenu(Menu menu, final SimpleCallback callback) {
        apiService.createMenu(menu).enqueue(new Callback<Menu>() {
            @Override
            public void onResponse(Call<Menu> call, Response<Menu> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Create failed.");
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
