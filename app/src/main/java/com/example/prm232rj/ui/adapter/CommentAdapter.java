package com.example.prm232rj.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm232rj.data.model.Comment;
import com.example.prm232rj.data.model.Reply;
import com.example.prm232rj.databinding.ItemCommentBinding;
import com.example.prm232rj.ui.screen.Dialogs.ReplyInputDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private final Fragment parentFragment;
    private String chapterId;
    private final Map<String, List<Reply>> repliesMap = new HashMap<>();
    private final Map<String, Boolean> expandedReplies = new HashMap<>();
    private final Map<String, ReplyAdapter> replyAdapters = new HashMap<>();
    private OnReplyCountRequestListener replyCountListener;
    private Map<String, Integer> replyCounts = new HashMap<>();

    public interface OnReplyCountRequestListener {
        void onRequestReplyCount(String commentId);
    }
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

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public void setReplyCounts(Map<String, Integer> replyCounts) {
        this.replyCounts = replyCounts;
        notifyDataSetChanged();
    }
    public void setOnReplyCountRequestListener(OnReplyCountRequestListener listener) {
        this.replyCountListener = listener;
    }


    public CommentAdapter(List<Comment> commentList, Fragment parentFragment) {
        this.commentList = commentList;
        this.parentFragment = parentFragment;
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

        boolean isExpanded = expandedReplies.getOrDefault(comment.getId(), false);

        holder.binding.recyclerReplies.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        // Adapter reply (chỉ khi expand)
        if (isExpanded) {
            ReplyAdapter replyAdapter = replyAdapters.get(comment.getId());
            if (replyAdapter == null) {
                replyAdapter = new ReplyAdapter(new ArrayList<>());
                replyAdapter.setContextInfo(parentFragment, chapterId, comment.getId()); // truyền thêm
                replyAdapters.put(comment.getId(), replyAdapter);
            }
            holder.binding.recyclerReplies.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            holder.binding.recyclerReplies.setAdapter(replyAdapter);

            if (repliesMap.containsKey(comment.getId())) {
                replyAdapter.setReplies(repliesMap.get(comment.getId()));
            }
        } else {
            holder.binding.recyclerReplies.setAdapter(null);
            replyAdapters.remove(comment.getId()); // Xóa adapter khi collapse để tiết kiệm bộ nhớ
        }

        int count = replyCounts.containsKey(comment.getId()) ? replyCounts.get(comment.getId()) : 0;
        holder.binding.setReplyCount(count);

        holder.binding.btnReply.setOnClickListener(v -> {
            boolean currentlyVisible = expandedReplies.getOrDefault(comment.getId(), false);
            expandedReplies.put(comment.getId(), !currentlyVisible);

            if (currentlyVisible) {
                replyAdapters.remove(comment.getId());
                holder.binding.recyclerReplies.setAdapter(null);
            } else if (listener != null) {
                listener.onExpandReplies(comment.getId());
            }

            notifyItemChanged(position);
        });


        if (replyCountListener != null) {
            replyCountListener.onRequestReplyCount(comment.getId());
        }

        holder.binding.btnAnswer.setOnClickListener(v -> {
            Context context = v.getContext();
            SharedPreferences prefs = context.getSharedPreferences("USER_PREF", Context.MODE_PRIVATE);
            String uid = prefs.getString("uid", null);

            if (uid == null) {
                Toast.makeText(context, "Bạn không thể thực hiện hành động này khi chưa đăng nhập.", Toast.LENGTH_SHORT).show();
                return;
            }

            String replyId = comment.getId();
            String userReplyId = comment.getUserId();
            String replyName = comment.getUserName();

            ReplyInputDialogFragment dialog = new ReplyInputDialogFragment(
                    comment.getId(),   // conversationId
                    replyId,
                    userReplyId,
                    replyName,
                    chapterId
            );
            dialog.show(parentFragment.getParentFragmentManager(), "ReplyInputDialog");
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
