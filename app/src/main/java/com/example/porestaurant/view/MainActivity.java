package com.example.porestaurant.view;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.example.porestaurant.R;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            // Chuyển sang LoginActivity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }
}
