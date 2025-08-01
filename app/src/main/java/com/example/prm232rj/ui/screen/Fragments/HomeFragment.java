package com.example.prm232rj.ui.screen.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.prm232rj.data.interfaces.IComicPreview;
import com.example.prm232rj.databinding.FragmentHomeBinding;
import com.example.prm232rj.ui.adapter.section.HomeSectionAdapter;
import com.example.prm232rj.ui.adapter.section.HomeSectionItem;
import com.example.prm232rj.ui.adapter.section.SectionViewType;
import com.example.prm232rj.ui.viewmodel.ComicViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private HomeSectionAdapter adapter;



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
        setupRecycler();

        view.post(() -> {
            viewModel.loadBanners(requireActivity());
            viewModel.loadComicsTop3(getActivity());
            viewModel.loadComicsByTagForHome(getActivity(),"4");
            viewModel.loadComicsByTagForHome(getActivity(),"2");
            viewModel.loadComicsByTagForHome(getActivity(),"10");
            viewModel.loadComicsByTagForHome(getActivity(),"14");
            viewModel.loadComicsByTagForHome(getActivity(),"5");
            viewModel.loadComicsByTagForHome(getActivity(),"13");
        });
    }

    private void setupRecycler() {
        List<HomeSectionItem> sectionList = new ArrayList<>();

        sectionList.add(new HomeSectionItem(SectionViewType.BANNER));

        sectionList.add(new HomeSectionItem(SectionViewType.SECTION_HEADER) {{
            sectionTitle = "Truyện Hot"; sectionTag = "hot";
        }});

        sectionList.add(new HomeSectionItem(SectionViewType.COMIC_LIST) {{
            sectionTag = "hot";
        }});

        sectionList.add(new HomeSectionItem(SectionViewType.SECTION_HEADER) {{
            sectionTitle = "Manga Phiêu lưu"; sectionTag = "adventure";
        }});

        sectionList.add(new HomeSectionItem(SectionViewType.COMIC_LIST) {{
            sectionTag = "adventure";
        }});

        sectionList.add(new HomeSectionItem(SectionViewType.SECTION_HEADER) {{
            sectionTitle = "Truyện cười"; sectionTag = "comedic";
        }});

        sectionList.add(new HomeSectionItem(SectionViewType.COMIC_LIST) {{
            sectionTag = "comedic";
        }});

        sectionList.add(new HomeSectionItem(SectionViewType.SECTION_HEADER) {{
            sectionTitle = "Xuyên không"; sectionTag = "Xuyên không";
        }});

        sectionList.add(new HomeSectionItem(SectionViewType.COMIC_LIST) {{
            sectionTag = "Xuyên không";
        }});

        sectionList.add(new HomeSectionItem(SectionViewType.SECTION_HEADER) {{
            sectionTitle = "Truyện Trung"; sectionTag = "Manhua";
        }});

        sectionList.add(new HomeSectionItem(SectionViewType.COMIC_LIST) {{
            sectionTag = "Manhua";
        }});

        sectionList.add(new HomeSectionItem(SectionViewType.SECTION_HEADER) {{
            sectionTitle = "Truyện Hàn"; sectionTag = "Manhwa";
        }});

        sectionList.add(new HomeSectionItem(SectionViewType.COMIC_LIST) {{
            sectionTag = "Manhwa";
        }});

        sectionList.add(new HomeSectionItem(SectionViewType.SECTION_HEADER) {{
            sectionTitle = "Fantasy"; sectionTag = "Fantasy";
        }});

        sectionList.add(new HomeSectionItem(SectionViewType.COMIC_LIST) {{
            sectionTag = "Fantasy";
        }});







        adapter = new HomeSectionAdapter(sectionList);
        binding.homeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.homeRecyclerView.setAdapter(adapter);

        viewModel.getBanners().observe(getViewLifecycleOwner(), banners ->
                adapter.updateBannerSection(banners)
        );

        viewModel.getComicsForHome("4").observe(getViewLifecycleOwner(), comics -> {
            adapter.updateComicSection("comedic", new ArrayList<>(comics));
        });

        viewModel.getComicsForHome("2").observe(getViewLifecycleOwner(), comics -> {
            adapter.updateComicSection("adventure", new ArrayList<>(comics));
        });

        viewModel.getComicsTop3().observe(getViewLifecycleOwner(), previews ->{
            adapter.updateComicSection("hot", new ArrayList<>(previews));
        });

        viewModel.getComicsForHome("10").observe(getViewLifecycleOwner(), comics -> {
            adapter.updateComicSection("Xuyên không", new ArrayList<>(comics));
        });

        viewModel.getComicsForHome("14").observe(getViewLifecycleOwner(), comics -> {
            adapter.updateComicSection("Manhua", new ArrayList<>(comics));
        });

        viewModel.getComicsForHome("5").observe(getViewLifecycleOwner(), comics -> {
            adapter.updateComicSection("Fantasy", new ArrayList<>(comics));
        });

        viewModel.getComicsForHome("13").observe(getViewLifecycleOwner(), comics -> {
            adapter.updateComicSection("Manhwa", new ArrayList<>(comics));
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = requireContext().getSharedPreferences("APP_PREF", Context.MODE_PRIVATE);
        boolean shouldRefresh = prefs.getBoolean("should_refresh_home", false);

        if (shouldRefresh) {
            // ✅ Gọi lại các hàm load dữ liệu
            viewModel.loadBanners(requireActivity());
            viewModel.loadComicsTop3(requireActivity());
            viewModel.loadComicsByTagForHome(requireActivity(), "4");
            viewModel.loadComicsByTagForHome(requireActivity(), "2");
            viewModel.loadComicsByTagForHome(requireActivity(), "10");
            viewModel.loadComicsByTagForHome(requireActivity(), "14");
            viewModel.loadComicsByTagForHome(requireActivity(), "5");
            viewModel.loadComicsByTagForHome(requireActivity(), "13");

            // ❌ Xóa flag sau khi xử lý
            prefs.edit().remove("should_refresh_home").apply();
        }
    }


}