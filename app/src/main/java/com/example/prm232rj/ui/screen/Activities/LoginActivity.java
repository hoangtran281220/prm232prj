package com.example.prm232rj.ui.screen.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm232rj.R;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.prm232rj.ui.viewmodel.AuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import android.widget.ImageView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1001;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences userPrefs;
    private AuthViewModel authViewModel;
    private EditText edtUsername;
    private EditText edtPassword;
    private CheckBox chkRemember;
    private Button btnLogin;
    private ImageView btnGoogle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        initializeViews();
        setupViewModel();
        setupGoogleSignIn();
        setupClickListeners();
        loadSavedCredentials();
        observeViewModelData();
        // ➤ Google Sign-In
        setupGoogleSignIn();
        btnGoogle.setOnClickListener(v -> signInWithGoogle());
    }
    private void initializeViews() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        chkRemember = findViewById(R.id.chkRememberMe);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.imgGoogleLogin);

        userPrefs = getSharedPreferences("USER_PREF", MODE_PRIVATE);
    }

    private void setupViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> handleUsernameLogin());
        btnGoogle.setOnClickListener(v -> signInWithGoogle());
    }

    private void loadSavedCredentials() {
        SharedPreferences pref = getSharedPreferences("PREF", MODE_PRIVATE);
        edtUsername.setText(pref.getString("username", ""));
        edtPassword.setText(pref.getString("password", ""));
        chkRemember.setChecked(!pref.getString("password", "").isEmpty());
    }

    private void observeViewModelData() {
        // Login success
        authViewModel.loginSuccess.observe(this, loginResult -> {
            if (loginResult != null) {
                Toast.makeText(this, "Đăng nhập thành công: " + loginResult.username, Toast.LENGTH_SHORT).show();
                saveCredentials();
                goToHome();
            }
        });

        // Login error
        authViewModel.loginError.observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        // Google Sign-in success
        authViewModel.googleSignInSuccess.observe(this, result -> {
            if (result != null) {
                saveGoogleUserToPref(result);
                goToHome();
            }
        });

        // Google Sign-in error
        authViewModel.googleSignInError.observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUsernameLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        authViewModel.login(username, password);
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                Toast.makeText(this, "Đăng nhập Google thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveCredentials() {
        SharedPreferences pref = getSharedPreferences("PREF", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username", edtUsername.getText().toString());

        if (chkRemember.isChecked()) {
            editor.putString("password", edtPassword.getText().toString());
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
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
