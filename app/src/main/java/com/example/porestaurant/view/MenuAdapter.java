package com.example.porestaurant.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.porestaurant.R;
import com.example.porestaurant.model.Menu;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

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
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        Menu menu = menuList.get(position);
        holder.tvName.setText(menu.getName());
        holder.tvDescription.setText(menu.getDescription());
        holder.tvPrice.setText(String.format("$%.2f", menu.getPrice()));

        holder.btnOrder.setOnClickListener(v -> {
            if (orderListener != null) {
                orderListener.onMenuOrdered(menu);
            }
        });

        byte[] imageData = menu.getImageData();
        if (imageData != null && imageData.length > 0) {
            holder.ivMenuImage.setImageBitmap(
                    BitmapFactory.decodeByteArray(imageData, 0,imageData.length));
        } else {
            holder.ivMenuImage.setImageResource(R.drawable.no_image);
        }
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public void updateList(List<Menu> newList) {
        menuList = newList;
        notifyDataSetChanged();
    }
    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvPrice;
        ImageView ivMenuImage;
        Button btnOrder;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txtMenuName);
            tvDescription = itemView.findViewById(R.id.txtMenuDescription);
            tvPrice = itemView.findViewById(R.id.txtMenuPrice);
            ivMenuImage = itemView.findViewById(R.id.imgMenu);
            btnOrder = itemView.findViewById(R.id.btnOrder);
        }
    }
}
