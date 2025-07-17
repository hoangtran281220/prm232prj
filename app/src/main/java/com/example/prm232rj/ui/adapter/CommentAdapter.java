package com.example.prm232rj.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm232rj.data.model.Comment;
import com.example.prm232rj.data.model.Reply;
import com.example.prm232rj.databinding.ItemCommentBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private final Map<String, List<Reply>> repliesMap = new HashMap<>();
    private final Map<String, Boolean> expandedReplies = new HashMap<>();

    // Callback để yêu cầu ViewModel load replies
    public interface OnExpandRepliesListener {
        void onExpandReplies(String commentId);
    }
    public void setData(List<Comment> newData) {
        this.commentList.clear();
        this.commentList.addAll(newData);
        notifyDataSetChanged();
    }
    private OnExpandRepliesListener listener;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public void setOnExpandRepliesListener(OnExpandRepliesListener listener) {
        this.listener = listener;
    }

    public void updateReplies(String commentId, List<Reply> replies) {
        repliesMap.put(commentId, replies);
        notifyItemChanged(getPositionByCommentId(commentId));
    }

    private int getPositionByCommentId(String commentId) {
        for (int i = 0; i < commentList.size(); i++) {
            if (commentList.get(i).getId().equals(commentId)) {
                return i;
            }
        }
        return -1;
    }



    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommentBinding binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.binding.setComment(comment);

        // Reply adapter
        ReplyAdapter replyAdapter = new ReplyAdapter(new ArrayList<>());
        holder.binding.recyclerReplies.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.binding.recyclerReplies.setAdapter(replyAdapter);
        holder.binding.recyclerReplies.setNestedScrollingEnabled(false);

        boolean isExpanded = expandedReplies.getOrDefault(comment.getId(), false);
        holder.binding.recyclerReplies.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        int count = repliesMap.containsKey(comment.getId())
                ? repliesMap.get(comment.getId()).size()
                : 0;
        holder.binding.setReplyCount(count);

        // Nếu đang mở và đã có data -> hiển thị
        if (isExpanded && repliesMap.containsKey(comment.getId())) {
            replyAdapter.setReplies(repliesMap.get(comment.getId()));
        }

        // Sự kiện click "Phản hồi"
        holder.binding.btnReply.setOnClickListener(v -> {
            boolean currentlyVisible = expandedReplies.getOrDefault(comment.getId(), false);
            expandedReplies.put(comment.getId(), !currentlyVisible);

            if (!currentlyVisible && listener != null) {
                listener.onExpandReplies(comment.getId()); // ViewModel sẽ xử lý việc fetch
            }
            notifyItemChanged(position);
        });

        holder.binding.btnAnswer.setOnClickListener(v -> {
            Context context = v.getContext();
            SharedPreferences prefs = context.getSharedPreferences("USER_PREF", Context.MODE_PRIVATE);
            String uid = prefs.getString("uid", null);

            if (uid == null) {
                Toast.makeText(context, "Bạn không thể thực hiện hành động này khi chưa đăng nhập.", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: xử lý nếu đã đăng nhập, ví dụ: mở giao diện nhập phản hồi

        });

    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ItemCommentBinding binding;

        public CommentViewHolder(ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
