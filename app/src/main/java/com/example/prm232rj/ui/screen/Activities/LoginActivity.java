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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;
public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences userPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userPrefs = getSharedPreferences("USER_PREF", MODE_PRIVATE);

        EditText edtUsername = findViewById(R.id.edtUsername);
        EditText edtPassword = findViewById(R.id.edtPassword);
        CheckBox chkRemember = findViewById(R.id.chkRememberMe);
        ImageView btnGoogle = findViewById(R.id.imgGoogleLogin);
        Button btnLogin = findViewById(R.id.btnLogin);


        SharedPreferences pref = getSharedPreferences("PREF", MODE_PRIVATE);
        edtUsername.setText(pref.getString("username", ""));
        edtPassword.setText(pref.getString("password", ""));
        chkRemember.setChecked(!pref.getString("password", "").isEmpty());



        btnLogin.setOnClickListener(v -> {
            String userName = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (userName.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Truy vấn Firestore
            db.collection("User")
                    .whereEqualTo("Username", userName)
                    .whereEqualTo("Password", password)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            String username = document.getString("Username");
                            Toast.makeText(this, "Đăng nhập thành công: " + username, Toast.LENGTH_SHORT).show();

                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("username", userName);

                            if (chkRemember.isChecked()) {
                                editor.putString("password", password);
                            } else {
                                editor.remove("password");
                            }

                            editor.apply();
                            //  Chuyển sang HOME
                            goToHome();
                        } else {
                            Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩuhoangdzpro2112", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi đăng nhập: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
        // ➤ Google Sign-In
        setupGoogleSignIn();
        btnGoogle.setOnClickListener(v -> signInWithGoogle());
    }
    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // from Firebase Console
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                Log.e("GOOGLE_LOGIN", "Google sign-in failed. Code=" + e.getStatusCode() + ", Msg=" + e.getMessage(), e);
                Toast.makeText(this, "Đăng nhập Google thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    checkAndCreateUser(user);
                }
            } else {
                Toast.makeText(this, "Đăng nhập Firebase thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAndCreateUser(FirebaseUser user) {
        String uid = user.getUid();

        db.collection("User").document(uid).get().addOnSuccessListener(doc -> {
            if (!doc.exists()) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("Username", user.getDisplayName());
                userMap.put("Email", user.getEmail());
                userMap.put("RoleId", 1);
                userMap.put("avatarUrl", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");
                userMap.put("isEmailLinked", true);
                userMap.put("linkedProvider", "google");
                userMap.put("CreatedAt", FieldValue.serverTimestamp());
                userMap.put("UpdateAt", FieldValue.serverTimestamp());

                db.collection("User").document(uid).set(userMap)
                        .addOnSuccessListener(aVoid -> {
                            saveUserToPref(user);
                            goToHome();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Lỗi tạo user Firestore", Toast.LENGTH_SHORT).show());
            } else {
                saveUserToPref(user);
                goToHome();
            }
        });
    }

    private void saveUserToPref(FirebaseUser user) {
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putString("uid", user.getUid());
        editor.putString("email", user.getEmail());
        editor.putString("displayName", user.getDisplayName());
        if (user.getPhotoUrl() != null) {
            editor.putString("photoUrl", user.getPhotoUrl().toString());
        }
        editor.apply();
    }

    private void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
