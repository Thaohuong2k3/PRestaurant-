package com.example.porestaurant.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity{

    public static final String PREFS_NAME = "LOGIN_PREF";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        setupNavigationView();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("961582604276-brc2l9efh7al4emaqrhce029h02ib3n7.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if (getIntent().getBooleanExtra("logout", false)) {
            clearUserSessionAndLogout();
            return; // Exit early so it doesn’t load fragment again
        }

        updateNavigationMenu();

        // Load initial fragment (Menu)
        loadFragment(new MenuFragment());


    }

    public void updateNavigationMenu() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.contains("email"); // or "userId"

        menu.findItem(R.id.nav_login).setVisible(!isLoggedIn);
        menu.findItem(R.id.nav_logout).setVisible(isLoggedIn);
        menu.findItem(R.id.nav_profile).setVisible(isLoggedIn);
        menu.findItem(R.id.nav_cart).setVisible(isLoggedIn);
        menu.findItem(R.id.nav_table).setVisible(isLoggedIn);
    }

    private void logOut() {
        // Show login fragment when not logged in
        loadFragment(new LoginFragment());
    }

    public void clearUserSessionAndLogout() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            mGoogleSignInClient.revokeAccess().addOnCompleteListener(this, revokeTask -> {
                SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.apply();

                Toast.makeText(this, "Logout Successfully!", Toast.LENGTH_SHORT).show();
                updateNavigationMenu();
                logOut();
            });
        });
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
                } else if (itemId == R.id.nav_login){
                    loadFragment(new LoginFragment());
                }

                // Table can be handled later
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}