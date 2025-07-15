package com.example.porestaurant.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.porestaurant.R;
import com.example.porestaurant.model.UpdateUserRequest;
import com.example.porestaurant.repository.UserRepository;

public class ActivityProfile extends AppCompatActivity {
    private EditText edtProfileFullName, edtProfilePassword, edtProfileConfirmPass;
    private TextView tvProfileEmail;
    private Button btnProfileUpdate, btnProfileLogout;
    private SharedPreferences sharedPreferences;
    private UserRepository userRepository;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edtProfileFullName = findViewById(R.id.edtProfileFullName);
        edtProfilePassword = findViewById(R.id.edtProfilePassword);
        edtProfileConfirmPass = findViewById(R.id.edtProfileConfirmPass);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        btnProfileUpdate = findViewById(R.id.btnProfileUpdate);
        btnProfileLogout = findViewById(R.id.btnProfileLogout);

        userRepository = new UserRepository();
        sharedPreferences = getSharedPreferences("LOGIN_PREF", MODE_PRIVATE);

        // Lấy thông tin từ SharedPreferences (PHẢI lưu đủ ở Login thành công)
        userId = sharedPreferences.getInt("userId", -1);
        String fullName = sharedPreferences.getString("fullName", "");
        String email = sharedPreferences.getString("email", "");

        edtProfileFullName.setText(fullName);
        tvProfileEmail.setText(email);

        btnProfileUpdate.setOnClickListener(v -> doUpdateProfile());
        btnProfileLogout.setOnClickListener(v -> {
            sharedPreferences.edit().clear().apply();
            startActivity(new Intent(ActivityProfile.this, LoginActivity.class));
            finish();
        });
    }

    private void doUpdateProfile() {
        String newFullName = edtProfileFullName.getText().toString().trim();
        String newPassword = edtProfilePassword.getText().toString();
        String confirmPass = edtProfileConfirmPass.getText().toString();

        if (newFullName.isEmpty()) {
            Toast.makeText(this, "Họ tên không được bỏ trống!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPassword.isEmpty() && !newPassword.equals(confirmPass)) {
            Toast.makeText(this, "Xác nhận mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateUserRequest request = new UpdateUserRequest(userId, newFullName, newPassword);

        userRepository.updateUser(request, new UserRepository.UpdateCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    // Lưu lại tên mới nếu thành công
                    sharedPreferences.edit().putString("fullName", newFullName).apply();
                    Toast.makeText(ActivityProfile.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    edtProfilePassword.setText("");
                    edtProfileConfirmPass.setText("");
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(ActivityProfile.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }
}
