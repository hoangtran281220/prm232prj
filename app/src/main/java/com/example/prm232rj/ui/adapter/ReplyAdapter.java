package com.example.prm232rj.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm232rj.data.model.Reply;
import com.example.prm232rj.databinding.ItemReplyBinding;

import java.util.ArrayList;
import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {
    private List<Reply> replies;

    public ReplyAdapter(List<Reply> replies) {
        this.replies = replies;
    }

    public void setReplies(List<Reply> newReplies) {
        this.replies = newReplies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReplyBinding binding = ItemReplyBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ReplyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        holder.binding.setReply(replies.get(position));
    }

    @Override
    public int getItemCount() {
        return replies.size();
    }

    static class ReplyViewHolder extends RecyclerView.ViewHolder {
        ItemReplyBinding binding;

        public ReplyViewHolder(ItemReplyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

