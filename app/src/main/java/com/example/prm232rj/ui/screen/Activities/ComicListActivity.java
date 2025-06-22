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

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ComicListActivity extends AppCompatActivity {
    public static final String EXTRA_TAG_ID = "TAG_ID";
    public static final String EXTRA_TAG_NAME = "TAG_NAME"; // Optional
    private ActivityComicListBinding binding;
    private ComicPreviewAdapter adapter;
    private ComicViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityComicListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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

        viewModel.getComics().observe(this, comics -> {
            adapter.setData(comics);
        });
    }

    private void loadData() {
        String tagId = getIntent().getStringExtra(EXTRA_TAG_ID);
        String tagName = getIntent().getStringExtra(EXTRA_TAG_NAME); // Optional


        if (tagName != null) {
            binding.setTitle(tagName);
        } else {
            binding.setTitle("Danh sách truyện");
        }
        Log.d("mytagt","id: " + tagId);
        Log.d("mytagt","name: "+tagName);
        if (tagId != null && !tagId.trim().isEmpty()) {
            viewModel.loadComicsByTags(Collections.singletonList(tagId));
        } else {
            viewModel.loadComicsByTags(Collections.emptyList());
        }    }

}