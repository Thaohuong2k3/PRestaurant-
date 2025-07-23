package com.example.porestaurant.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.porestaurant.R;
import com.example.porestaurant.model.Menu;
import com.example.porestaurant.model.CartStorage;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<Menu> cartList;
    private OnCartChangedListener listener;

    public interface OnCartChangedListener {
        void onCartChanged();
    }

    public CartAdapter(Context context, List<Menu> cartList, OnCartChangedListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Menu menu = cartList.get(position);

        holder.tvName.setText(menu.getName());
        holder.tvDescription.setText(menu.getDescription());
        holder.tvQuantity.setText(String.valueOf(menu.getQuantity()));
        holder.tvUnitPrice.setText(String.format("$%.2f", menu.getPrice()));
        holder.tvTotalPrice.setText(String.format("$%.2f", menu.getPrice() * menu.getQuantity()));

        // Load image
        loadMenuImage(holder, menu);

        // Quantity controls
        holder.btnIncrease.setOnClickListener(v -> {
            menu.setQuantity(menu.getQuantity() + 1);
            updateItemDisplay(holder, menu);
            saveAndNotify();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int qty = menu.getQuantity() - 1;
            if (qty <= 0) {
                removeItem(position);
            } else {
                menu.setQuantity(qty);
                updateItemDisplay(holder, menu);
                saveAndNotify();
            }
        });

        holder.btnRemove.setOnClickListener(v -> removeItem(holder.getAdapterPosition()));
    }

    private void loadMenuImage(CartViewHolder holder, Menu menu) {
        byte[] imageData = menu.getImageData();
        if (imageData != null && imageData.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            Glide.with(context)
                    .load(bitmap)
                    .apply(new RequestOptions()
                            .transform(new RoundedCorners(12))
                            .placeholder(R.drawable.placeholder_food)
                            .error(R.drawable.no_image))
                    .into(holder.imgMenu);
        } else {
            Glide.with(context)
                    .load(R.drawable.placeholder_food)
                    .apply(new RequestOptions().transform(new RoundedCorners(12)))
                    .into(holder.imgMenu);
        }
    }

    private void updateItemDisplay(CartViewHolder holder, Menu menu) {
        holder.tvQuantity.setText(String.valueOf(menu.getQuantity()));
        holder.tvTotalPrice.setText(String.format("$%.2f", menu.getPrice() * menu.getQuantity()));
    }

    private void removeItem(int position) {
        if (position != RecyclerView.NO_POSITION && position < cartList.size()) {
            cartList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartList.size());
            saveAndNotify();
        }
    }

    private void saveAndNotify() {
        CartStorage.saveCart(context, cartList);
        if (listener != null) {
            listener.onCartChanged();
        }
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView imgMenu;
        TextView tvName, tvDescription, tvQuantity, tvUnitPrice, tvTotalPrice;
        MaterialButton btnIncrease, btnDecrease, btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            imgMenu = itemView.findViewById(R.id.imgMenu);
            tvName = itemView.findViewById(R.id.txtCartItemName);
            tvDescription = itemView.findViewById(R.id.txtCartItemDescription);
            tvQuantity = itemView.findViewById(R.id.txtCartItemQuantity);
            tvUnitPrice = itemView.findViewById(R.id.txtUnitPrice);
            tvTotalPrice = itemView.findViewById(R.id.txtTotalPrice);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
