package com.example.porestaurant.repository;
import com.example.porestaurant.model.LoginRequest;
import com.example.porestaurant.model.User;
import com.example.porestaurant.network.ApiClient;
import com.example.porestaurant.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class UserRepository {
    private ApiService apiService;

    public UserRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public void login(LoginRequest request, final LoginCallback callback) {
        apiService.login(request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Sai tài khoản hoặc mật khẩu");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public interface LoginCallback {
        void onSuccess(User user);
        void onError(String error);
    }
}
