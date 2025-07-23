package com.example.porestaurant.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.porestaurant.R;
import com.example.porestaurant.model.Admin;
import com.example.porestaurant.repository.StatisticalRepository;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class TopMenuItemsFragment extends Fragment {
    private PieChart chart;
    private RecyclerView rvTopItems;
    private TopMenuItemsAdapter adapter;
    private StatisticalRepository repo;
    private int year = 2025;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top_menu_items, container, false);
    }

    @Override public void onViewCreated(@NonNull View v,
                                        @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        // Initialize views
        chart = v.findViewById(R.id.pieChartTopMenu);
        rvTopItems = v.findViewById(R.id.rvTopItems);
        repo = new StatisticalRepository();

        // Get year from arguments if available
        if (getArguments() != null) {
            year = getArguments().getInt("year", 2025);
        }

        // Setup RecyclerView
        setupRecyclerView();

        // Setup chart
        setupChart();

        // Load data
        loadData();
    }

    private void setupRecyclerView() {
        adapter = new TopMenuItemsAdapter();
        rvTopItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTopItems.setAdapter(adapter);
    }

    private void setupChart() {
        // Remove description and background
        chart.getDescription().setEnabled(false);
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(45f);
        chart.setTransparentCircleRadius(50f);
        chart.setDrawEntryLabels(false);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTextSize(12f);
        chart.setDrawMarkers(true);
        chart.setHighlightPerTapEnabled(true);

        // Center text
        chart.setCenterText("Top 5\nItems");
        chart.setCenterTextSize(16f);
        chart.setCenterTextColor(getResources().getColor(R.color.admin_text_primary));

        // Legend
        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);
        legend.setTextSize(12f);
        legend.setTextColor(getResources().getColor(R.color.admin_text_primary));
        legend.setXEntrySpace(10f);
    }

    private void loadData() {
        repo.getTopMenuItems(
                5,
                year + "-01-01",
                year + "-12-31",
                new StatisticalRepository.TopMenuCallback() {
                    @Override public void onSuccess(List<Admin.TopMenuItemDto> data) {
                        if (getActivity() == null) return;

                        getActivity().runOnUiThread(() -> {
                            // Update RecyclerView
                            adapter.setItems(data);

                            // Update chart
                            updateChart(data);
                        });
                    }

                    @Override public void onError(String error) {
                        // Handle error
                    }
                }
        );
    }

    private void updateChart(List<Admin.TopMenuItemDto> data) {
        List<PieEntry> entries = new ArrayList<>();

        // Create entries for pie chart
        for (Admin.TopMenuItemDto item : data) {
            entries.add(new PieEntry(item.getTotalSold(), item.getName()));
        }

        // Create dataset
        PieDataSet dataSet = new PieDataSet(entries, "");

        // Set colors
        int[] colors = new int[] {
                getResources().getColor(R.color.admin_chart_color1),
                getResources().getColor(R.color.admin_chart_color2),
                getResources().getColor(R.color.admin_chart_color3),
                getResources().getColor(R.color.admin_chart_color4),
                getResources().getColor(R.color.admin_chart_color5)
        };
        dataSet.setColors(colors);

        // Styling
        dataSet.setSliceSpace(2f);
        dataSet.setValueLinePart1Length(0.5f);
        dataSet.setValueLinePart2Length(0.3f);
        dataSet.setValueLineColor(getResources().getColor(R.color.admin_text_secondary));
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(getResources().getColor(R.color.admin_text_primary));

        // Format values as integers
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        // Create and set data
        PieData pieData = new PieData(dataSet);
        chart.setData(pieData);

        // Animate and refresh
        chart.animateY(1000);
        chart.invalidate();
    }
}
