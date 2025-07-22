package com.example.porestaurant.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.porestaurant.R;
import com.example.porestaurant.model.ForgotPasswordRequest;
import com.example.porestaurant.model.MessageResponse;
import com.example.porestaurant.network.ApiClient;
import com.example.porestaurant.network.ApiService;
import com.example.porestaurant.repository.ForgotPasswordRepository;
import com.example.porestaurant.repository.UserRepository;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText edtEmail;
    private Button btnSendOtp;
    private ImageView btnBack;

    private ForgotPasswordRepository forgotPasswordRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Ánh xạ view
        edtEmail = findViewById(R.id.edtEmail);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnBack = findViewById(R.id.btnBack);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Truyền ApiService vào repository
        forgotPasswordRepository = new ForgotPasswordRepository(apiService);

        btnSendOtp.setOnClickListener(v -> doRequestOtp());
        // back
        TextView tvLogin = findViewById(R.id.tvLogin);
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            finish();
        });
        btnBack.setOnClickListener(v -> {
            onBackPressed(); // hoặc finish();
        });
    }

    private void doRequestOtp() {
        String email = edtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }

        forgotPasswordRepository.requestOtp(email, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String msg = response.body().string();
                            Toast.makeText(ForgotPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();
                            // Chuyển sang ResetPasswordActivity
                            Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(ForgotPasswordActivity.this, "Lỗi đọc response", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Gửi OTP thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                runOnUiThread(() -> Toast
                        .makeText(ForgotPasswordActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT)
                        .show());
            }
        });
    }
}