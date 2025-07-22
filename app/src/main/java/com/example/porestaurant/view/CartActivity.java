package com.example.porestaurant.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.porestaurant.R;
import com.example.porestaurant.model.CartStorage;
import com.example.porestaurant.model.Menu;

import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerCart;
    private TextView txtTotal;
    private CartAdapter cartAdapter;
    private List<Menu> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerCart = findViewById(R.id.recyclerCart);
        txtTotal = findViewById(R.id.txtTotal);
        Button btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(v -> finish());

        cartItems = CartStorage.getCart(this);

        cartAdapter = new CartAdapter(this, cartItems, this::updateTotal);
        recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerCart.setAdapter(cartAdapter);

        updateTotal();
    }

    public void updateTotal() {
        double total = 0;
        for (Menu item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        txtTotal.setText(String.format("Total: $%.2f", total));
    }
}
