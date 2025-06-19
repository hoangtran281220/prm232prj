package com.example.prm232rj.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.prm232rj.ui.screen.Fragments.AccountFragment;
import com.example.prm232rj.ui.screen.Fragments.HomeFragment;
import com.example.prm232rj.ui.screen.Fragments.MyFavoriteFragment;
import com.example.prm232rj.ui.screen.Fragments.SearchFragment;

import java.util.HashMap;
import java.util.Map;

public class MainPagerAdapter extends FragmentStateAdapter {
    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();
    public MainPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 1:
                fragment = new SearchFragment();
                break;
            case 2:
                fragment = new MyFavoriteFragment();
                break;
            case 3:
                fragment = new AccountFragment();
                break;
            default:
                fragment = new HomeFragment();
                break;
        }
        fragmentMap.put(position, fragment);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public Fragment getFragmentAtPosition(int position) {
        return fragmentMap.get(position);
    }
}
