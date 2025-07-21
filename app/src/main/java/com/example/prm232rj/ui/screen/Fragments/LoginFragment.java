package com.example.prm232rj.ui.screen.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.prm232rj.R;
import com.example.prm232rj.databinding.FragmentLoginBinding;
import com.example.prm232rj.ui.screen.Activities.HomeActivity;
import com.example.prm232rj.ui.viewmodel.AuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment {
    private static final int RC_SIGN_IN = 1001;
    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences userPrefs;
    private boolean isPasswordVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        userPrefs = requireActivity().getSharedPreferences("USER_PREF", requireActivity().MODE_PRIVATE);

        setupGoogleSignIn();
        setupClickListeners();
        loadSavedCredentials();
        observeViewModel();

        return binding.getRoot();
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            String username = binding.edtUsername.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            authViewModel.login(username, password);
        });

        binding.imgGoogleLogin.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        binding.edtPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (binding.edtPassword.getRight() - binding.edtPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    togglePasswordVisibility(binding.edtPassword);
                    return true;
                }
            }
            return false;
        });
    }

    private void loadSavedCredentials() {
        SharedPreferences pref = requireActivity().getSharedPreferences("PREF", requireActivity().MODE_PRIVATE);
        binding.edtUsername.setText(pref.getString("username", ""));
        binding.edtPassword.setText(pref.getString("password", ""));
        binding.chkRememberMe.setChecked(!pref.getString("password", "").isEmpty());
    }

    private void observeViewModel() {
        authViewModel.loginSuccess.observe(getViewLifecycleOwner(), loginResult -> {
            if (loginResult != null) {
                Toast.makeText(getContext(), "Đăng nhập thành công: " + loginResult.username, Toast.LENGTH_SHORT).show();
                saveCredentials();
                goToHome();
            }
        });

        authViewModel.loginError.observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        authViewModel.googleSignInSuccess.observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                saveGoogleUserToPref(result);
                goToHome();
            }
        });

        authViewModel.googleSignInError.observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void togglePasswordVisibility(EditText editText) {
        if (isPasswordVisible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon_24, 0);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon_24, 0);
        }
        isPasswordVisible = !isPasswordVisible;
        editText.setSelection(editText.getText().length());
    }

    private void saveCredentials() {
        SharedPreferences pref = requireActivity().getSharedPreferences("PREF", requireActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username", binding.edtUsername.getText().toString());

        if (binding.chkRememberMe.isChecked()) {
            editor.putString("password", binding.edtPassword.getText().toString());
        } else {
            editor.remove("password");
        }

        editor.apply();
    }

    private void saveGoogleUserToPref(AuthViewModel.GoogleSignInResult result) {
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putString("uid", result.uid);
        editor.putString("email", result.email);
        editor.putString("displayName", result.displayName);
        if (result.photoUrl != null) {
            editor.putString("photoUrl", result.photoUrl);
        }
        editor.apply();
    }

    private void goToHome() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    authViewModel.signInWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                Log.e("GOOGLE_LOGIN", "Google sign-in failed. Code=" + e.getStatusCode() + ", Msg=" + e.getMessage(), e);
                Toast.makeText(getContext(), "Đăng nhập Google thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
