package com.example.prm232rj.data.room;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "read_history")
public class ReadHistoryEntity {
    @PrimaryKey
    @NonNull
    public String comicId = "";
    public String chapterId;
    public String comicTitle;
    public String comicCoverUrl;
    public int chapterReading;
    public long lastReadAt;
}
