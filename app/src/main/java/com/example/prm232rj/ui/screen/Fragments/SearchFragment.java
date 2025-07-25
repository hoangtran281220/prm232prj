package com.example.prm232rj.ui.screen.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import com.example.prm232rj.R;
import com.example.prm232rj.databinding.FragmentSearchBinding;
import com.example.prm232rj.ui.adapter.ComicPreviewAdapter;
import com.example.prm232rj.ui.screen.Dialogs.FilterComicDialogFragment;
import com.example.prm232rj.ui.viewmodel.ComicViewModel;
import com.example.prm232rj.utils.FilterPrefManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ComicViewModel comicViewModel;
    private FilterPrefManager filterPrefManager;
    private boolean isLoading = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ComicPreviewAdapter adapter;

    private ComicPreviewAdapter searchResultAdapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbarSearch.setTitle("Khám phá");
        comicViewModel = new ViewModelProvider(this).get(ComicViewModel.class);
        filterPrefManager = new FilterPrefManager(requireContext());

        // Adapter cho kết quả tìm kiếm nổi
        searchResultAdapter = new ComicPreviewAdapter(Collections.emptyList());
        binding.rvSearchResult.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.rvSearchResult.setAdapter(searchResultAdapter);

        // set up adapter chính
        adapter = new ComicPreviewAdapter(Collections.emptyList());
        binding.recyclerViewSearch.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.recyclerViewSearch.setAdapter(adapter);
        binding.recyclerViewSearch.setHasFixedSize(true);

        // load khởi đầu
        List<String> savedTagIds = new ArrayList<>(filterPrefManager.getSavedTagIds());
        String savedSort = filterPrefManager.getSavedSort();
        String savedStatus = filterPrefManager.getSavedStatus();
        comicViewModel.loadFilteredComics(savedTagIds, savedStatus, savedSort);

        comicViewModel.getFilteredComics().observe(getViewLifecycleOwner(), comics -> {
            adapter.setData(comics);
            isLoading = false;
        });

        binding.recyclerViewSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy <= 0 || isLoading) return;
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + firstVisibleItemPosition >= totalItemCount)
                        && firstVisibleItemPosition >= 0 && totalItemCount >= 12) {

                    isLoading = true;
                    List<String> tagIds = new ArrayList<>(filterPrefManager.getSavedTagIds());
                    String sort = filterPrefManager.getSavedSort();
                    String status = filterPrefManager.getSavedStatus();

                    comicViewModel.loadNextPageFiltered(tagIds, status, sort);
                }
            }
        });

        // ✅ LẤY SearchView TỪ MENU NGAY KHI GIAO DIỆN TẠO XONG
        MenuItem searchItem = binding.toolbarSearch.getMenu().findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Nhập tên truyện...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("mytest", "submit: " + query);
                comicViewModel.searchComicsByTitleContains(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("mytest", "change: " + newText);
                if (newText.isEmpty()) {
                    binding.rvSearchResult.setVisibility(View.GONE);
                    binding.tvNoResult.setVisibility(View.GONE);
                } else {
                    comicViewModel.searchComicsByTitleContains(newText);
                }
                return true;
            }
        });

        // ✅ Chỉ xử lý FILTER ở MenuClick
        binding.toolbarSearch.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_filter) {
                FilterComicDialogFragment dialog = new FilterComicDialogFragment(
                        (tagIds, sort, status) -> comicViewModel.loadFilteredComics(tagIds, status, sort)
                );
                dialog.show(getChildFragmentManager(), "FilterDialog");
                return true;
            }
            return false;
        });

        // Kết quả tìm kiếm realtime
        comicViewModel.getSearchResults().observe(getViewLifecycleOwner(), comics -> {
            if (comics.isEmpty()) {
                binding.rvSearchResult.setVisibility(View.GONE);
                binding.tvNoResult.setVisibility(View.VISIBLE);
            } else {
                binding.rvSearchResult.setVisibility(View.VISIBLE);
                binding.tvNoResult.setVisibility(View.GONE);
                searchResultAdapter.setData(comics);
            }
        });
    }
}