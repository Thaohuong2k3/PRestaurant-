package com.example.porestaurant.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.porestaurant.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<String> categories;
    private String selectedCategory = "All";
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    public CategoryAdapter(List<String> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    public void setSelectedCategory(String category) {
        this.selectedCategory = category;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.txtCategory.setText(category);

        boolean isSelected = category.equalsIgnoreCase(selectedCategory);

        // Update card appearance based on selection
        if (isSelected) {
            holder.cardView.setCardBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.primary_color)
            );
            holder.txtCategory.setTextColor(
                    holder.itemView.getContext().getColor(android.R.color.white)
            );
            holder.cardView.setStrokeWidth(0);
        } else {
            holder.cardView.setCardBackgroundColor(
                    holder.itemView.getContext().getColor(android.R.color.white)
            );
            holder.txtCategory.setTextColor(
                    holder.itemView.getContext().getColor(R.color.primary_text)
            );
            holder.cardView.setStrokeWidth(2);
            holder.cardView.setStrokeColor(
                    holder.itemView.getContext().getColor(R.color.divider_color)
            );
        }

        holder.cardView.setOnClickListener(v -> {
            if (!category.equalsIgnoreCase(selectedCategory)) {
                selectedCategory = category;
                notifyDataSetChanged();
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView txtCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            txtCategory = itemView.findViewById(R.id.txtCategory);
        }
    }
}
