package com.example.porestaurant.view;

import android.graphics.Color;
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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class TopMenuItemsFragment extends Fragment {
    private PieChart chart;
    private StatisticalRepository repo;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top_menu_items, container, false);
    }

    @Override public void onViewCreated(@NonNull View v,
                                        @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        chart = v.findViewById(R.id.pieChartTopMenu);
        repo   = new StatisticalRepository();

        // 1. Tắt mô tả mặc định
        chart.getDescription().setEnabled(false);

        // 2. Chuyển thành donut, thêm tiêu đề
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(45f);
        chart.setTransparentCircleRadius(50f);
        chart.setCenterText("Top 5 món bán chạy nhất");
        chart.setCenterTextSize(18f);

        // 3. Hiển thị entry labels (tên món) ngay trên slice
        chart.setDrawEntryLabels(true);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTextSize(12f);

        // 4. Legend bên dưới chỉ hiển thị tên món
        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);
        legend.setTextSize(12f);

        // Load data và vẽ
        loadData();
    }

    private void loadData() {
        repo.getTopMenuItems(
                5,
                "2025-01-01",
                "2025-12-31",
                new StatisticalRepository.TopMenuCallback() {
                    @Override public void onSuccess(List<Admin.TopMenuItemDto> data) {
                        List<PieEntry> entries = new ArrayList<>();
                        for (Admin.TopMenuItemDto d : data) {
                            entries.add(new PieEntry(d.getTotalSold(), d.getName()));
                        }

                        PieDataSet set = new PieDataSet(entries, "");
                        set.setColors(ColorTemplate.MATERIAL_COLORS);
                        set.setSliceSpace(2f);

                        // 5. Hiển thị giá trị (số lượng) ngoài slice với đường nối
                        set.setValueLinePart1Length(0.5f);
                        set.setValueLinePart2Length(0.3f);
                        set.setValueLineColor(Color.BLACK);
                        set.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                        set.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

                        // Formatter chỉ in số nguyên
                        set.setValueTextSize(12f);
                        set.setValueTextColor(Color.BLACK);
                        set.setValueFormatter(new ValueFormatter() {
                            @Override public String getFormattedValue(float value) {
                                return String.valueOf((int) value);
                            }
                        });

                        PieData pieData = new PieData(set);
                        pieData.setDrawValues(true);   // bật hiển thị giá trị ngoài slice

                        chart.setData(pieData);
                        chart.animateY(800);
                        chart.invalidate();
                    }
                    @Override public void onError(String error) {
                        // TODO: show error
                    }
                }
        );
    }
}
