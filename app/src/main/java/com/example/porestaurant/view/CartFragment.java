package com.example.porestaurant.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.porestaurant.R;
import com.example.porestaurant.model.CartStorage;
import com.example.porestaurant.model.Menu;
import java.util.List;

public class CartFragment extends Fragment {
    private RecyclerView recyclerCart;
    private TextView txtTotal;
    private CartAdapter cartAdapter;
    private List<Menu> cartItems;
    private Button btnCheckout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerCart = view.findViewById(R.id.recyclerCart);
        txtTotal = view.findViewById(R.id.txtTotal);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        cartItems = CartStorage.getCart(requireContext());
        cartAdapter = new CartAdapter(requireContext(), cartItems, this::updateTotal);
        recyclerCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerCart.setAdapter(cartAdapter);

        updateTotal();

        btnCheckout.setOnClickListener(v -> {
            // Clear the cart
            cartItems.clear();
            CartStorage.saveCart(requireContext(), cartItems);
            cartAdapter.notifyDataSetChanged();
            updateTotal();

            // Navigate to MapFragment
            MapFragment mapFragment = new MapFragment();
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).loadFragment(mapFragment);
                // Trigger the route logic after a slight delay to ensure map is ready
                view.post(() -> {
                    if (mapFragment.isAdded()) {
                        mapFragment.getCurrentLocationAndShowRoute();
                    }
                });
            }
        });
    }

    public void updateTotal() {
        double total = 0;
        for (Menu item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        txtTotal.setText(String.format("Total: $%.2f", total));
        btnCheckout.setVisibility(total > 0 ? View.VISIBLE : View.GONE);
    }
}