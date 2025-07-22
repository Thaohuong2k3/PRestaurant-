package com.example.porestaurant.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartStorage {
    private static final String PREF_NAME = "CartPref";
    private static final String CART_KEY = "cartItems";

    public static void saveCart(Context context, List<Menu> cartList) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(cartList);

        editor.putString(CART_KEY, json);
        editor.apply();
    }

    public static List<Menu> getCart(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(CART_KEY, null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Menu>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }

    public static void clearCart(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(CART_KEY).apply();
    }

    public static void addToCart(Context context, Menu newItem) {
        List<Menu> cart = getCart(context);

        boolean found = false;
        for (Menu item : cart) {
            if (item.getMenuId() == newItem.getMenuId()) {
                item.setQuantity(item.getQuantity() + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            newItem.setQuantity(1);
            cart.add(newItem);
        }

        saveCart(context, cart);
    }

    public static int getCartCount(Context context) {
        List<Menu> cartList = getCart(context);
        int total = 0;
        for (Menu item : cartList) {
            total += item.getQuantity();
        }
        return total;
    }
}
