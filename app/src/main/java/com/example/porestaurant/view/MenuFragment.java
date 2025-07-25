package com.example.porestaurant.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.porestaurant.R;
import com.example.porestaurant.model.Menu;
import com.example.porestaurant.repository.CategoryRepository;
import com.example.porestaurant.repository.MenuRepository;
import com.example.porestaurant.model.CartStorage;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {
    private RecyclerView recyclerViewMenu;
    private RecyclerView recyclerViewCategory;
    private MenuAdapter menuAdapter;
    private CategoryAdapter categoryAdapter;
    private SearchView searchViewMenu;
    private ShimmerFrameLayout shimmerFrameLayout;
    private List<Menu> allMenus = new ArrayList<>();
    private String selectedCategory = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerViews();
        setupSearchView();
        loadMenus();
    }

    private void initViews(View view) {
        recyclerViewMenu = view.findViewById(R.id.recyclerMenu);
        recyclerViewCategory = view.findViewById(R.id.recyclerCategory);
        searchViewMenu = view.findViewById(R.id.searchViewMenu);
        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout);
        // Remove: cartContainer and txtCartCount
    }

    private void setupRecyclerViews() {
        // Setup Grid Layout for Menu (2 columns)
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerViewMenu.setLayoutManager(gridLayoutManager);
        recyclerViewMenu.setHasFixedSize(true);

        // Setup Horizontal Layout for Categories
        recyclerViewCategory.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        recyclerViewCategory.setHasFixedSize(true);
    }

    private void setupSearchView() {
        searchViewMenu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMenus();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMenus();
    }

    private void loadMenus() {
        showShimmer(true);
        MenuRepository repository = new MenuRepository();

        repository.getAllMenus(new MenuRepository.MenuCallback() {
            @Override
            public void onSuccess(List<Menu> menuList) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    showShimmer(false);

                    // Filter only available menus
                    allMenus = new ArrayList<>();
                    for (Menu menu : menuList) {
                        if (menu.isAvailable()) {
                            allMenus.add(menu);
                        }
                    }

                    menuAdapter = new MenuAdapter(requireContext(), allMenus, menu -> {
                        CartStorage.addToCart(requireContext(), menu);
                        Toast.makeText(requireContext(), "Added to cart: " + menu.getName(), Toast.LENGTH_SHORT).show();
                    });
                    recyclerViewMenu.setAdapter(menuAdapter);

                    loadCategoriesFromApi();
                });
            }

            @Override
            public void onError(String errorMessage) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    showShimmer(false);
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
                    categories.add("All");

                    for (com.example.porestaurant.model.Category cat : categoryList) {
                        if (cat.getCategoryName() != null) {
                            categories.add(cat.getCategoryName());
                        }
                    }

                    categoryAdapter = new CategoryAdapter(categories, new CategoryAdapter.OnCategoryClickListener() {
                        @Override
                        public void onCategoryClick(String selected) {
                            // Khi click 1 category
                            selectedCategory = selected;
                            categoryAdapter.setSelectedCategory(selected);
                            filterMenus();
                        }

                        @Override
                        public void onCategoryLongClick(String category, int position) {
                            // Ở màn MenuFragment bạn không cần xử lý long‑click,
                            // nên có thể để trống hoặc ignore
                        }
                    });
                    recyclerViewCategory.setAdapter(categoryAdapter);
                });
            }

            @Override
            public void onError(String errorMessage) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Category load failed: " + errorMessage, Toast.LENGTH_SHORT).show()
                );
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

    private void showShimmer(boolean show) {
        if (show) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.startShimmer();
            recyclerViewMenu.setVisibility(View.GONE);
        } else {
            shimmerFrameLayout.setVisibility(View.GONE);
            shimmerFrameLayout.stopShimmer();
            recyclerViewMenu.setVisibility(View.VISIBLE);
        }
    }
}
