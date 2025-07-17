package com.example.prm232rj.ui.screen.Dialogs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm232rj.databinding.DialogCommentsBinding;
import com.example.prm232rj.ui.adapter.CommentAdapter;
import com.example.prm232rj.ui.screen.Activities.LoginActivity;
import com.example.prm232rj.ui.screen.Activities.RegisterActivity;
import com.example.prm232rj.ui.viewmodel.CommentViewModel;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CommentDialogFragment extends DialogFragment {

    private DialogCommentsBinding binding;
    private CommentViewModel viewModel;
    private CommentAdapter adapter;
    private String chapterId;

    public CommentDialogFragment(String chapterId) {
        this.chapterId = chapterId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DialogCommentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // ViewModel lấy qua Hilt
        viewModel = new ViewModelProvider(this).get(CommentViewModel.class);
        viewModel.setChapterId(chapterId);

        // Setup RecyclerView
        adapter = new CommentAdapter(new ArrayList<>());
        adapter.setOnExpandRepliesListener(commentId -> viewModel.loadReplies(commentId));
        binding.recyclerComments.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerComments.setAdapter(adapter);

        // Load dữ liệu ban đầu
        viewModel.loadMoreRootComments(chapterId, 10);

        // Quan sát danh sách comment
        viewModel.getCommentsLiveData().observe(getViewLifecycleOwner(), comments -> {
            adapter = new CommentAdapter(comments);
            adapter.setOnExpandRepliesListener(commentId -> viewModel.loadReplies(commentId));
            binding.recyclerComments.setAdapter(adapter);
        });

        // Quan sát replies map để update mỗi comment
        viewModel.getRepliesLiveData().observe(getViewLifecycleOwner(), repliesMap -> {
            for (String commentId : repliesMap.keySet()) {
                adapter.updateReplies(commentId, repliesMap.get(commentId));
            }
        });

        // Scroll để load thêm comment
        binding.recyclerComments.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (lm != null && lm.findLastVisibleItemPosition() >= adapter.getItemCount() - 1) {
                    viewModel.loadMoreRootComments(chapterId, 10);
                }
            }
        });

        SharedPreferences prefs = requireActivity().getSharedPreferences("USER_PREF", Context.MODE_PRIVATE);
        String uid = prefs.getString("uid", null);

        if (uid == null) {
            showLoginHint();
        } else {
            showCommentInput();
        }

        // Nút đóng dialog
        binding.btnClose.setOnClickListener(v -> dismiss());
    }

    private void showLoginHint() {
        binding.tvLoginHint.setVisibility(View.VISIBLE);
        binding.inputLayout.setVisibility(View.GONE);

        SpannableString spannable = new SpannableString("Đăng nhập hoặc đăng ký tài khoản để đăng bình luận.");

        ClickableSpan loginSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(requireContext(), LoginActivity.class));
                dismiss();
            }

            @Override
            public void updateDrawState(@NonNull android.text.TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(false);
            }
        };

        ClickableSpan registerSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(requireContext(), RegisterActivity.class));
                dismiss();
            }

            @Override
            public void updateDrawState(@NonNull android.text.TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(false);
            }
        };

        spannable.setSpan(loginSpan, 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);      // "Đăng nhập"
        spannable.setSpan(registerSpan, 14, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // "đăng ký"

        binding.tvLoginHint.setText(spannable);
        binding.tvLoginHint.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvLoginHint.setHighlightColor(Color.TRANSPARENT);
    }

    private void showCommentInput() {
        binding.tvLoginHint.setVisibility(View.GONE);
        binding.inputLayout.setVisibility(View.VISIBLE);

        binding.inputLayout.setEndIconOnClickListener(v -> {
            String content = binding.edtCommentInput.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(getContext(), "Bạn chưa nhập bình luận.", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: xử lý gửi bình luận vào Firestore
            Toast.makeText(getContext(), "Đã gửi: " + content, Toast.LENGTH_SHORT).show();
            binding.edtCommentInput.setText("");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
        }
    }
}


