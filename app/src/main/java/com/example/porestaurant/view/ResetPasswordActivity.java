package com.example.porestaurant.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.porestaurant.R;
import android.widget.ImageView;
import com.example.porestaurant.model.MessageResponse;
import com.example.porestaurant.repository.ForgotPasswordRepository;
import com.example.porestaurant.network.ApiClient;
import com.example.porestaurant.network.ApiService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText edtEmail, edtOtp, edtNewPassword;
    private Button btnResetPassword;
    private ForgotPasswordRepository forgotPasswordRepository;
    private TextView tvLogin;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Ánh xạ view
        edtEmail = findViewById(R.id.edtEmail);
        edtOtp = findViewById(R.id.edtOtp);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvLogin = findViewById(R.id.tvLogin);
        btnBack = findViewById(R.id.btnBack);

        // Lấy email từ Intent nếu có (từ màn ForgotPasswordActivity gửi sang)
        String email = getIntent().getStringExtra("email");
        if (email != null && !email.isEmpty()) {
            edtEmail.setText(email);
        }

        // Khởi tạo ApiService và Repository
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        forgotPasswordRepository = new ForgotPasswordRepository(apiService);

        btnResetPassword.setOnClickListener(v -> doVerifyOtp());

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        btnBack.setOnClickListener(v -> {
            onBackPressed(); // hoặc finish();
        });
        // Back icon (nếu có trên layout)
        // findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void doVerifyOtp() {
        String email = edtEmail.getText().toString().trim();
        String otp = edtOtp.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();

        if (email.isEmpty() || otp.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        forgotPasswordRepository.verifyOtp(email, otp, newPassword, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String msg = response.body().string();
                            Toast.makeText(ResetPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(ResetPasswordActivity.this, "Lỗi đọc response", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "OTP sai hoặc đã hết hạn!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                runOnUiThread(() ->
                        Toast.makeText(ResetPasswordActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
