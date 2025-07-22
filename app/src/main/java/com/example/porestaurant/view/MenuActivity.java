package com.example.porestaurant.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.porestaurant.R;
import com.example.porestaurant.model.Menu;
import com.example.porestaurant.model.CartStorage;
import com.example.porestaurant.repository.CategoryRepository;
import com.example.porestaurant.repository.MenuRepository;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMenu;
    private RecyclerView recyclerViewCategory;
    private MenuAdapter menuAdapter;
    private CategoryAdapter categoryAdapter;
    private Button btnCart;
    private SearchView searchViewMenu;

    private List<Menu> allMenus = new ArrayList<>();
    private String selectedCategory = "All"; // Track selected category

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

        searchViewMenu = findViewById(R.id.searchViewMenu);
        searchViewMenu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Not using submit
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMenus();
                return true;
            }
        });

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

                    // Load categories from API
                    loadCategoriesFromApi();
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

    private void loadCategoriesFromApi() {
        CategoryRepository categoryRepository = new CategoryRepository();
        categoryRepository.getAllCategories(new CategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(List<com.example.porestaurant.model.Category> categoryList) {
                runOnUiThread(() -> {
                    List<String> categories = new ArrayList<>();
                    categories.add("All"); // default

                    for (com.example.porestaurant.model.Category cat : categoryList) {
                        if (cat.getCategoryName() != null) {
                            categories.add(cat.getCategoryName());
                        }
                    }

                    categoryAdapter = new CategoryAdapter(categories, selected -> {
                        selectedCategory = selected;
                        filterMenus();
                    });

                    recyclerViewCategory.setAdapter(categoryAdapter);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(MenuActivity.this, "Category load failed: " + errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void filterMenus() {
        String searchQuery = searchViewMenu.getQuery().toString().toLowerCase().trim();
        List<Menu> filtered = new ArrayList<>();

        for (Menu m : allMenus) {
            boolean matchesCategory = selectedCategory.equals("All") ||
                    (m.getCategoryName() != null && selectedCategory.equals(m.getCategoryName()));
            boolean matchesSearch = m.getName().toLowerCase().contains(searchQuery);

            if (matchesCategory && matchesSearch) {
                filtered.add(m);
            }
        }

        menuAdapter.updateList(filtered);
    }

    private void updateCartCount() {
        List<Menu> cart = CartStorage.getCart(this);
        int count = 0;
        for (Menu item : cart) {
            count += item.getQuantity();
        }
        btnCart.setText("Cart (" + count + ")");
    }

    public void updateList(List<Menu> newList) {
        allMenus = newList;
        menuAdapter.notifyDataSetChanged();
    }
}
