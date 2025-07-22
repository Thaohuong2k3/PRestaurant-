package com.example.porestaurant.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.porestaurant.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        Button btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MenuActivity.class));
        });

        Button btnGoToBooking = findViewById(R.id.btn_go_to_booking);

        btnGoToBooking.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BookingActivity.class));
        });
    }
}
