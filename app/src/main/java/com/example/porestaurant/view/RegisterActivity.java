package com.example.porestaurant.view;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.porestaurant.R;
import com.example.porestaurant.model.User;
import com.example.porestaurant.repository.UserRepository;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtFullName, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        userRepository = new UserRepository();

        btnRegister.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        String fullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString();
        String confirmPass = edtConfirmPassword.getText().toString();

        if(fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(confirmPass)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(password);
        user.setConfirmPass(confirmPass);

        userRepository.register(user, new UserRepository.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    // Chuyển về LoginActivity hoặc MainActivity tùy ý bạn
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                });
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Đăng ký lỗi: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }
}