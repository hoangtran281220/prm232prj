package com.example.prm232rj.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm232rj.data.interfaces.IComicPreview;
import com.example.prm232rj.databinding.ItemComicTopBinding;

import java.util.List;

public class TopComicAdapter extends RecyclerView.Adapter<TopComicAdapter.TopComicViewHolder> {

    private List<IComicPreview> comics;

    public TopComicAdapter(List<IComicPreview> comics) {
        this.comics = comics;
    }

    public void setData(List<IComicPreview> newData) {
        this.comics = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TopComicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemComicTopBinding binding = ItemComicTopBinding.inflate(inflater, parent, false);
        return new TopComicViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TopComicViewHolder holder, int position) {
        IComicPreview item = comics.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return comics != null ? comics.size() : 0;
    }

    static class TopComicViewHolder extends RecyclerView.ViewHolder {
        private final ItemComicTopBinding binding;

        public TopComicViewHolder(ItemComicTopBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(IComicPreview item, int index) {
            binding.setItem(item);
            binding.setIndex(index);
            binding.executePendingBindings();
        }
    }
}

