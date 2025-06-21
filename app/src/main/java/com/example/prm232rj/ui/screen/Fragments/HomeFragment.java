package com.example.prm232rj.ui.screen.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.prm232rj.R;
import com.example.prm232rj.databinding.FragmentHomeBinding;
import com.example.prm232rj.ui.adapter.ComicBannerPagerAdapter;
import com.example.prm232rj.ui.adapter.ComicPreviewAdapter;
import com.example.prm232rj.ui.screen.Activities.ComicListActivity;
import com.example.prm232rj.ui.viewmodel.ComicViewModel;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FragmentHomeBinding binding;
    private ComicViewModel viewModel;

    private ComicBannerPagerAdapter bannerAdapter;
    private ComicPreviewAdapter previewAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ComicViewModel.class);

        setupBanner();
        setupPreview();

        observeViewModel();
        binding.btnSeeAllHot.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ComicListActivity.class);
            intent.putExtra("TAG_ID", "4"); // ví dụ: 8 = "Kinh Dị"
            intent.putExtra("TAG_NAME", "Hành Động");
            startActivity(intent);
        });
    }

    private void setupBanner() {
        bannerAdapter = new ComicBannerPagerAdapter();
        binding.bannerViewPager.setAdapter(bannerAdapter);
        binding.bannerDots.setViewPager2(binding.bannerViewPager);

        // Chặn ViewPager2 cha intercept gesture vuốt ngang
        binding.bannerViewPager.getChildAt(0).setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });
    }

    private void setupPreview() {
        previewAdapter = new ComicPreviewAdapter(new ArrayList<>());
        binding.latestComicsRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.latestComicsRecyclerView.setAdapter(previewAdapter);
    }

    private void observeViewModel() {
        viewModel.getBanners().observe(getViewLifecycleOwner(), banners -> {
            bannerAdapter.setData(banners);
        });

        viewModel.getPreviews().observe(getViewLifecycleOwner(), previews -> {
            previewAdapter.setData(previews);
        });

        viewModel.loadBanners();  // trigger fetch
        viewModel.loadPreviews(); // trigger fetch
    }
}