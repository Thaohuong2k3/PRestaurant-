package com.example.porestaurant.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.porestaurant.R;

public class ToolbarFragment extends Fragment {

    public interface ToolbarListener {
        void onSearchClicked();
        void onFilterClicked();
    }

    private ToolbarListener toolbarListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ToolbarListener) {
            toolbarListener = (ToolbarListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ToolbarListener");
        }
    }

    private ImageView hamburgerMenu;
    private DrawerLayout drawerLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tool_bar, container, false);

        // Initialize views
        initViews(view);

        // Setup drawer
        assert getActivity() != null;
        drawerLayout = getActivity().findViewById(R.id.drawer_layout);

        // Setup click listeners
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        hamburgerMenu = view.findViewById(R.id.iv_hamburger);
//        searchIcon = view.findViewById(R.id.iv_search);
//        filterIcon = view.findViewById(R.id.iv_filter);
    }

    private void setupClickListeners() {
        hamburgerMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
    }
}