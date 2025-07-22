package com.example.porestaurant.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.porestaurant.R;
import com.example.porestaurant.model.Admin;

import java.util.ArrayList;
import java.util.List;

public class TopMenuItemsAdapter extends RecyclerView.Adapter<TopMenuItemsAdapter.VH> {

    private final List<Admin.TopMenuItemDto> items = new ArrayList<>();

    public void setItems(List<Admin.TopMenuItemDto> data) {
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_menu, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Admin.TopMenuItemDto dto = items.get(position);
        holder.tvName.setText(dto.getName());
        holder.tvSold.setText(String.valueOf(dto.getTotalSold()));
    }

    @Override public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvSold;
        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvSold = itemView.findViewById(R.id.tvItemSold);
        }
    }
}
