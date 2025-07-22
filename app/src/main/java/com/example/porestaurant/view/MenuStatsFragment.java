package com.example.porestaurant.view;

import static java.lang.System.err;

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

public class MenuStatsFragment extends Fragment {

    private TextView tvTotal, tvAvgPrice, tvMostExp;
    private StatisticalRepository repo;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_stats, container, false);
    }

    @Override public void onViewCreated(@NonNull View view,
                                        @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTotal    = view.findViewById(R.id.tvTotalItems);
        tvAvgPrice = view.findViewById(R.id.tvAveragePrice);
        tvMostExp  = view.findViewById(R.id.tvMostExpensive);
        repo       = new StatisticalRepository();
        loadData(2025);
    }

    private void loadData(int year) {
        repo.getMenuStats(year, new StatisticalRepository.MenuStatsCallback() {
            @Override
            public void onSuccess(Admin.MenuStatsDto d) {
                Log.d("DBG-MenuStats","items=" + d.getTotalItems()
                        + " avg=" + d.getAveragePrice()
                        + " max=" + d.getMostExpensiveItem());
                tvTotal   .setText("Tổng món: " + d.getTotalItems());
                tvAvgPrice.setText("Giá TB: " + d.getAveragePrice());
                tvMostExp .setText("Đắt nhất: " + d.getMostExpensiveItem());
            }
            @Override
            public void onError(String error) {
                Log.e("DBG-MenuStats","Error: "+err);
            }
        });
    }
}
