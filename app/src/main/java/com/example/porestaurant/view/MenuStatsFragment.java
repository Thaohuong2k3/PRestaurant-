package com.example.porestaurant.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.porestaurant.R;
import com.example.porestaurant.model.Admin;
import com.example.porestaurant.repository.StatisticalRepository;

import java.text.NumberFormat;
import java.util.Locale;

public class MenuStatsFragment extends Fragment {

    private TextView tvTotal, tvAvgPrice, tvMostExp;
    private StatisticalRepository repo;
    private int year = 2025;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_stats, container, false);
    }

    @Override public void onViewCreated(@NonNull View view,
                                        @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tvTotal = view.findViewById(R.id.tvTotalItems);
        tvAvgPrice = view.findViewById(R.id.tvAveragePrice);
        tvMostExp = view.findViewById(R.id.tvMostExpensive);
        repo = new StatisticalRepository();

        // Get year from arguments if available
        if (getArguments() != null) {
            year = getArguments().getInt("year", 2025);
        }

        // Load data
        loadData(year);
    }

    private void loadData(int year) {
        repo.getMenuStats(year, new StatisticalRepository.MenuStatsCallback() {
            @Override
            public void onSuccess(Admin.MenuStatsDto d) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    // Format currency
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);

                    // Update UI
                    tvTotal.setText(String.valueOf(d.getTotalItems()));
                    tvAvgPrice.setText(formatter.format(d.getAveragePrice()));
                    tvMostExp.setText(d.getMostExpensiveItem());
                });
            }

            @Override
            public void onError(String error) {
                Log.e("MenuStats", "Error: " + error);
            }
        });
    }
}
