package com.example.prm232rj.ui.screen.Activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.prm232rj.R;
import com.example.prm232rj.data.dto.ComicDtoPreview;
import com.example.prm232rj.databinding.ActivityComicListBinding;
import com.example.prm232rj.ui.adapter.ComicPreviewAdapter;
import com.example.prm232rj.ui.viewmodel.ComicListViewModel;

import java.util.Collections;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ComicListActivity extends AppCompatActivity {
    public static final String EXTRA_TAG_ID = "TAG_ID";
    public static final String EXTRA_TAG_NAME = "TAG_NAME"; // Optional
    private ActivityComicListBinding binding;
    private ComicPreviewAdapter adapter;
    private ComicListViewModel viewModel;

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
        viewModel = new ViewModelProvider(this).get(ComicListViewModel.class);

        viewModel.getComics().observe(this, comics -> {
            adapter.setData(comics);
        });
    }

    private void loadData() {
        String tagId = getIntent().getStringExtra(EXTRA_TAG_ID);
        String tagName = getIntent().getStringExtra(EXTRA_TAG_NAME); // Optional

        if (tagId == null || tagId.isEmpty()) {
            Toast.makeText(this, "Thiếu tagId", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (tagName != null) {
            binding.setTitle(tagName);
        } else {
            binding.setTitle("Danh sách truyện");
        }

        viewModel.loadComicsByTags(Collections.singletonList(tagId));
    }

}