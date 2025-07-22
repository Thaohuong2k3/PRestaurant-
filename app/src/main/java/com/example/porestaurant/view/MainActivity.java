package com.example.porestaurant.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.porestaurant.R;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements ToolbarFragment.ToolbarListener{

    public static final String PREFS_NAME = "userPrefs";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        checkUserSession();

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Load initial fragment (Menu)
        loadFragment(new MenuFragment());
        setupNavigationView();
    }

    private void checkUserSession() {
        SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (!pref.contains("user")) {
            logOut();
        }
    }

    private void logOut() {
        // Show login fragment when not logged in
        loadFragment(new LoginFragment());
    }

    private void clearUserSessionAndLogout() {
        SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("user");
        editor.apply();

        Toast.makeText(this, "Logout Successfully!", Toast.LENGTH_SHORT).show();
        logOut();
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_profile) {
                    loadFragment(new ProfileFragment());
                } else if (itemId == R.id.nav_cart) {
                    loadFragment(new CartFragment());
                } else if (itemId == R.id.nav_menu) {
                    loadFragment(new MenuFragment());
                } else if (itemId == R.id.nav_logout) {
                    clearUserSessionAndLogout();
                }
                // Table can be handled later
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onSearchClicked() {

    }

    @Override
    public void onFilterClicked() {

    }
}