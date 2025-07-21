package com.example.prm232rj.ui.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.prm232rj.data.room.ReadHistoryEntity;
import com.example.prm232rj.databinding.ItemHistoryReadingBinding;

import java.util.List;

public class ReadingHistoryAdapter extends RecyclerView.Adapter<ReadingHistoryAdapter.HistoryViewHolder> {

    private List<ReadHistoryEntity> histories;
    private final OnHistoryClickListener listener;

    public interface OnHistoryClickListener {
        void onClick(ReadHistoryEntity item);
    }

    public ReadingHistoryAdapter(List<ReadHistoryEntity> histories, OnHistoryClickListener listener) {
        this.histories = histories;
        this.listener = listener;
    }

    public void setData(List<ReadHistoryEntity> newData) {
        this.histories = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryReadingBinding binding = ItemHistoryReadingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new HistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        ReadHistoryEntity item = histories.get(position);
        holder.binding.setHistory(item);
        holder.binding.getRoot().setOnClickListener(v -> listener.onClick(item));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return histories != null ? histories.size() : 0;
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        final ItemHistoryReadingBinding binding;

        public HistoryViewHolder(ItemHistoryReadingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
