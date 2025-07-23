package com.example.porestaurant.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.porestaurant.R;
import com.example.porestaurant.model.CartStorage;
import com.example.porestaurant.model.Menu;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class CartFragment extends Fragment {
    private RecyclerView recyclerCart;
    private TextView txtTotal, txtSubtotal, txtTax, txtItemCount;
    private MaterialCardView emptyCartCard, summaryCard;
    private MaterialButton btnCheckout;
    private CartAdapter cartAdapter;
    private List<Menu> cartItems;
    private View emptyCartLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        loadCartItems();
        setupCheckoutButton();
    }

    private void initViews(View view) {
        recyclerCart = view.findViewById(R.id.recyclerCart);
        txtTotal = view.findViewById(R.id.txtTotal);
        txtSubtotal = view.findViewById(R.id.txtSubtotal);
        txtTax = view.findViewById(R.id.txtTax);
        txtItemCount = view.findViewById(R.id.txtItemCount);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        emptyCartLayout = view.findViewById(R.id.emptyCartLayout);
        summaryCard = view.findViewById(R.id.summaryCard);
    }

    private void setupRecyclerView() {
        recyclerCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerCart.setHasFixedSize(true);
    }

    private void loadCartItems() {
        cartItems = CartStorage.getCart(requireContext());
        cartAdapter = new CartAdapter(requireContext(), cartItems, this::updateCartSummary);
        recyclerCart.setAdapter(cartAdapter);
        updateCartSummary();
    }

    private void setupCheckoutButton() {
        btnCheckout.setOnClickListener(v -> {
            // Clear the cart
            cartItems.clear();
            CartStorage.saveCart(requireContext(), cartItems);
            cartAdapter.notifyDataSetChanged();
            updateCartSummary();

            // Navigate to MapFragment
            MapFragment mapFragment = new MapFragment();
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).loadFragment(mapFragment);
                // Trigger the route logic after a slight delay to ensure map is ready
                v.post(() -> {
                    if (mapFragment.isAdded()) {
                        mapFragment.getCurrentLocationAndShowRoute();
                    }
                });
            }
        });
    }

    public void updateCartSummary() {
        if (cartItems.isEmpty()) {
            showEmptyCart();
            return;
        }

        showCartWithItems();

        double subtotal = 0;
        int totalItems = 0;

        for (Menu item : cartItems) {
            subtotal += item.getPrice() * item.getQuantity();
            totalItems += item.getQuantity();
        }

        double tax = subtotal * 0.08; // 8% tax
        double total = subtotal + tax;

        txtItemCount.setText(String.format("%d item%s", totalItems, totalItems == 1 ? "" : "s"));
        txtSubtotal.setText(String.format("$%.2f", subtotal));
        txtTax.setText(String.format("$%.2f", tax));
        txtTotal.setText(String.format("$%.2f", total));
    }

    private void showEmptyCart() {
        emptyCartLayout.setVisibility(View.VISIBLE);
        recyclerCart.setVisibility(View.GONE);
        summaryCard.setVisibility(View.GONE);
    }

    private void showCartWithItems() {
        emptyCartLayout.setVisibility(View.GONE);
        recyclerCart.setVisibility(View.VISIBLE);
        summaryCard.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartItems();
    }
}
