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

public class ForgotPasswordFragment extends Fragment {
    private EditText edtEmail;
    private Button btnSendOtp;
    private ImageView btnBack;
    private ForgotPasswordRepository forgotPasswordRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtEmail = view.findViewById(R.id.edtEmail);
        btnSendOtp = view.findViewById(R.id.btnSendOtp);
        btnBack = view.findViewById(R.id.btnBack);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        forgotPasswordRepository = new ForgotPasswordRepository(apiService);

        btnSendOtp.setOnClickListener(v -> doRequestOtp());
        TextView tvLogin = view.findViewById(R.id.tvLogin);
        tvLogin.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new LoginFragment());
            }
        });
        btnBack.setOnClickListener(v -> {
            if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                requireActivity().onBackPressed();
            }
        });
    }

    private void doRequestOtp() {
        String email = edtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }

        forgotPasswordRepository.requestOtp(email, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String msg = response.body().string();
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                            // Navigate to ResetPasswordFragment with email
                            if (getActivity() instanceof MainActivity) {
                                ResetPasswordFragment fragment = new ResetPasswordFragment();
                                Bundle args = new Bundle();
                                args.putString("email", email);
                                fragment.setArguments(args);
                                ((MainActivity) getActivity()).loadFragment(fragment);
                            }
                        } catch (Exception e) {
                            Toast.makeText(requireContext(), "Lỗi đọc response", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Gửi OTP thất bại", Toast.LENGTH_SHORT).show();
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