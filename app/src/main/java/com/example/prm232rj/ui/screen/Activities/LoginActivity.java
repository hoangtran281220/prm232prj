package com.example.prm232rj.ui.screen.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm232rj.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        EditText edtUsername = findViewById(R.id.edtUsername);
        EditText edtPassword = findViewById(R.id.edtPassword);
        CheckBox chkRemember = findViewById(R.id.chkRememberMe);
        Button btnLogin = findViewById(R.id.btnLogin);
        SharedPreferences pref = getSharedPreferences("PREF", MODE_PRIVATE);
        String savedUsername = pref.getString("username", "");
        String savedPassword = pref.getString("password", "");

        edtUsername.setText(savedUsername);
        edtPassword.setText(savedPassword);
        chkRemember.setChecked(!savedPassword.isEmpty());

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
                            //
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("username", userName);

                            if (chkRemember.isChecked()) {
                                editor.putString("password", password);
                            } else {
                                editor.remove("password");
                            }

                            editor.apply();
                            //  Chuyển sang HOME
                            Intent intent = new Intent(this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩuhoangdzpro2112", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi đăng nhập: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
