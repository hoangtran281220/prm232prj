package com.example.prm232rj.ui.screen.Dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.prm232rj.databinding.DialogReplyInputBinding;
import com.example.prm232rj.ui.viewmodel.CommentViewModel;
import com.example.prm232rj.ui.viewmodel.UserViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ReplyInputDialogFragment extends DialogFragment {

    private DialogReplyInputBinding binding;
    private CommentViewModel commentViewModel;
    private UserViewModel userViewModel;
    private final String conversationId; // comment gốc
    private final String replyId;        // comment gốc hoặc reply bị rep
    private final String userReplyId;    // userId người bị rep
    private final String replyName;      // tên người bị nhắc
    private final String chapterId;

    public ReplyInputDialogFragment(String conversationId, String replyId, String userReplyId, String replyName, String chapterId) {
        this.conversationId = conversationId;
        this.replyId = replyId;
        this.userReplyId = userReplyId;
        this.replyName = replyName;
        this.chapterId = chapterId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DialogReplyInputBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        commentViewModel = new ViewModelProvider(requireActivity()).get(CommentViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        commentViewModel.setChapterId(chapterId);
        binding.tvReplyTo.setText("Phản hồi @" + replyName);

        SharedPreferences prefs = requireContext().getSharedPreferences("USER_PREF", Context.MODE_PRIVATE);
        String userId = prefs.getString("uid", null);

        if (userId == null) {
            Toast.makeText(getContext(), "Cần đăng nhập để gửi phản hồi", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        userViewModel.loadUser(userId);
        commentViewModel.getReplySuccess().observe(getViewLifecycleOwner(), event -> {
            Boolean success = event != null ? event.getContentIfNotHandled() : null;
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(getContext(), "Đã gửi phản hồi!", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });


        commentViewModel.getReplyErrorMessage().observe(getViewLifecycleOwner(), err -> {
            if (err != null) {
                Toast.makeText(getContext(), "Lỗi khi gửi phản hồi: " + err, Toast.LENGTH_SHORT).show();
            }
        });
        binding.btnSendReply.setOnClickListener(v -> {
            String content = binding.edtReplyInput.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                return;
            }

            userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
                if (user == null) {
                    Toast.makeText(getContext(), "Không tải được thông tin người dùng", Toast.LENGTH_SHORT).show();
                    return;
                }

                commentViewModel.addReplyToComment(
                        conversationId,
                        replyId,
                        userReplyId,
                        userId,
                        user.getUsername(),
                        user.getAvatarUrl(),
                        content,
                        replyName
                );

                dismiss();
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}