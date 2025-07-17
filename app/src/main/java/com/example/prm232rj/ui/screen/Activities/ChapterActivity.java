package com.example.prm232rj.ui.screen.Activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.prm232rj.data.repository.ComicRepository;
import com.example.prm232rj.data.room.ReadHistoryEntity;
import com.example.prm232rj.databinding.ActivityChapterBinding;
import com.example.prm232rj.ui.adapter.ImageForChapterAdapter;
import com.example.prm232rj.ui.screen.Dialogs.CommentDialogFragment;
import com.example.prm232rj.ui.viewmodel.ChapterViewModel;
import com.example.prm232rj.ui.viewmodel.ReadHistoryViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChapterActivity extends AppCompatActivity {
    private ChapterViewModel viewModel;
    private ReadHistoryViewModel historyViewModel;
    private ImageForChapterAdapter imageAdapter;
    private ActivityChapterBinding binding;

    @Inject
    ComicRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChapterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.setCount(0); // Giá trị mặc định của commentCount nếu cần

        String comicId = getIntent().getStringExtra("COMIC_ID");
        String chapterId = getIntent().getStringExtra("CHAPTER_ID");

        imageAdapter = new ImageForChapterAdapter();
        binding.imageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.imageRecyclerView.setAdapter(imageAdapter);

        viewModel = new ViewModelProvider(this).get(ChapterViewModel.class);
        viewModel.init(comicId, chapterId);

        historyViewModel = new ViewModelProvider(this).get(ReadHistoryViewModel.class);

        viewModel.getCurrentChapter().observe(this, chapter -> {
            if (chapter != null) {
                binding.chapterTitle.setText(chapter.getChapterTitle());
                imageAdapter.setImageUrls(chapter.getContentImages());

                ReadHistoryEntity entity = new ReadHistoryEntity();
                entity.comicId = comicId;
                entity.chapterReading = chapter.getChapterNumber();
                entity.lastReadAt = System.currentTimeMillis();
                entity.chapterId = chapter.getChapterId();

                historyViewModel.saveHistory(entity);
            }
        });

        viewModel.getChapters().observe(this, chapters -> {
            boolean hasChapters = chapters != null && !chapters.isEmpty();
            binding.prevButton.setEnabled(hasChapters);
            binding.nextButton.setEnabled(hasChapters);
        });
        binding.imgComment.setOnClickListener(v -> {
            if (chapterId != null) {
                CommentDialogFragment dialog = new CommentDialogFragment(chapterId);
                dialog.show(getSupportFragmentManager(), "comment_dialog");
            }
        });
        binding.prevButton.setOnClickListener(v -> viewModel.loadPreviousChapter());
        binding.nextButton.setOnClickListener(v -> viewModel.loadNextChapter());
    }
}
