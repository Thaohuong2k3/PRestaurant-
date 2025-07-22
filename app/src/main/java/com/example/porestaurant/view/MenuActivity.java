package com.example.porestaurant.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.porestaurant.R;
import com.example.porestaurant.model.Menu;
import com.example.porestaurant.model.CartStorage;
import com.example.porestaurant.repository.MenuRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMenu;
    private RecyclerView recyclerViewCategory;
    private MenuAdapter menuAdapter;
    private CategoryAdapter categoryAdapter;
    private Button btnCart;

    private List<Menu> allMenus = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        recyclerViewMenu = findViewById(R.id.recyclerMenu);
        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewCategory = findViewById(R.id.recyclerCategory);
        recyclerViewCategory.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        Button btnCreateMenu = findViewById(R.id.btnCreateMenu);
        btnCart = findViewById(R.id.btnCart);

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, CartActivity.class);
            startActivity(intent);
        });

        btnCreateMenu.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, CreateMenuActivity.class);
            startActivity(intent);
        });

        loadMenus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMenus();
        updateCartCount(); // update when returning
    }

    private void loadMenus() {
        MenuRepository repository = new MenuRepository();

        repository.getAllMenus(new MenuRepository.MenuCallback() {
            @Override
            public void onSuccess(List<Menu> menuList) {
                runOnUiThread(() -> {
                    allMenus = menuList;

                    // Setup menu list
                    menuAdapter = new MenuAdapter(MenuActivity.this, menuList, menu -> {
                        CartStorage.addToCart(MenuActivity.this, menu);
                        updateCartCount();
                        Toast.makeText(MenuActivity.this, "Added to cart: " + menu.getName(), Toast.LENGTH_SHORT).show();
                    });
                    recyclerViewMenu.setAdapter(menuAdapter);

                    // Setup category filter
                    setupCategoryFilter(menuList);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(MenuActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("MenuActivity", "loadMenus: " + errorMessage);
                });
            }
        });
    }

    private void setupCategoryFilter(List<Menu> menuList) {
        Set<String> categorySet = new LinkedHashSet<>();
        categorySet.add("All"); // Default
        for (Menu menu : menuList) {
            if (menu.getCategoryName() != null) {
                categorySet.add(menu.getCategoryName());
            }
        }

        List<String> categories = new ArrayList<>(categorySet);

        categoryAdapter = new CategoryAdapter(categories, selectedCategory -> {
            if ("All".equals(selectedCategory)) {
                menuAdapter.updateList(allMenus);
            } else {
                List<Menu> filtered = new ArrayList<>();
                for (Menu m : allMenus) {
                    if (selectedCategory.equals(m.getCategoryName())) {
                        filtered.add(m);
                    }
                }
                menuAdapter.updateList(filtered);
            }
        });

        recyclerViewCategory.setAdapter(categoryAdapter);
    }

    private void updateCartCount() {
        List<Menu> cart = CartStorage.getCart(this);
        int count = 0;
        for (Menu item : cart) {
            count += item.getQuantity();
        }
        btnCart.setText("Cart (" + count + ")");
    }
}
