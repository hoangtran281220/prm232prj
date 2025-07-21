package com.example.prm232rj.ui.screen.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.prm232rj.R;
import com.example.prm232rj.databinding.FragmentComicsFavoriteBinding;
import com.example.prm232rj.ui.adapter.ComicPreviewAdapter;
import com.example.prm232rj.ui.screen.Activities.ComicDetailActivity;
import com.example.prm232rj.ui.screen.Activities.LoginActivity;
import com.example.prm232rj.ui.viewmodel.FollowedComicsViewModel;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.HiltAndroidApp;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComicsFavoriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class ComicsFavoriteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentComicsFavoriteBinding binding;
    private FollowedComicsViewModel followViewModel;
    private ComicPreviewAdapter adapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ComicsFavoriteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComicsFavoriteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComicsFavoriteFragment newInstance(String param1, String param2) {
        ComicsFavoriteFragment fragment = new ComicsFavoriteFragment();
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
        binding = FragmentComicsFavoriteBinding.inflate(inflater, container, false);
        SharedPreferences prefs = requireActivity().getSharedPreferences("USER_PREF", Context.MODE_PRIVATE);
        String uid = prefs.getString("uid", null);

        if (uid == null) {
            // Chưa đăng nhập
            binding.layoutRequireLogin.setVisibility(View.VISIBLE);
            binding.recyclerFollowed.setVisibility(View.GONE);

            binding.btnLogin.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);
            });
        } else {
            // Đã đăng nhập
            followViewModel = new ViewModelProvider(this).get(FollowedComicsViewModel.class);

            // Init adapter và layout
            adapter = new ComicPreviewAdapter(new ArrayList<>());

            binding.recyclerFollowed.setLayoutManager(new GridLayoutManager(requireContext(), 3));
            binding.recyclerFollowed.setAdapter(adapter);

            // Observe realtime danh sách follow
            followViewModel.observeFollowedComics(uid);

            // Lắng nghe dữ liệu
            followViewModel.getFollowedComics().observe(getViewLifecycleOwner(), comics -> {
                if (comics != null && !comics.isEmpty()) {
                    adapter.setData(comics);
                    binding.tvEmpty.setVisibility(View.GONE);
                    binding.recyclerFollowed.setVisibility(View.VISIBLE);
                } else {
                    Log.d("mytagt","kco");
                    adapter.setData(new ArrayList<>());
                    binding.tvEmpty.setVisibility(View.VISIBLE); // Hiển thị "Không có truyện theo dõi"
                }
            });


            // TODO: load danh sách truyện theo dõi
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}