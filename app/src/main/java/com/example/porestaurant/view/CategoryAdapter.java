package com.example.porestaurant.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.porestaurant.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        /** Click bình thường để edit */
        void onCategoryClick(String category);
        /** Long‑click để delete */
        void onCategoryLongClick(String category, int position);
    }

    private final List<String> categories;
    private String selectedCategory = "All";
    private final OnCategoryClickListener listener;

    public CategoryAdapter(List<String> categories, OnCategoryClickListener listener) {
        this.categories = categories != null
                ? new ArrayList<>(categories)
                : new ArrayList<>();
        this.listener = listener;
    }

    /** Thay toàn bộ danh sách */
    public void setCategories(List<String> newList) {
        categories.clear();
        if (newList != null) categories.addAll(newList);
        notifyDataSetChanged();
    }

    /** Thêm mới */
    public void addCategory(String c) {
        categories.add(0, c);
        notifyItemInserted(0);
    }

    /** Cập nhật */
    public void updateCategory(int pos, String c) {
        if (pos >= 0 && pos < categories.size()) {
            categories.set(pos, c);
            notifyItemChanged(pos);
        }
    }

    /** Xóa */
    public void removeCategory(int pos) {
        if (pos >= 0 && pos < categories.size()) {
            categories.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    /** Highlight */
    public void setSelectedCategory(String category) {
        this.selectedCategory = category;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int pos) {
        String cat = categories.get(pos);
        holder.txtCategory.setText(cat);

        boolean isSel = cat.equalsIgnoreCase(selectedCategory);
        if (isSel) {
            holder.cardView.setCardBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.primary_color));
            holder.txtCategory.setTextColor(
                    holder.itemView.getContext().getColor(android.R.color.white));
            holder.cardView.setStrokeWidth(0);
        } else {
            holder.cardView.setCardBackgroundColor(
                    holder.itemView.getContext().getColor(android.R.color.white));
            holder.txtCategory.setTextColor(
                    holder.itemView.getContext().getColor(R.color.primary_text));
            holder.cardView.setStrokeWidth(2);
            holder.cardView.setStrokeColor(
                    holder.itemView.getContext().getColor(R.color.divider_color));
        }

        // Click để edit
        holder.cardView.setOnClickListener(v -> {
            if (!cat.equalsIgnoreCase(selectedCategory)) {
                selectedCategory = cat;
                notifyDataSetChanged();
            }
            listener.onCategoryClick(cat);
        });

        // Long‑click để delete
        holder.cardView.setOnLongClickListener(v -> {
            listener.onCategoryLongClick(cat, pos);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView txtCategory;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView    = itemView.findViewById(R.id.cardView);
            txtCategory = itemView.findViewById(R.id.txtCategory);
        }
    }
}
