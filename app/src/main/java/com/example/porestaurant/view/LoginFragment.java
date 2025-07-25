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

        // Initialize views
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPassword = view.findViewById(R.id.edtPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnGoogleLogin = view.findViewById(R.id.btnGoogleLogin);
        chkRemember = view.findViewById(R.id.chkRemember);

        userRepository = new UserRepository();
        sharedPreferences = requireContext().getSharedPreferences("LOGIN_PREF", 0);

        // Auto fill if Remember Me was saved
        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");
        boolean isRemember = sharedPreferences.getBoolean("remember", false);
        edtEmail.setText(savedEmail);
        edtPassword.setText(savedPassword);
        chkRemember.setChecked(isRemember);

        // Navigation to register
        TextView tvRegister = view.findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new RegisterFragment());
            }
        });

        // Regular login button
        btnLogin.setOnClickListener(v -> doLogin());

        // Navigation to forgot password
        TextView tvForgot = view.findViewById(R.id.tvForgot);
        tvForgot.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new ForgotPasswordFragment());
            }
        });

        // Google Sign-In configuration (using the same working config from Activity)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("961582604276-brc2l9efh7al4emaqrhce029h02ib3n7.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        // Handle Google Sign-In result with Activity constants
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d("GOOGLE_LOGIN", "Result code: " + result.getResultCode());

                    // Import and use Activity constants
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d("GOOGLE_LOGIN", "Account: " + (account != null ? account.getEmail() : "null"));
                            doLoginWithGoogle(account);
                        } catch (ApiException e) {
                            Log.e("GOOGLE_LOGIN", "ApiException: " + e.getStatusCode() + " - " + e.getMessage());
                            Toast.makeText(requireContext(), "Đăng nhập Google thất bại! " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Also check if we have valid data even with different result code
                        if (result.getData() != null) {
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                            try {
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                                if (account != null) {
                                    Log.d("GOOGLE_LOGIN", "Found valid account despite result code: " + result.getResultCode());
                                    doLoginWithGoogle(account);
                                    return;
                                }
                            } catch (ApiException e) {
                                Log.e("GOOGLE_LOGIN", "No valid account found: " + e.getStatusCode());
                            }
                        }

                        Log.e("GOOGLE_LOGIN", "Login canceled or failed. Result code: " + result.getResultCode());
                        Toast.makeText(requireContext(), "Google login canceled or failed!", Toast.LENGTH_SHORT).show();
                    }
                });

        // Google login button click with better debugging
        btnGoogleLogin.setOnClickListener(v -> {
            Log.d("GOOGLE_LOGIN", "Google login button clicked");
            try {
                // Clear previous sign-in to force account selection
                mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    Log.d("GOOGLE_LOGIN", "Launching sign-in intent");
                    googleSignInLauncher.launch(signInIntent);
                });
            } catch (Exception e) {
                Log.e("GOOGLE_LOGIN", "Error launching Google sign-in", e);
                Toast.makeText(requireContext(), "Error starting Google login: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
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
                    // Always save user info
                    editor.putInt("userId", user.getUserID());
                    editor.putString("fullName", user.getFullName());
                    editor.putString("email", user.getEmail());
                    editor.putString("role", user.getRole());
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
                    if (user.getRole().equalsIgnoreCase("Admin")) {
                        // Navigate to admin dashboard
                        Intent intent = new Intent(requireContext(), AdminDashboardActivity.class);
                        startActivity(intent);
                        requireActivity().finish(); // Optional: close login activity
                    } else {
                        // Navigate to profile for customers
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).updateNavigationMenu();
                            ((MainActivity) getActivity()).loadFragment(new MenuFragment());
                        }
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
        String password = "Huong11@"; // Same default password used in backend
        String idToken = account.getIdToken();
        String fullName = account.getDisplayName();
        if (fullName == null || fullName.isEmpty()) {
            fullName = "Hello User";
        }

        // Create request with all 4 parameters (using the working implementation from Activity)
        GoogleLoginRequest request = new GoogleLoginRequest(email, password, idToken, fullName);

        userRepository.googleLogin(request, new UserRepository.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    // Save user info
                    editor.putInt("userId", user.getUserID());
                    editor.putString("fullName", user.getFullName());
                    editor.putString("email", user.getEmail());
                    editor.putString("role", user.getRole());
                    editor.apply();

                    // Handle Remember Me for Google login
                    if (chkRemember.isChecked()) {
                        editor.putBoolean("remember", true);
                        editor.putString("password", "123123"); // Optional default password
                    } else {
                        editor.remove("password");
                        editor.putBoolean("remember", false);
                    }
                    editor.apply();

                    Toast.makeText(requireContext(), "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show();

                    if (user.getRole().equalsIgnoreCase("Admin")) {
                        Intent intent = new Intent(requireContext(), AdminDashboardActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    } else {
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).updateNavigationMenu();
                            ((MainActivity) getActivity()).loadFragment(new MenuFragment());
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Google login fail: " + error, Toast.LENGTH_LONG).show()
                );
            }
        });
    }
}