package com.example.porestaurant.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.porestaurant.R;
import com.example.porestaurant.model.UpdateUserRequest;
import com.example.porestaurant.repository.UserRepository;

public class ProfileFragment extends Fragment {
    private EditText edtProfileFullName, edtProfilePassword, edtProfileConfirmPass;
    private TextView tvProfileEmail, tvProfileEmailReadonly;
    private Button btnProfileUpdate, btnProfileLogout;
    private ImageView btnProfileBack;
    private SharedPreferences sharedPreferences;
    private UserRepository userRepository;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtProfileFullName = view.findViewById(R.id.edtProfileFullName);
        edtProfilePassword = view.findViewById(R.id.edtProfilePassword);
        edtProfileConfirmPass = view.findViewById(R.id.edtProfileConfirmPass);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        btnProfileUpdate = view.findViewById(R.id.btnProfileUpdate);
        btnProfileLogout = view.findViewById(R.id.btnProfileLogout);
        btnProfileBack = view.findViewById(R.id.btnProfileBack);
        tvProfileEmailReadonly = view.findViewById(R.id.tvProfileEmailReadonly);
        userRepository = new UserRepository();
        sharedPreferences = requireContext().getSharedPreferences("LOGIN_PREF", 0);

        // Lấy thông tin từ SharedPreferences
        userId = sharedPreferences.getInt("userId", -1);
        String fullName = sharedPreferences.getString("fullName", "");
        String email = sharedPreferences.getString("email", "");

        edtProfileFullName.setText(fullName);
        tvProfileEmail.setText(email);
        tvProfileEmailReadonly.setText(email);

        btnProfileUpdate.setOnClickListener(v -> doUpdateProfile());

        btnProfileLogout.setOnClickListener(v -> {
            sharedPreferences.edit().clear().apply();
            // Navigate to LoginFragment
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new LoginFragment());
            }
        });

        btnProfileBack.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new MenuFragment());
            } else if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                requireActivity().onBackPressed();
            }
        });
    }

    private void doUpdateProfile() {
        String newFullName = edtProfileFullName.getText().toString().trim();
        String newPassword = edtProfilePassword.getText().toString();
        String confirmPass = edtProfileConfirmPass.getText().toString();

        if (newFullName.isEmpty()) {
            Toast.makeText(requireContext(), "Họ tên không được bỏ trống!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPassword.isEmpty() && !newPassword.equals(confirmPass)) {
            Toast.makeText(requireContext(), "Xác nhận mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateUserRequest request = new UpdateUserRequest(userId, newFullName, newPassword);

        userRepository.updateUser(request, new UserRepository.UpdateCallback() {
            @Override
            public void onSuccess(String message) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    sharedPreferences.edit().putString("fullName", newFullName).apply();
                    Toast.makeText(requireContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    edtProfilePassword.setText("");
                    edtProfileConfirmPass.setText("");
                });
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }
} 