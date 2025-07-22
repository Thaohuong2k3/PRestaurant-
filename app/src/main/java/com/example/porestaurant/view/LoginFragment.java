package com.example.porestaurant.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.porestaurant.R;
import com.example.porestaurant.model.GoogleLoginRequest;
import com.example.porestaurant.model.LoginRequest;
import com.example.porestaurant.model.User;
import com.example.porestaurant.repository.UserRepository;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginFragment extends Fragment {
    private EditText edtEmail, edtPassword;
    private Button btnLogin, btnGoogleLogin;
    private CheckBox chkRemember;
    private SharedPreferences sharedPreferences;
    private UserRepository userRepository;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPassword = view.findViewById(R.id.edtPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnGoogleLogin = view.findViewById(R.id.btnGoogleLogin);
        chkRemember = view.findViewById(R.id.chkRemember);
        userRepository = new UserRepository();
        sharedPreferences = requireContext().getSharedPreferences("LOGIN_PREF", 0);

        // Auto fill nếu Remember Me đã lưu
        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");
        boolean isRemember = sharedPreferences.getBoolean("remember", false);
        edtEmail.setText(savedEmail);
        edtPassword.setText(savedPassword);
        chkRemember.setChecked(isRemember);
        TextView tvRegister = view.findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new RegisterFragment());
            }
        });
        btnLogin.setOnClickListener(v -> doLogin());
        TextView tvForgot = view.findViewById(R.id.tvForgot);
        tvForgot.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new ForgotPasswordFragment());
            }
        });

        // Google Sign-In config (THAY client_id web thật của bạn)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("961582604276-brc2l9efh7al4emaqrhce029h02ib3n7.apps.googleusercontent.com") // Cái này phải là Web client ID
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        // Xử lý kết quả đăng nhập Google
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            doLoginWithGoogle(account);
                        } catch (ApiException e) {
                            Toast.makeText(requireContext(), "Đăng nhập Google thất bại! " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
                            Log.e("GOOGLE_LOGIN", "Lỗi Google login", e);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Google login canceled!", Toast.LENGTH_SHORT).show();
                    }
                });

        btnGoogleLogin.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void doLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập email và mật khẩu!", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest request = new LoginRequest(email, password);

        userRepository.login(request, new UserRepository.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    // Luôn lưu thông tin user
                    editor.putInt("userId", user.getUserID());
                    editor.putString("fullName", user.getFullName());
                    editor.putString("email", user.getEmail());
                    editor.apply();
                    if (chkRemember.isChecked()) {
                        editor.putString("password", password);
                        editor.putBoolean("remember", true);
                    } else {
                        editor.remove("password");
                        editor.putBoolean("remember", false);
                    }
                    editor.apply();

                    Toast.makeText(requireContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    // Navigate to ProfileFragment
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).loadFragment(new ProfileFragment());
                    }
                });
            }
            @Override
            public void onError(String error) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void doLoginWithGoogle(GoogleSignInAccount account) {
        if (account == null) {
            Toast.makeText(requireContext(), "Google account null!", Toast.LENGTH_SHORT).show();
            return;
        }
        String email = account.getEmail();
        String password = "Huong11@"; // Đúng như backend bạn yêu cầu
        String idToken = account.getIdToken(); // Bắt buộc đã cấu hình requestIdToken

        // GoogleLoginRequest (email, password, fullName, idToken)
        GoogleLoginRequest request = new GoogleLoginRequest(email, password, idToken);

        userRepository.googleLogin(request, new UserRepository.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    // Lưu thông tin user
                    editor.putInt("userId", user.getUserID());
                    editor.putString("fullName", user.getFullName());
                    editor.putString("email", user.getEmail());
                    editor.apply();

                    // Nếu có Remember Me (tuỳ bạn có tích hợp hay không)
                    if (chkRemember.isChecked()) {
                        editor.putBoolean("remember", true);
                        // Nếu bạn có password mặc định cho Google, có thể lưu luôn (không khuyến khích)
                        editor.putString("password", "123123"); // Hoặc password Google mặc định của bạn
                    } else {
                        editor.remove("password");
                        editor.putBoolean("remember", false);
                    }
                    editor.apply();

                    Toast.makeText(requireContext(), "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show();
                    // Navigate to ProfileFragment
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).loadFragment(new ProfileFragment());
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Google login fail: " + error, Toast.LENGTH_LONG).show());
            }
        });
    }
} 