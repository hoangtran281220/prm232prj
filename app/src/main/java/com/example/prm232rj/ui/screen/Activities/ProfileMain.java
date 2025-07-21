package com.example.prm232rj.ui.screen.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.prm232rj.MainActivity;
import com.example.prm232rj.R;

public class ProfileMain extends AppCompatActivity {
    Button uploadAvatar, confirmEmail, changeProfile, notification, btnLogout, btnRedirectHome;
    ImageView avatar;
    TextView usernameText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_main);
        uploadAvatar = findViewById(R.id.uploadAvatar);
        confirmEmail = findViewById(R.id.confirmEmail);
        changeProfile = findViewById(R.id.changeProfile);
        notification = findViewById(R.id.notification);
        btnLogout = findViewById(R.id.btnLogout);
        btnRedirectHome = findViewById(R.id.btnRedirectHome);
        avatar = findViewById(R.id.avatar);
        usernameText = findViewById(R.id.Username);
        SharedPreferences userPrefs = getSharedPreferences("USER_PREF", MODE_PRIVATE);
        String avatarUrl = userPrefs.getString("avatarUrl", null);
        String email = userPrefs.getString("Email", "No email");


        usernameText.setText(email);
        if (avatarUrl != null && !avatarUrl.isEmpty()) {

            Glide.with(this)
                    .load(Uri.parse(avatarUrl))
                    .placeholder(R.drawable.logo) // ảnh mặc định trong khi loading
                    .error(R.drawable.logo)       // ảnh mặc định nếu lỗi
                    .into(avatar);
        } else {
            avatar.setImageResource(R.drawable.logo);
        }
        uploadAvatar.setOnClickListener(v ->
                startActivity(new Intent(ProfileMain.this, UploadAvatarActivity.class)));

        confirmEmail.setOnClickListener(v ->
                startActivity(new Intent(ProfileMain.this, ConfirmEmailActivity.class)));

        changeProfile.setOnClickListener(v ->
                startActivity(new Intent(ProfileMain.this, ChangeProfileActivity.class)));

        notification.setOnClickListener(v ->
                startActivity(new Intent(ProfileMain.this, NotificationActivity.class)));

        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("USER_PREF", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();
            Intent intent = new Intent(ProfileMain.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnRedirectHome.setOnClickListener(v ->
                startActivity(new Intent(ProfileMain.this, MainActivity.class)));
    }
    }
