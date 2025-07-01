package com.example.prm232rj.ui.screen.Activities;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.prm232rj.databinding.ActivityComicListBinding;
import com.example.prm232rj.ui.adapter.ComicPreviewAdapter;
import com.example.prm232rj.ui.viewmodel.ComicViewModel;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ComicListActivity extends AppCompatActivity {
    public static final String EXTRA_TAG_ID = "TAG_ID";
    public static final String EXTRA_TAG_NAME = "TAG_NAME"; // Optional
    private ActivityComicListBinding binding;
    private ComicPreviewAdapter adapter;
    private ComicViewModel viewModel;

    private String tagId;
    private String tagName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityComicListBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        tagId = getIntent().getStringExtra(EXTRA_TAG_ID);
        tagName = getIntent().getStringExtra(EXTRA_TAG_NAME);

        setupRecyclerView();
        setupViewModel();
        loadData();

    }

    private void setupRecyclerView() {
        adapter = new ComicPreviewAdapter(Collections.emptyList());
        binding.recyclerViewComics.setLayoutManager(new GridLayoutManager(this, 3));
        binding.recyclerViewComics.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ComicViewModel.class);

        Log.d("mykey", "tagid: " + tagId);
        String tagKey = (tagId != null && !tagId.trim().isEmpty()) ? tagId : "fallback";
        Log.d("mykey","tagKey:" + tagKey);
        // Quan sát trước
        viewModel.getComicsByTag(tagKey).observe(this, comics -> {
            Log.d("mykey", "Observed " + tagKey + " comics: " + (comics != null ? comics.size() : -1));
            adapter.setData(comics);
        });

        viewModel.loadComicsByTags(tagKey);
    }


    private void loadData() {
        binding.setTitle(Objects.requireNonNullElse(tagName, "Danh sách truyện"));
        if (tagId != null && !tagId.trim().isEmpty()) {
            viewModel.loadComicsByTags(tagId);
        } else {
            viewModel.loadComicsByTags("");
        }
    }

}