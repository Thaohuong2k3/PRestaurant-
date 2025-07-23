package com.example.porestaurant.view;

import android.graphics.Color;
import android.os.Bundle;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CombinedStatsFragment extends Fragment {

    private BarChart chartRevenue, chartOrders;
    private TextView txtTotalRevenue, txtTotalOrders;
    private StatisticalRepository repo;
    private int year = 2025;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_combined_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        // Get year from arguments
        if (getArguments() != null) {
            year = getArguments().getInt("year", 2025);
        }

        setupCharts();
        loadData();
    }

    private void initViews(View view) {
        chartRevenue = view.findViewById(R.id.barChartRevenueCombined);
        chartOrders = view.findViewById(R.id.barChartOrdersCombined);
        txtTotalRevenue = view.findViewById(R.id.txtTotalRevenue);
        txtTotalOrders = view.findViewById(R.id.txtTotalOrders);
        repo = new StatisticalRepository();
    }

    private void setupCharts() {
        setupChart(chartRevenue, getResources().getColor(R.color.admin_success));
        setupChart(chartOrders, getResources().getColor(R.color.admin_info));
    }

    private void setupChart(BarChart chart, int color) {
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);

        // Legend
        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        // X Axis
        final String[] months = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(getResources().getColor(R.color.admin_text_secondary));

        // Y Axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(getResources().getColor(R.color.admin_divider));
        leftAxis.setTextColor(getResources().getColor(R.color.admin_text_secondary));
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        chart.setFitBars(true);
    }

    private void loadData() {
        loadRevenue(year);
        loadOrders(year);
    }

    private void loadRevenue(int y) {
        repo.getRevenueByMonth(y, new StatisticalRepository.RevenueCallback() {
            @Override
            public void onSuccess(List<Admin.RevenueByMonthDto> data) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    List<BarEntry> entries = new ArrayList<>();
                    double totalRevenue = 0;

                    for (Admin.RevenueByMonthDto d : data) {
                        entries.add(new BarEntry(d.getMonth() - 1, (float) d.getRevenue()));
                        totalRevenue += d.getRevenue();
                    }

                    // Update total revenue
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
                    txtTotalRevenue.setText(formatter.format(totalRevenue));

                    BarDataSet dataSet = new BarDataSet(entries, "Revenue");
                    dataSet.setColor(getResources().getColor(R.color.admin_success));
                    dataSet.setDrawValues(false);

                    BarData barData = new BarData(dataSet);
                    barData.setBarWidth(0.8f);

                    chartRevenue.setData(barData);
                    chartRevenue.animateY(1000);
                    chartRevenue.invalidate();
                });
            }

            @Override
            public void onError(String err) {
                // Handle error
            }
        });
    }

    private void loadOrders(int y) {
        repo.getOrderCountByMonth(y, new StatisticalRepository.OrderCountCallback() {
            @Override
            public void onSuccess(List<Admin.OrderCountByMonthDto> data) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    List<BarEntry> entries = new ArrayList<>();
                    int totalOrders = 0;

                    for (Admin.OrderCountByMonthDto d : data) {
                        entries.add(new BarEntry(d.getMonth() - 1, d.getOrderCount()));
                        totalOrders += d.getOrderCount();
                    }

                    // Update total orders
                    txtTotalOrders.setText(String.valueOf(totalOrders));

                    BarDataSet dataSet = new BarDataSet(entries, "Orders");
                    dataSet.setColor(getResources().getColor(R.color.admin_info));
                    dataSet.setDrawValues(false);

                    BarData barData = new BarData(dataSet);
                    barData.setBarWidth(0.8f);

                    chartOrders.setData(barData);
                    chartOrders.animateY(1000);
                    chartOrders.invalidate();
                });
            }

            @Override
            public void onError(String err) {
                // Handle error
            }
        });
    }
}
