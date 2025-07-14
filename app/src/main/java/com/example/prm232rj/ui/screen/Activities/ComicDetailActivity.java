package com.example.prm232rj.ui.screen.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.prm232rj.data.room.ReadHistoryEntity;
import com.example.prm232rj.databinding.ActivityComicDetailBinding;
import com.example.prm232rj.ui.adapter.ChapterAdapter;
import com.example.prm232rj.ui.viewmodel.ComicDetailViewModel;
import com.example.prm232rj.ui.viewmodel.FollowedComicsViewModel;
import com.example.prm232rj.ui.viewmodel.ReadHistoryViewModel;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ComicDetailActivity extends AppCompatActivity {
    private ActivityComicDetailBinding binding;
    private ComicDetailViewModel viewModel;
    private String comicId;
    private ChapterAdapter chapterAdapter;
    private FollowedComicsViewModel followViewModel;
    private boolean isFollowed = false;

    private ReadHistoryViewModel historyViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComicDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(ComicDetailViewModel.class);
        historyViewModel = new ViewModelProvider(this).get(ReadHistoryViewModel.class);
        // Lấy comicId từ Intent
        comicId = getIntent().getStringExtra("COMIC_ID");

        if (comicId == null || comicId.trim().isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin truyện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        followViewModel = new ViewModelProvider(this).get(FollowedComicsViewModel.class);

        // Gọi quan sát truyện đã theo dõi hay chưa
        SharedPreferences prefs = getSharedPreferences("USER_PREF", MODE_PRIVATE);
        String uid = prefs.getString("uid", null);
        if (uid != null) {
            followViewModel.observeFollowedComics(uid);
        }
        // Setup observers
        setupObservers();

        // Setup UI events
        setupUI();
        chapterAdapter = new ChapterAdapter(new ArrayList<>(), chapter -> {
            // Xử lý khi nhấn vào chapter
            Intent intent = new Intent(ComicDetailActivity.this, ChapterActivity.class);
            intent.putExtra("COMIC_ID", comicId);
            intent.putExtra("CHAPTER_ID", chapter.getId());
            ReadHistoryEntity entity = new ReadHistoryEntity();
            entity.comicId = comicId;
            entity.comicTitle = binding.getComic().getTitle();
            entity.comicCoverUrl = binding.getComic().getCoverImage();
            entity.chapterReading = chapter.getChapterNumber();
            entity.lastReadAt = System.currentTimeMillis();
            entity.chapterId = chapter.getId();
            historyViewModel.saveHistory(entity);
            startActivity(intent);
        });
        binding.recyclerChapters.setAdapter(chapterAdapter); // ← Gán adapter ở đây
        binding.recyclerChapters.setLayoutManager(new LinearLayoutManager(this));
        // Load dữ liệu
        viewModel.loadComic(comicId);
        viewModel.loadChapters(comicId);
    }

    private void setupObservers() {
        followViewModel.getFollowedComics().observe(this, followedList -> {
            if (followedList != null && !followedList.isEmpty()) {
                boolean isFollowed = false;
                for (var comic : followedList) {
                    if (comic.getId().equals(comicId)) {
                        isFollowed = true;
                        break;
                    }
                }
                updateFavoriteUI(isFollowed);
            } else {
                updateFavoriteUI(false);
            }
        });

        followViewModel.getFollowSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Đã thêm vào danh sách theo dõi!", Toast.LENGTH_SHORT).show();
            }
        });
        followViewModel.getUnfollowSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Đã xoá khỏi danh sách theo dõi!", Toast.LENGTH_SHORT).show();
            }
        });

        // Observe comic data
        viewModel.getComic().observe(this, comic -> {
            if (comic != null) {
                binding.setComic(comic);
                binding.executePendingBindings(); // Đảm bảo binding được thực thi ngay lập tức

                // Gọi fetch tác giả & thể loại ở đây
                viewModel.fetchAuthorAndTags(comic);
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

        viewModel.getChapters().observe(this, chapters -> {
            if (chapters != null) {
                chapterAdapter.updateChapters(chapters); // Cập nhật dữ liệu cho adapter hiện tại
                binding.tvChapterCount.setText(chapters.size() + " chương"); // Cập nhật số chương

            }
        });
        //load tác giả
        viewModel.getAuthorNames().observe(this, authors -> {
            binding.textAuthors.setText(authors);
        });
        //load tag
        viewModel.getTagNames().observe(this, tags -> {
            binding.textTags.setText(tags);
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
        binding.btnFavorite.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("USER_PREF", MODE_PRIVATE);
            String uid = prefs.getString("uid", null);

            if (uid == null) {
                showLoginRequiredDialog(); // ← Nếu chưa đăng nhập
            } else {
                // TODO: Xử lý thêm/trừ truyện vào danh sách follow
                if (isFollowed) {
                    followViewModel.unfollowComic(uid, comicId);
                } else {
                    followViewModel.followComic(uid, comicId);
                }            }
        });
        // - Swipe to refresh
        // - etc.
    }

    private void updateFavoriteUI(boolean followed) {
        isFollowed = followed;
        if (followed) {
            binding.btnFavorite.setText("Đã yêu thích ❤️");
        } else {
            binding.btnFavorite.setText("Yêu thích");
        }
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
    private void showLoginRequiredDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Yêu cầu đăng nhập")
                .setMessage("Tính năng này yêu cầu đăng nhập.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

}