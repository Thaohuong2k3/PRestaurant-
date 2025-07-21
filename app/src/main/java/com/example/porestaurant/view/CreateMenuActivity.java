package com.example.porestaurant.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.*;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.example.porestaurant.R;
import com.example.porestaurant.model.Menu;
import com.example.porestaurant.network.ApiClient;
import com.example.porestaurant.network.ApiService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateMenuActivity extends Activity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etDescription, etPrice;
    private Spinner spinnerCategory;
    private CheckBox checkIsAvailable;
    private ImageView imgPreview;
    private byte[] imageData = null;
    private String imageMimeType = "";

    private String[] categories = {"Food", "Drink", "Dessert"};
    private int[] categoryIds = {1, 2, 3}; // You can load this from API if needed

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
        Button btnChooseImage = findViewById(R.id.btnChooseImage);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);


        btnCancel.setOnClickListener(v -> {
            finish();
        });

        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnSave.setOnClickListener(v -> saveMenu());
    }

    private void saveMenu() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceText = etPrice.getText().toString().trim();
        boolean isAvailable = checkIsAvailable.isChecked();

        if (name.isEmpty() || priceText.isEmpty() || imageData == null || imageMimeType == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceText);
        int selectedCategoryId = categoryIds[spinnerCategory.getSelectedItemPosition()];

        Menu menu = new Menu();
        menu.setName(name);
        menu.setDescription(description);
        menu.setPrice(price);
        menu.setCategoryId(selectedCategoryId);
        menu.setAvailable(isAvailable);
        menu.setImageData(imageData);
        menu.setImageMimeType(imageMimeType);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Menu> call = apiService.createMenu(menu);

        call.enqueue(new Callback<Menu>() {
            @Override
            public void onResponse(Call<Menu> call, Response<Menu> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateMenuActivity.this, "Menu created successfully", Toast.LENGTH_SHORT).show();
                    finish(); // go back
                } else {
                    Toast.makeText(CreateMenuActivity.this, "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Menu> call, Throwable t) {
                Toast.makeText(CreateMenuActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Show preview using Glide (on UI thread)
            Glide.with(this)
                    .load(imageUri)
                    .override(512, 512) // Optional: downscale for preview only
                    .centerCrop()
                    .into(imgPreview);

            // Load actual byte[] for upload in background
            new Thread(() -> {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    Bitmap resized = Bitmap.createScaledBitmap(bitmap, 512, 512, true); // Optional downscale
                    imageData = bitmapToByteArray(resized);
                    imageMimeType = getContentResolver().getType(imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(this, "Image load failed", Toast.LENGTH_SHORT).show());
                }
            }).start();
        }
    }


    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // you can change to JPEG
        return stream.toByteArray();
    }
}