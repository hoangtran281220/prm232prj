package com.example.prm232rj.ui.screen.Activities;

import android.os.Bundle;
import android.view.Menu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm232rj.R;
import com.example.prm232rj.databinding.ActivityHomeBinding;
import com.example.prm232rj.ui.adapter.MainPagerAdapter;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private MainPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new MainPagerAdapter(getSupportFragmentManager(), getLifecycle());
        binding.viewPager.setAdapter(adapter);

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