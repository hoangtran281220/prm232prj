package com.example.prm232rj.ui.screen.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.prm232rj.databinding.ActivityComicDetailBinding;
import com.example.prm232rj.ui.viewmodel.ComicDetailViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ComicDetailActivity extends AppCompatActivity {
    private ActivityComicDetailBinding binding;
    private ComicDetailViewModel viewModel;
    private String comicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComicDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(ComicDetailViewModel.class);

        // Lấy comicId từ Intent
        comicId = getIntent().getStringExtra("COMIC_ID");

        if (comicId == null || comicId.trim().isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin truyện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup observers
        setupObservers();

        // Setup UI events
        setupUI();

        // Load dữ liệu
        viewModel.loadComic(comicId);
    }

    private void setupObservers() {
        // Observe comic data
        viewModel.getComic().observe(this, comic -> {
            if (comic != null) {
                binding.setComic(comic);
                binding.executePendingBindings(); // Đảm bảo binding được thực thi ngay lập tức
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (binding.progressBar != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }

            // Có thể thêm logic disable/enable UI khi loading
            binding.getRoot().setEnabled(!isLoading);
        });

        // Observe error
        viewModel.getError().observe(this, error -> {
            if (error != null && !error.trim().isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();

                // Có thể thêm logic hiển thị error state UI
                // hoặc finish activity nếu lỗi nghiêm trọng
            }
        });
    }

    private void setupUI() {
        // Setup toolbar nếu có
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết truyện");
        }

        // Có thể thêm các sự kiện UI khác như:
        // - Button đọc truyện
        // - Button yêu thích
        // - Swipe to refresh
        // - etc.
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}