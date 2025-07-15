package com.example.porestaurant.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.porestaurant.R;
import com.example.porestaurant.model.Menu;
import com.example.porestaurant.repository.MenuRepository;

import java.util.List;

public class MenuActivity extends AppCompatActivity {

    EditText edtId, edtName, edtDesc, edtPrice, edtCategoryId, edtImage;
    Button btnCreate, btnGetAll, btnGetById, btnUpdate, btnDelete;
    TextView txtResult;
    MenuRepository menuRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialize views
        edtId = findViewById(R.id.edtId);
        edtName = findViewById(R.id.edtName);
        edtDesc = findViewById(R.id.edtDescription);
        edtPrice = findViewById(R.id.edtPrice);
        edtCategoryId = findViewById(R.id.edtCategoryId);
        edtImage = findViewById(R.id.edtImage);
        txtResult = findViewById(R.id.txtResult);

        btnCreate = findViewById(R.id.btnCreate);
        btnGetAll = findViewById(R.id.btnGetAll);
        btnGetById = findViewById(R.id.btnGetById);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        menuRepo = new MenuRepository();

        btnCreate.setOnClickListener(v -> createMenu());
        btnGetAll.setOnClickListener(v -> getAllMenus());
        btnGetById.setOnClickListener(v -> getMenuById());
        btnUpdate.setOnClickListener(v -> updateMenu());
        btnDelete.setOnClickListener(v -> deleteMenu());
    }

    private void createMenu() {
        Menu menu = collectMenuInput();
        if (menu == null) return;

        menuRepo.createMenu(menu, new MenuRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> txtResult.setText("Created"));
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> txtResult.setText(error));
            }
        });
    }

    private void getAllMenus() {
        menuRepo.getAllMenus(new MenuRepository.MenuCallback() {
            @Override
            public void onSuccess(List<Menu> menuList) {
                runOnUiThread(() -> txtResult.setText(menuList.toString()));
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> txtResult.setText(error));
            }
        });
    }

    private void getMenuById() {
        try {
            int id = Integer.parseInt(edtId.getText().toString());
            menuRepo.getMenuById(id, new MenuRepository.SingleMenuCallback() {
                @Override
                public void onSuccess(Menu menu) {
                    runOnUiThread(() -> txtResult.setText(menu.toString()));
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> txtResult.setText(error));
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMenu() {
        try {
            int id = Integer.parseInt(edtId.getText().toString());
            Menu menu = collectMenuInput();
            if (menu == null) return;

            menuRepo.updateMenu(id, menu, new MenuRepository.SimpleCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> txtResult.setText("Updated"));
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> txtResult.setText(error));
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteMenu() {
        try {
            int id = Integer.parseInt(edtId.getText().toString());
            menuRepo.deleteMenu(id, new MenuRepository.SimpleCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> txtResult.setText("Deleted"));
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> txtResult.setText(error));
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid ID", Toast.LENGTH_SHORT).show();
        }
    }

    private Menu collectMenuInput() {
        try {
            String name = edtName.getText().toString();
            String desc = edtDesc.getText().toString();
            double price = Double.parseDouble(edtPrice.getText().toString());
            int categoryId = Integer.parseInt(edtCategoryId.getText().toString());
            String image = edtImage.getText().toString();

            Menu menu = new Menu();
            menu.setName(name);
            menu.setDescription(desc);
            menu.setPrice(price);
            menu.setCategoryId(categoryId);
            menu.setImage(image);

            return menu;
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
