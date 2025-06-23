package com.example.prm232rj.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm232rj.data.model.Chapter;
import com.example.prm232rj.databinding.ItemChapterBinding;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {
    private  List<Chapter> chapters;
    private final OnChapterClickListener listener;


    public interface OnChapterClickListener {
        void onClick(Chapter chapter);
    }

    public ChapterAdapter(List<Chapter> chapters, OnChapterClickListener listener) {
        this.chapters = chapters;
        this.listener = listener;
    }

    // Phương thức cập nhật dữ liệu
    public void updateChapters(List<Chapter> newChapters) {
        this.chapters = new ArrayList<>(newChapters);
        notifyDataSetChanged(); // Thông báo adapter rằng dữ liệu đã thay đổi
    }
    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemChapterBinding binding = ItemChapterBinding.inflate(inflater, parent, false);
        return new ChapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapters.get(position);
        holder.binding.setChapter(chapter);
        holder.binding.getRoot().setOnClickListener(v -> listener.onClick(chapter));
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    static class ChapterViewHolder extends RecyclerView.ViewHolder {
        ItemChapterBinding binding;

        ChapterViewHolder(ItemChapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
