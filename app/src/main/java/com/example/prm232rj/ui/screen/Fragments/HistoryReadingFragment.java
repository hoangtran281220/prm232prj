package com.example.prm232rj.ui.screen.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.prm232rj.R;
import com.example.prm232rj.databinding.FragmentHistoryReadingBinding;
import com.example.prm232rj.ui.adapter.ReadingHistoryAdapter;
import com.example.prm232rj.ui.screen.Activities.ChapterActivity;
import com.example.prm232rj.ui.viewmodel.ReadHistoryViewModel;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryReadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class HistoryReadingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentHistoryReadingBinding binding;
    private ReadHistoryViewModel viewModel;
    private ReadingHistoryAdapter adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoryReadingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryReadingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryReadingFragment newInstance(String param1, String param2) {
        HistoryReadingFragment fragment = new HistoryReadingFragment();
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
        binding = FragmentHistoryReadingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ReadingHistoryAdapter(new ArrayList<>(), history -> {
            // Xử lý khi click item history (ví dụ mở lại ChapterActivity)
            Intent intent = new Intent(requireContext(), ChapterActivity.class);
            intent.putExtra("COMIC_ID", history.comicId);
            intent.putExtra("CHAPTER_ID", history.chapterId); // nếu chapterReading là int ID
            startActivity(intent);
        });

        binding.recyclerHistory.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ReadHistoryViewModel.class);
        viewModel.getHistories().observe(getViewLifecycleOwner(), list -> {
            adapter.setData(list);
        });
    }
}