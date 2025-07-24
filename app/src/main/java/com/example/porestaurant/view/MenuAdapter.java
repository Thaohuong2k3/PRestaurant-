package com.example.porestaurant.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.porestaurant.R;
import com.example.porestaurant.model.Menu;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    // Keep the original interface name
    public interface OnMenuOrderListener {
        void onMenuOrdered(Menu menu);
    }

    private Context context;
    private List<Menu> menuList;
    private OnMenuOrderListener orderListener;

    public MenuAdapter(Context context, List<Menu> menuList, OnMenuOrderListener listener) {
        this.context = context;
        this.menuList = menuList;
        this.orderListener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item_grid, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        Menu menu = menuList.get(position);

        holder.tvName.setText(menu.getName());
        holder.tvDescription.setText(menu.getDescription());
        holder.tvPrice.setText(String.format("$%.2f", menu.getPrice()));

        // Check login state
        boolean isLoggedIn = context.getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE)
                .contains("email");

        holder.btnOrder.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);

        holder.btnOrder.setOnClickListener(v -> {
            if (orderListener != null) {
                orderListener.onMenuOrdered(menu);
            }
        });

        // Keep only the image loading logic
        byte[] imageData = menu.getImageData();
        if (imageData != null && imageData.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            Glide.with(context)
                    .load(bitmap)
                    .apply(new RequestOptions()
                            .transform(new RoundedCorners(16))
                            .placeholder(R.drawable.placeholder_food)
                            .error(R.drawable.no_image))
                    .into(holder.ivMenuImage);
        } else {
            Glide.with(context)
                    .load(R.drawable.placeholder_food)
                    .apply(new RequestOptions().transform(new RoundedCorners(16)))
                    .into(holder.ivMenuImage);
        }

        // Add click listener to the card instead
        holder.cardView.setOnClickListener(v -> {
            // Removed clickListener
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public void updateList(List<Menu> newList) {
        menuList = newList;
        notifyDataSetChanged();
    }

    // Restore the order button in ViewHolder
    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvName, tvDescription, tvPrice;
        ImageView ivMenuImage;
        MaterialButton btnOrder; // Add this back

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvName = itemView.findViewById(R.id.txtMenuName);
            tvDescription = itemView.findViewById(R.id.txtMenuDescription);
            tvPrice = itemView.findViewById(R.id.txtMenuPrice);
            ivMenuImage = itemView.findViewById(R.id.imgMenu);
            btnOrder = itemView.findViewById(R.id.btnOrder); // Add this back
        }
    }
}
