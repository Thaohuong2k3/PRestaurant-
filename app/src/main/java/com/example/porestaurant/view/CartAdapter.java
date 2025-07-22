package com.example.porestaurant.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.porestaurant.R;
import com.example.porestaurant.model.Menu;
import com.example.porestaurant.model.CartStorage;

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
        holder.tvQuantity.setText(String.valueOf(menu.getQuantity()));
        holder.tvPrice.setText(String.format("$%.2f", menu.getPrice() * menu.getQuantity()));

        holder.btnIncrease.setOnClickListener(v -> {
            menu.setQuantity(menu.getQuantity() + 1);
            holder.tvQuantity.setText(String.valueOf(menu.getQuantity()));
            holder.tvPrice.setText(String.format("$%.2f", menu.getPrice() * menu.getQuantity()));
            CartStorage.saveCart(context, cartList);
            listener.onCartChanged();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int qty = menu.getQuantity() - 1;
            if (qty <= 0) {
                cartList.remove(position);
                CartStorage.saveCart(context, cartList);
                notifyItemRemoved(position);
            } else {
                menu.setQuantity(qty);
                holder.tvQuantity.setText(String.valueOf(menu.getQuantity()));
                holder.tvPrice.setText(String.format("$%.2f", menu.getPrice() * menu.getQuantity()));
                CartStorage.saveCart(context, cartList);
            }
            listener.onCartChanged();
        });

        holder.btnRemove.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION && currentPosition < cartList.size()) {
                cartList.remove(currentPosition);
                CartStorage.saveCart(context, cartList);
                notifyItemRemoved(currentPosition);
                listener.onCartChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvPrice;
        Button btnIncrease, btnDecrease, btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txtCartItemName);
            tvQuantity = itemView.findViewById(R.id.txtCartItemQuantity);
            tvPrice = itemView.findViewById(R.id.txtCartItemPrice);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
