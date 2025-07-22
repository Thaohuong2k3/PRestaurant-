package com.example.porestaurant.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.porestaurant.R;
import com.example.porestaurant.model.Menu;
import com.example.porestaurant.model.CartStorage;
import com.example.porestaurant.repository.CategoryRepository;
import com.example.porestaurant.repository.MenuRepository;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {
    private RecyclerView recyclerViewMenu;
    private RecyclerView recyclerViewCategory;
    private MenuAdapter menuAdapter;
    private CategoryAdapter categoryAdapter;
    private Button btnCart;
    private SearchView searchViewMenu;
    private Button btnCreateMenu;

    private List<Menu> allMenus = new ArrayList<>();
    private String selectedCategory = "All"; // Track selected category

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewMenu = view.findViewById(R.id.recyclerMenu);
        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(requireContext()));

        recyclerViewCategory = view.findViewById(R.id.recyclerCategory);
        recyclerViewCategory.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        searchViewMenu = view.findViewById(R.id.searchViewMenu);
        searchViewMenu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Not using submit
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMenus();
                return true;
            }
        });

        btnCreateMenu = view.findViewById(R.id.btnCreateMenu);
        btnCart = view.findViewById(R.id.btnCart);

        btnCart.setOnClickListener(v -> {
            // Navigate to CartFragment
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new CartFragment());
            }
        });

        btnCreateMenu.setOnClickListener(v -> {
            // Navigate to CreateMenuFragment
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new CreateMenuFragment());
            }
        });

        loadMenus();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMenus();
        updateCartCount(); // update when returning
    }

    private void loadMenus() {
        MenuRepository repository = new MenuRepository();

        repository.getAllMenus(new MenuRepository.MenuCallback() {
            @Override
            public void onSuccess(List<Menu> menuList) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    allMenus = menuList;

                    // Setup menu list
                    menuAdapter = new MenuAdapter(requireContext(), menuList, menu -> {
                        CartStorage.addToCart(requireContext(), menu);
                        updateCartCount();
                        Toast.makeText(requireContext(), "Added to cart: " + menu.getName(), Toast.LENGTH_SHORT).show();
                    });
                    recyclerViewMenu.setAdapter(menuAdapter);

                    // Load categories from API
                    loadCategoriesFromApi();
                });
            }

            @Override
            public void onError(String errorMessage) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("MenuFragment", "loadMenus: " + errorMessage);
                });
            }
        });
    }

    private void loadCategoriesFromApi() {
        CategoryRepository categoryRepository = new CategoryRepository();
        categoryRepository.getAllCategories(new CategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(List<com.example.porestaurant.model.Category> categoryList) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    List<String> categories = new ArrayList<>();
                    categories.add("All"); // default

                    for (com.example.porestaurant.model.Category cat : categoryList) {
                        if (cat.getCategoryName() != null) {
                            categories.add(cat.getCategoryName());
                        }
                    }

                    categoryAdapter = new CategoryAdapter(categories, selected -> {
                        selectedCategory = selected;
                        filterMenus();
                    });

                    recyclerViewCategory.setAdapter(categoryAdapter);
                });
            }

            @Override
            public void onError(String errorMessage) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Category load failed: " + errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void filterMenus() {
        String searchQuery = searchViewMenu.getQuery().toString().toLowerCase().trim();
        List<Menu> filtered = new ArrayList<>();

        for (Menu m : allMenus) {
            boolean matchesCategory = selectedCategory.equals("All") ||
                    (m.getCategoryName() != null && selectedCategory.equals(m.getCategoryName()));
            boolean matchesSearch = m.getName().toLowerCase().contains(searchQuery);

            if (matchesCategory && matchesSearch) {
                filtered.add(m);
            }
        }

        if (menuAdapter != null) {
            menuAdapter.updateList(filtered);
        }
    }

    private void updateCartCount() {
        List<Menu> cart = CartStorage.getCart(requireContext());
        int count = 0;
        for (Menu item : cart) {
            count += item.getQuantity();
        }
        btnCart.setText("Cart (" + count + ")");
    }

    public void updateList(List<Menu> newList) {
        allMenus = newList;
        if (menuAdapter != null) menuAdapter.notifyDataSetChanged();
    }
} 