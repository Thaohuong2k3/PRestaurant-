package com.example.porestaurant.repository;
import com.example.porestaurant.model.GoogleLoginRequest;
import com.example.porestaurant.model.LoginRequest;
import com.example.porestaurant.model.UpdateUserRequest;
import com.example.porestaurant.model.User;
import com.example.porestaurant.network.ApiClient;
import com.example.porestaurant.network.ApiService;

import okhttp3.ResponseBody;
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
    public void googleLogin(GoogleLoginRequest request, LoginCallback callback) {
        apiService.loginWithGoogle(request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "";
                    try {
                        errorMsg = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                    } catch (Exception e) {
                        errorMsg = response.message();
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    public void register(User user, final LoginCallback callback) {
        apiService.register(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Đăng ký thất bại";
                    try {
                        if (response.errorBody() != null)
                            errorMsg = response.errorBody().string();
                    } catch (Exception e) {}
                    callback.onError(errorMsg);
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    public void updateUser(UpdateUserRequest request, final UpdateCallback callback) {
        apiService.updateUser(request.getUserId(), request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = "Cập nhật thành công!";
                    try {
                        message = response.body().string();
                    } catch (Exception e) {}
                    callback.onSuccess(message);
                } else {
                    String errorMsg = "Cập nhật lỗi";
                    try {
                        if (response.errorBody() != null)
                            errorMsg = response.errorBody().string();
                    } catch (Exception e) {}
                    callback.onError(errorMsg);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public interface UpdateCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public interface LoginCallback {
        void onSuccess(User user);
        void onError(String error);
    }
}
