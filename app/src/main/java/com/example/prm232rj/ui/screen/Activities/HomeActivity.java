package com.example.prm232rj.ui.screen.Activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.prm232rj.data.dto.NotificationDto;
import com.example.prm232rj.data.firebase.NotificationDataSource;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.prm232rj.R;
import com.example.prm232rj.databinding.ActivityHomeBinding;
import com.example.prm232rj.ui.adapter.MainPagerAdapter;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private MainPagerAdapter adapter;
    private NotificationDataSource notificationDataSource = new NotificationDataSource();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Yêu cầu quyền gửi thông báo nếu API >= 33 (Android 13)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1001);
            }
        }
            String userId = "7le7rdgAgcVFOuvWTQrSNMBCofI2";
            Log.d("Notification", "Gọi getNotifications()");


        adapter = new MainPagerAdapter(getSupportFragmentManager(), getLifecycle());
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setUserInputEnabled(false);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Trang chủ");
                    tab.setIcon(R.drawable.baseline_home_24);
                    break;
                case 1:
                    tab.setText("Khám phá");
                    tab.setIcon(R.drawable.baseline_manage_search_24);
                    break;
                case 2:
                    tab.setText("Tủ sách");
                    tab.setIcon(R.drawable.baseline_favorite_24);
                    break;
                case 3:
                    tab.setText("Tài khoản");
                    tab.setIcon(R.drawable.baseline_account_circle_24);
                    break;
            }
        }).attach();
    }

   

}