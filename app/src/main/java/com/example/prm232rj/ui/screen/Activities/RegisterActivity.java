package com.example.prm232rj.ui.screen.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm232rj.R;
import com.example.prm232rj.databinding.ActivityRegisterBinding;
import com.example.prm232rj.ui.screen.Fragments.RegisterFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Gắn RegisterFragment vào container nếu lần đầu khởi tạo
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(binding.registerFragmentContainer.getId(), new RegisterFragment())
                    .commit();
        }
    }
}