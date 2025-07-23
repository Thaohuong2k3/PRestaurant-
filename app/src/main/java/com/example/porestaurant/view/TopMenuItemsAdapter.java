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

        // Set rank (position + 1)
        holder.tvRank.setText(String.valueOf(position + 1));

        // Set item name
        holder.tvName.setText(dto.getName());

        // Set sold count
        holder.tvSold.setText(String.valueOf(dto.getTotalSold()));

        // Set rank background color based on position
        int colorResId;
        switch (position) {
            case 0:
                colorResId = R.color.admin_gold;
                break;
            case 1:
                colorResId = R.color.admin_silver;
                break;
            case 2:
                colorResId = R.color.admin_bronze;
                break;
            default:
                colorResId = R.color.admin_text_secondary;
                break;
        }
        holder.tvRank.getBackground().setTint(
                holder.itemView.getContext().getResources().getColor(colorResId)
        );
    }

    @Override public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvRank, tvName, tvSold;

        VH(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvSold = itemView.findViewById(R.id.tvItemSold);
        }
    }
}
