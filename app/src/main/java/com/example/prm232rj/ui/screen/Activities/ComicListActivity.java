package com.example.prm232rj.ui.screen.Activities;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private boolean isLoading = false;
    private boolean isLastPage = false;
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
        binding.recyclerViewComics.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(androidx.recyclerview.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy <= 0) return; // chỉ xử lý khi cuộn xuống

                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // Điều kiện gọi trang tiếp theo
                if (!isLoading && !isLastPage &&
                        (visibleItemCount + firstVisibleItemPosition >= totalItemCount)
                ) {
                    isLoading = true;

                    Log.d("paging", "Load next page...");
                    viewModel.loadNextPageForTagList();
                }
            }
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ComicViewModel.class);

        String tagKey = (tagId != null && !tagId.trim().isEmpty()) ? tagId : "fallback";
        // Quan sát trước
        viewModel.getComicsForList(tagKey).observe(this, comics -> {
            adapter.setData(comics);
            isLoading = false;
        });

        viewModel.loadComicsByTagForList(tagKey);
    }


    private void loadData() {
        binding.setTitle(Objects.requireNonNullElse(tagName, "Danh sách truyện"));
        if (tagId != null && !tagId.trim().isEmpty()) {
            viewModel.loadComicsByTagForList(tagId);
        } else {
            viewModel.loadComicsByTagForList("");
        }
    }

}