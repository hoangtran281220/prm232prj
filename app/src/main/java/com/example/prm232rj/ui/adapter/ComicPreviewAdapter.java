package com.example.prm232rj.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm232rj.data.dto.ComicDtoPreview;
import com.example.prm232rj.data.interfaces.IComicPreview;
import com.example.prm232rj.databinding.ItemComicPreviewBinding;

import java.util.ArrayList;
import java.util.List;

public class ComicPreviewAdapter extends RecyclerView.Adapter<ComicPreviewAdapter.PreviewViewHolder> {

    private List<IComicPreview> previewList = new ArrayList<>();

    public ComicPreviewAdapter(List<IComicPreview> previewList) {
        this.previewList = previewList;
    }

    public void setData(List<? extends IComicPreview> list) {
        this.previewList = new ArrayList<>(list);
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
        IComicPreview item = previewList.get(position);
        holder.binding.setItem(item);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        Log.d("mytag","size: " + previewList.size());
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
