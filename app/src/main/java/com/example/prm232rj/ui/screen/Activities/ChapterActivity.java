package com.example.prm232rj.ui.screen.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm232rj.R;
import com.example.prm232rj.data.repository.ComicRepository;
import com.example.prm232rj.ui.adapter.ImageForChapterAdapter;
import com.example.prm232rj.ui.viewmodel.ChapterViewModel;
import com.example.prm232rj.data.dto.ChapterReadingDto;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChapterActivity extends AppCompatActivity {
    private ChapterViewModel viewModel;
    private ImageForChapterAdapter imageAdapter;
    private TextView chapterTitle;
    private Button prevButton;
    private Button nextButton;

    @Inject
    ComicRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        String comicId = getIntent().getStringExtra("COMIC_ID");
        String chapterId = getIntent().getStringExtra("CHAPTER_ID");

        chapterTitle = findViewById(R.id.chapter_title);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);

        RecyclerView recyclerView = findViewById(R.id.image_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageAdapter = new ImageForChapterAdapter();
        recyclerView.setAdapter(imageAdapter);

        viewModel = new ViewModelProvider(this).get(ChapterViewModel.class);
        viewModel.init(comicId, chapterId);

        viewModel.getCurrentChapter().observe(this, chapter -> {
            if (chapter != null) {
                chapterTitle.setText(chapter.getChapterTitle());
                imageAdapter.setImageUrls(chapter.getContentImages());
            }
        });

        viewModel.getChapters().observe(this, chapters -> {
            prevButton.setEnabled(chapters != null && !chapters.isEmpty());
            nextButton.setEnabled(chapters != null && !chapters.isEmpty());
        });

        prevButton.setOnClickListener(v -> viewModel.loadPreviousChapter());
        nextButton.setOnClickListener(v -> viewModel.loadNextChapter());
    }
}