package com.example.porestaurant.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.porestaurant.R;
import com.example.porestaurant.model.Category;
import com.example.porestaurant.model.Menu;
import com.example.porestaurant.network.ApiClient;
import com.example.porestaurant.network.ApiService;
import com.example.porestaurant.repository.MenuRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateMenuActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etDescription, etPrice;
    private Spinner spinnerCategory;
    private CheckBox checkIsAvailable;
    private ImageView imgPreview;
    private Button btnChooseImage, btnSave;

    private byte[] imageData;
    private String imageMimeType;

    private int[] categoryIds;

    private final MenuRepository menuRepository = new MenuRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_menu);

        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        checkIsAvailable = findViewById(R.id.checkIsAvailable);
        imgPreview = findViewById(R.id.imgPreview);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);
        loadCategories();

        btnChooseImage.setOnClickListener(v -> openImageChooser());

        btnSave.setOnClickListener(v -> saveMenu());

        btnCancel.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadCategories() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    String[] categoryNames = new String[categories.size()];
                    categoryIds = new int[categories.size()];

                    for (int i = 0; i < categories.size(); i++) {
                        categoryNames[i] = categories.get(i).getCategoryName();
                        categoryIds[i] = categories.get(i).getCategoryId();
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            CreateMenuActivity.this,
                            android.R.layout.simple_spinner_item,
                            categoryNames
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                } else {
                    Toast.makeText(CreateMenuActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(CreateMenuActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imgPreview.setImageURI(selectedImage);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                imageData = stream.toByteArray();

                String path = getRealPath(this, selectedImage);
                imageMimeType = getContentResolver().getType(selectedImage);
                if (imageMimeType == null) {
                    imageMimeType = "image/jpeg"; // fallback
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveMenu() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceText = etPrice.getText().toString().trim();
        boolean isAvailable = checkIsAvailable.isChecked();

        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || imageData == null) {
            Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
            return;
        }

        int categoryId = categoryIds[spinnerCategory.getSelectedItemPosition()];

        Menu menu = new Menu();
        menu.setName(name);
        menu.setDescription(description);
        menu.setPrice(price);
        menu.setAvailable(isAvailable);
        menu.setCategoryId(categoryId);

        menuRepository.createMenu(menu, imageData, imageMimeType, new MenuRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(CreateMenuActivity.this, "Menu created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(CreateMenuActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    public static String getRealPath(Context context, Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }
}
