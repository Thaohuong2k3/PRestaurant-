package com.example.porestaurant.repository;

import com.example.porestaurant.model.ForgotPasswordRequest;
import com.example.porestaurant.model.VerifyOtpRequest;
import com.example.porestaurant.model.MessageResponse;
import com.example.porestaurant.network.ApiService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordRepository {

    private final ApiService apiService;

    public ForgotPasswordRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public void requestOtp(String email, Callback<ResponseBody> callback) {
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);
        apiService.requestOtp(request).enqueue(callback);
    }

    public void verifyOtp(String email, String otp, String newPassword, Callback<ResponseBody> callback) {
        VerifyOtpRequest request = new VerifyOtpRequest(email, otp, newPassword);
        apiService.verifyOtp(request).enqueue(callback);
    }
}