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
import com.example.porestaurant.network.ApiClient;
import com.example.porestaurant.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.porestaurant.model.CartStorage;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;
    private Button btnCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        recyclerView = findViewById(R.id.recyclerMenu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Menu>> call = apiService.getAllMenus();

        call.enqueue(new Callback<List<Menu>>() {
            @Override
            public void onResponse(Call<List<Menu>> call, Response<List<Menu>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Menu> menuList = response.body();

                    menuAdapter = new MenuAdapter(MenuActivity.this, menuList, menu -> {
                        CartStorage.addToCart(MenuActivity.this, menu);
                        updateCartCount();
                        Toast.makeText(MenuActivity.this, "Added to cart: " + menu.getName(), Toast.LENGTH_SHORT).show();
                        updateCartCount(); // update UI
                    });

                    recyclerView.setAdapter(menuAdapter);
                } else {
                    Toast.makeText(MenuActivity.this, "Failed to load menu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Menu>> call, Throwable t) {
                Toast.makeText(MenuActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("MenuActivity", "onFailure: ", t);
            }
        });
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

