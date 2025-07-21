package com.example.porestaurant.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.porestaurant.R;
import com.example.porestaurant.model.ForgotPasswordRequest;
import com.example.porestaurant.repository.UserRepository;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText edtForgotEmail;
    private Button btnForgotSend;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password); // Đúng tên layout của bạn

        edtForgotEmail = findViewById(R.id.edtForgotEmail);
        btnForgotSend = findViewById(R.id.btnForgotSend);

        userRepository = new UserRepository();

        btnForgotSend.setOnClickListener(v -> doForgotPassword());
    }

    private void doForgotPassword() {
        String email = edtForgotEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gửi đúng dạng raw string JSON cho backend: "abc@gmail.com"
        userRepository.forgotPassword(email, new UserRepository.ForgotPasswordCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show());
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(ForgotPasswordActivity.this, error, Toast.LENGTH_LONG).show());
            }
        });
    }
}