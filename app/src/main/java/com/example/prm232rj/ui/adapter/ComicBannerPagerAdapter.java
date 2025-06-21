package com.example.prm232rj.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm232rj.data.dto.ComicDtoBanner;
import com.example.prm232rj.databinding.ItemComicsBannerBinding;

import java.util.ArrayList;
import java.util.List;

public class ComicBannerPagerAdapter extends RecyclerView.Adapter<ComicBannerPagerAdapter.BannerViewHolder> {
    private List<ComicDtoBanner> bannerList = new ArrayList<>();

    public void setData(List<ComicDtoBanner> list) {
        this.bannerList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemComicsBannerBinding binding = ItemComicsBannerBinding.inflate(inflater, parent, false);
        return new BannerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ComicBannerPagerAdapter.BannerViewHolder holder, int position) {
        ComicDtoBanner item = bannerList.get(position);
        holder.binding.setItem(item);
        holder.binding.executePendingBindings();
//
//        Glide.with(holder.binding.getRoot().getContext())
//                .load(item.getCoverImage())
//                .into(holder.binding.imageBanner);
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ItemComicsBannerBinding binding;

        public BannerViewHolder(ItemComicsBannerBinding  binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
