package com.example.porestaurant.ui.theme;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.porestaurant.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminDashboardActivity extends AppCompatActivity {
    private int selectedYear = 2025;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // --- Spinner chọn năm ---
        Spinner spinnerYear = findViewById(R.id.spinnerYear);
        String[] years = {"2023","2024","2025","2026"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                years
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(adapter);
        spinnerYear.setSelection(2); // mặc định 2025
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedYear = Integer.parseInt(years[pos]);
                // reload fragment với năm mới
                reloadCurrentFragment();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        // --- BottomNavigation ---
        BottomNavigationView bottomNav = findViewById(R.id.admin_bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Fragment frag = null;
            int id = item.getItemId();
            if ( id == R.id.nav_stats) {
                frag = new CombinedStatsFragment();
            } else if (id == R.id.nav_manage_menu) {
                frag = new MenuManagementFragment();
            } else if (id == R.id.nav_manage_table) {
                frag = new TableManagementFragment();
            }else if (id == R.id.nav_menu_stats) {
                frag = new TopMenuItemsFragment();
            }
            if (frag != null) {
                Bundle args = new Bundle();
                args.putInt("year", selectedYear);
                frag.setArguments(args);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.admin_fragment_container, frag)
                        .commit();
                return true;
            }
            return false;
        });
        // Chọn launcher item mặc định và load nó
        bottomNav.setSelectedItemId(R.id.nav_stats);
    }

    private void reloadCurrentFragment() {
        // gọi lại như khi click nav để truyền lại year mới
        BottomNavigationView nav = findViewById(R.id.admin_bottom_nav);
        nav.setSelectedItemId(nav.getSelectedItemId());
    }
}
