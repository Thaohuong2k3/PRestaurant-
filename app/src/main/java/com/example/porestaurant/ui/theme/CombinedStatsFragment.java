package com.example.porestaurant.ui.theme;

import android.graphics.Color;  // thêm import này
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.porestaurant.R;
import com.example.porestaurant.model.Admin;
import com.example.porestaurant.repository.StatisticalRepository;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class CombinedStatsFragment extends Fragment {

    private BarChart chartRevenue, chartOrders;
    private StatisticalRepository repo;
    private int year = 2025;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_combined_stats, container, false);
    }

    @Override public void onViewCreated(@NonNull View view,
                                        @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chartRevenue = view.findViewById(R.id.barChartRevenueCombined);
        chartOrders  = view.findViewById(R.id.barChartOrdersCombined);
        repo         = new StatisticalRepository();

        setupChart(chartRevenue);
        setupChart(chartOrders);

        loadRevenue(year);
        loadOrders(year);
    }

    private void setupChart(BarChart chart) {
        chart.getDescription().setEnabled(false);

        // Bật legend nếu muốn hiển thị label dataset
        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(12f);

        final String[] months = {
                "Jan","Feb","Mar","Apr","May","Jun",
                "Jul","Aug","Sep","Oct","Nov","Dec"
        };
        XAxis x = chart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setGranularity(1f);
        x.setValueFormatter(new IndexAxisValueFormatter(months));
        x.setCenterAxisLabels(true);

        chart.setFitBars(true);
    }

    private void loadRevenue(int y) {
        repo.getRevenueByMonth(y, new StatisticalRepository.RevenueCallback() {
            @Override public void onSuccess(List<Admin.RevenueByMonthDto> data) {
                List<BarEntry> entries = new ArrayList<>();
                for (Admin.RevenueByMonthDto d : data) {
                    entries.add(new BarEntry(d.getMonth() - 1, (float)d.getRevenue()));
                }
                BarDataSet set = new BarDataSet(entries, "Doanh thu");
                // Đổi màu xanh lá cho doanh thu
                set.setColor(Color.parseColor("#4CAF50"));

                BarData bd = new BarData(set);
                bd.setBarWidth(0.8f);
                chartRevenue.setData(bd);
                chartRevenue.animateY(800);
                chartRevenue.invalidate();
            }
            @Override public void onError(String err) { /* xử lý lỗi nếu cần */ }
        });
    }

    private void loadOrders(int y) {
        repo.getOrderCountByMonth(y, new StatisticalRepository.OrderCountCallback() {
            @Override public void onSuccess(List<Admin.OrderCountByMonthDto> data) {
                List<BarEntry> entries = new ArrayList<>();
                for (Admin.OrderCountByMonthDto d : data) {
                    entries.add(new BarEntry(d.getMonth() - 1, d.getOrderCount()));
                }
                BarDataSet set = new BarDataSet(entries, "Số đơn");
                // Đổi màu xanh dương cho số đơn
                set.setColor(Color.parseColor("#2196F3"));

                BarData bd = new BarData(set);
                bd.setBarWidth(0.8f);
                chartOrders.setData(bd);
                chartOrders.animateY(800);
                chartOrders.invalidate();
            }
            @Override public void onError(String err) { /* xử lý lỗi nếu cần */ }
        });
    }
}
