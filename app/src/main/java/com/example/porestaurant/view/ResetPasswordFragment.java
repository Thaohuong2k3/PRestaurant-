package com.example.porestaurant.view;

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
import com.example.porestaurant.network.ApiClient;
import com.example.porestaurant.network.ApiService;
import com.example.porestaurant.repository.ForgotPasswordRepository;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordFragment extends Fragment {
    private EditText edtEmail, edtOtp, edtNewPassword;
    private Button btnResetPassword;
    private ForgotPasswordRepository forgotPasswordRepository;
    private TextView tvLogin;
    private ImageView btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtOtp = view.findViewById(R.id.edtOtp);
        edtNewPassword = view.findViewById(R.id.edtNewPassword);
        btnResetPassword = view.findViewById(R.id.btnResetPassword);
        tvLogin = view.findViewById(R.id.tvLogin);
        btnBack = view.findViewById(R.id.btnBack);

        // Lấy email từ arguments nếu có (từ ForgotPasswordFragment gửi sang)
        if (getArguments() != null) {
            String email = getArguments().getString("email");
            if (email != null && !email.isEmpty()) {
                edtEmail.setText(email);
            }
        }

        // Khởi tạo ApiService và Repository
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        forgotPasswordRepository = new ForgotPasswordRepository(apiService);

        btnResetPassword.setOnClickListener(v -> doVerifyOtp());

        tvLogin.setOnClickListener(v -> {
            // TODO: Replace with fragment navigation if LoginFragment exists
            Toast.makeText(requireContext(), "Navigate to LoginFragment", Toast.LENGTH_SHORT).show();
        });
        btnBack.setOnClickListener(v -> {
            if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                requireActivity().onBackPressed();
            }
        });
    }

    private void doVerifyOtp() {
        String email = edtEmail.getText().toString().trim();
        String otp = edtOtp.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();

        if (email.isEmpty() || otp.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        forgotPasswordRepository.verifyOtp(email, otp, newPassword, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String msg = response.body().string();
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                            // Navigate to LoginFragment
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).loadFragment(new LoginFragment());
                            }
                        } catch (Exception e) {
                            Toast.makeText(requireContext(), "Lỗi đọc response", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "OTP sai hoặc đã hết hạn!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
} 