package com.example.prm232rj.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm232rj.data.model.Reply;
import com.example.prm232rj.databinding.ItemReplyBinding;
import com.example.prm232rj.ui.screen.Dialogs.ReplyInputDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {
    private List<Reply> replies;
    private String chapterId;
    private String commentId;
    private Fragment parentFragment;

    public void setContextInfo(Fragment parentFragment, String chapterId, String commentId) {
        this.chapterId = chapterId;
        this.commentId = commentId;
        this.parentFragment = parentFragment;
    }
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
        holder.binding.btnReplyInReply.setOnClickListener(v -> {
            Reply reply = replies.get(position);

            ReplyInputDialogFragment dialog = new ReplyInputDialogFragment(
                    commentId,               // conversationId = comment gốc
                    reply.getId(),           // replyId đang được reply
                    reply.getUserId(),       // userReplyId = người bị reply
                    reply.getUserName(),     // tên người bị reply
                    chapterId
            );

            dialog.show(parentFragment.getParentFragmentManager(), "ReplyInputDialog");
        });
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

