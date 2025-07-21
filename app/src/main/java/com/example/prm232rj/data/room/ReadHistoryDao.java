package com.example.prm232rj.data.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReadHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(ReadHistoryEntity entity);

    @Query("SELECT * FROM read_history ORDER BY lastReadAt DESC")
    LiveData<List<ReadHistoryEntity>> getAllHistoriesLive();

    @Query("SELECT * FROM read_history WHERE comicId = :comicId LIMIT 1")
    ReadHistoryEntity getByComicId(String comicId);

    @Query("SELECT * FROM read_history WHERE comicId = :comicId LIMIT 1")
    ReadHistoryEntity getByComicIdAndChapterId(String comicId);
}
