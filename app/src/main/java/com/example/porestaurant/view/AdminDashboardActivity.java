package com.example.porestaurant.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.porestaurant.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminDashboardActivity extends AppCompatActivity {
    private int selectedYear = 2025;
    private TextView txtCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        setupYearSpinner();
        setupBottomNavigation();
        updateCurrentDate();

        ImageButton btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);
                intent.putExtra("logout", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initViews() {
        txtCurrentDate = findViewById(R.id.txtCurrentDate);
    }

    private void updateCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date()); // Displays 01:01 AM +07, Friday, July 25, 2025
        txtCurrentDate.setText(currentDate);
    }

    private void setupYearSpinner() {
        Spinner spinnerYear = findViewById(R.id.spinnerYear);
        String[] years = {"2023", "2024", "2025", "2026"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.spinner_item_admin,
                years
        ) {
            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                view.setBackgroundResource(R.drawable.spinner_dropdown_background);
                return view;
            }
        };

        spinnerYear.setAdapter(adapter);
        spinnerYear.setSelection(2); // Default 2025

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedYear = Integer.parseInt(years[pos]);
                reloadCurrentFragment();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.admin_bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_stats) {
                fragment = new CombinedStatsFragment();
            } else if (id == R.id.nav_manage_menu) {
                fragment = new MenuManagementFragment();
            } else if (id == R.id.nav_menu_stats) {
                fragment = new TopMenuItemsFragment();
            } else if (id == R.id.nav_manage_table) {
                fragment = new ManageTableFragment();
            } else if (id == R.id.nav_category) {
                fragment = new CategoryListFragment();
            }

            if (fragment != null) {
                Bundle args = new Bundle();
                args.putInt("year", selectedYear);
                fragment.setArguments(args);

                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right
                        )
                        .replace(R.id.admin_fragment_container, fragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Set default selection
        bottomNav.setSelectedItemId(R.id.nav_stats);
    }

    private void reloadCurrentFragment() {
        BottomNavigationView nav = findViewById(R.id.admin_bottom_nav);
        nav.setSelectedItemId(nav.getSelectedItemId());
    }
}