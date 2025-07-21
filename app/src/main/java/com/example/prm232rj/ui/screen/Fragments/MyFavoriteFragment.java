package com.example.prm232rj.ui.screen.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.prm232rj.R;
import com.example.prm232rj.databinding.FragmentMyFavoriteBinding;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyFavoriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFavoriteFragment extends Fragment {
    private FragmentMyFavoriteBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyFavoriteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyFavoriteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyFavoriteFragment newInstance(String param1, String param2) {
        MyFavoriteFragment fragment = new MyFavoriteFragment();
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
        binding = FragmentMyFavoriteBinding.inflate(inflater, container, false);
        replaceFragment(new HistoryReadingFragment());
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Đang đọc"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Theo dõi"));
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0)
                    replaceFragment(new HistoryReadingFragment());
                else
                    replaceFragment(new ComicsFavoriteFragment());
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        return binding.getRoot();
    }

    private void replaceFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}