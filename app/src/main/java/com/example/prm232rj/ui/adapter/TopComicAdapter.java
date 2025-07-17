package com.example.prm232rj.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm232rj.data.interfaces.IComicPreview;
import com.example.prm232rj.databinding.ItemComicTopBinding;
import com.example.prm232rj.databinding.ItemTopComicPageBinding;
import com.example.prm232rj.ui.screen.Activities.ComicDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class TopComicAdapter extends RecyclerView.Adapter<TopComicAdapter.PageViewHolder> {
    private final List<List<IComicPreview>> pages = new ArrayList<>();
    private int itemsPerPage;

    public TopComicAdapter(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public void setData(List<IComicPreview> data) {
        pages.clear();
        if (data != null) {
            for (int i = 0; i < data.size(); i += itemsPerPage) {
                int end = Math.min(i + itemsPerPage, data.size());
                pages.add(data.subList(i, end));
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemTopComicPageBinding binding = ItemTopComicPageBinding.inflate(inflater, parent, false);
        return new PageViewHolder(binding, itemsPerPage);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        holder.bind(pages.get(position));

    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        private final ItemTopComicPageBinding binding;
        private final int itemsPerPage;

        public PageViewHolder(ItemTopComicPageBinding binding, int itemsPerPage) {
            super(binding.getRoot());
            this.binding = binding;
            this.itemsPerPage = itemsPerPage;
        }

        public void bind(List<IComicPreview> pageData) {
            LayoutInflater inflater = LayoutInflater.from(binding.getRoot().getContext());
            binding.linearContainer.removeAllViews();

            for (int i = 0; i < pageData.size(); i++) {
                IComicPreview item = pageData.get(i);
                int index = getBindingAdapterPosition() * itemsPerPage + i;

                ItemComicTopBinding itemBinding = ItemComicTopBinding.inflate(inflater, binding.linearContainer, false);
                itemBinding.setItem(item);
                itemBinding.setIndex(index);
                itemBinding.executePendingBindings();
                itemBinding.getRoot().setOnClickListener(v -> {
                    Intent intent = new Intent(v.getContext(), ComicDetailActivity.class);
                    intent.putExtra("COMIC_ID", item.getId()); // Truyền ID của truyện
                    v.getContext().startActivity(intent);
                });
                binding.linearContainer.addView(itemBinding.getRoot());
            }
        }
    }
}

