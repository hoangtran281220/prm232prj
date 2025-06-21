package com.example.prm232rj.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm232rj.data.dto.ComicDtoPreview;
import com.example.prm232rj.databinding.ItemComicPreviewBinding;

import java.util.ArrayList;
import java.util.List;

public class ComicPreviewAdapter extends RecyclerView.Adapter<ComicPreviewAdapter.PreviewViewHolder> {

    private List<ComicDtoPreview> previewList = new ArrayList<>();

    public ComicPreviewAdapter(List<ComicDtoPreview> previewList) {
        this.previewList = previewList;
    }

    public void setData(List<ComicDtoPreview> list) {
        this.previewList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemComicPreviewBinding binding = ItemComicPreviewBinding.inflate(inflater, parent, false);
        return new PreviewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewViewHolder holder, int position) {
        ComicDtoPreview item = previewList.get(position);
        holder.binding.setItem(item);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return previewList.size();
    }

    static class PreviewViewHolder extends RecyclerView.ViewHolder {
        ItemComicPreviewBinding binding;

        public PreviewViewHolder(ItemComicPreviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
