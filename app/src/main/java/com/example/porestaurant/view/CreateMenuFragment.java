package com.example.porestaurant.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class CreateMenuFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etDescription, etPrice;
    private Spinner spinnerCategory;
    private CheckBox checkIsAvailable;
    private ImageView imgPreview;
    private Button btnChooseImage, btnSave, btnCancel;

    private byte[] imageData;
    private String imageMimeType;
    private int[] categoryIds;

    private final MenuRepository menuRepository = new MenuRepository();
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etName = view.findViewById(R.id.etName);
        etDescription = view.findViewById(R.id.etDescription);
        etPrice = view.findViewById(R.id.etPrice);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        checkIsAvailable = view.findViewById(R.id.checkIsAvailable);
        imgPreview = view.findViewById(R.id.imgPreview);
        btnChooseImage = view.findViewById(R.id.btnChooseImage);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);
        loadCategories();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        imgPreview.setImageURI(selectedImage);
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImage);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            imageData = stream.toByteArray();
                            imageMimeType = requireContext().getContentResolver().getType(selectedImage);
                            if (imageMimeType == null) {
                                imageMimeType = "image/jpeg";
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        btnChooseImage.setOnClickListener(v -> openImageChooser());
        btnSave.setOnClickListener(v -> saveMenu());
        btnCancel.setOnClickListener(v -> {
            if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                requireActivity().onBackPressed();
            }
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
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                } else {
                    Toast.makeText(requireContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void saveMenu() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceText = etPrice.getText().toString().trim();
        boolean isAvailable = checkIsAvailable.isChecked();

        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || imageData == null) {
            Toast.makeText(requireContext(), "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid price", Toast.LENGTH_SHORT).show();
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
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Menu created successfully", Toast.LENGTH_SHORT).show();
                    if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        requireActivity().onBackPressed();
                    }
                });
            }

            @Override
            public void onError(String message) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(
                        () -> Toast.makeText(requireContext(), "Error: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }
} 