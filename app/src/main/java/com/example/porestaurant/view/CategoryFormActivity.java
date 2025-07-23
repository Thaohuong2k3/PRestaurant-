package com.example.porestaurant.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.porestaurant.R;
import com.example.porestaurant.model.Category;
import com.example.porestaurant.network.ApiClient;
import com.example.porestaurant.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFormActivity extends AppCompatActivity {
    private EditText edtName, edtDesc;
    private Button btnSave;
    private ApiService api;
    private boolean isEdit = false;
    private int editingId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_form);

        edtName = findViewById(R.id.edtName);
        edtDesc = findViewById(R.id.edtDesc);
        btnSave = findViewById(R.id.btnSave);

        api = ApiClient.getClient().create(ApiService.class);

        Intent it = getIntent();
        if (it.hasExtra("id")) {
            // Đang sửa: lấy id, name, desc
            isEdit = true;
            editingId = it.getIntExtra("id", 0);
            edtName.setText(it.getStringExtra("name"));
            edtDesc.setText(it.getStringExtra("desc"));
            setTitle("Sửa danh mục");
        } else {
            setTitle("Thêm danh mục");
        }

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String desc = edtDesc.getText().toString().trim();
            if (name.isEmpty()) {
                edtName.setError("Bắt buộc");
                return;
            }
            if (isEdit) updateCategory(editingId, name, desc);
            else       createCategory(name, desc);
        });
    }

    private void createCategory(String name, String desc) {
        Category c = new Category();
        c.setCategoryName(name);
        c.setDescription(desc);

        api.createCategory(c).enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> resp) {
                if (resp.isSuccessful()) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CategoryFormActivity.this,
                            "Tạo thất bại: " + resp.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Toast.makeText(CategoryFormActivity.this,
                        "Lỗi: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCategory(int id, String name, String desc) {
        Category c = new Category();
        c.setCategoryName(name);
        c.setDescription(desc);

        api.updateCategory(id, c).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> resp) {
                if (resp.isSuccessful()) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CategoryFormActivity.this,
                            "Cập nhật thất bại: " + resp.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CategoryFormActivity.this,
                        "Lỗi: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
