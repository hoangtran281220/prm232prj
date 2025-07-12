package com.example.prm232rj.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.prm232rj.data.room.AppDatabase;
import com.example.prm232rj.data.room.ReadHistoryDao;
import com.example.prm232rj.data.room.ReadHistoryEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ReadHistoryRepository {
    private final ReadHistoryDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Inject
    public ReadHistoryRepository(ReadHistoryDao dao) {
        this.dao = dao;
    }

    /**
     * Chèn mới hoặc cập nhật lịch sử đọc của truyện.
     */
    public void insertOrUpdate(ReadHistoryEntity entity) {
        executor.execute(() -> dao.insertOrUpdate(entity));
    }

    /**
     * Lấy toàn bộ lịch sử đọc để hiển thị (đồng bộ, nên gọi từ thread nền).
     */
    public LiveData<List<ReadHistoryEntity>> getAllHistoriesLive() {
        return dao.getAllHistoriesLive();
    }

    /**
     * Lấy lịch sử theo comicId.
     */
    public ReadHistoryEntity getByComicId(String comicId) {
        return dao.getByComicId(comicId);
    }
}
