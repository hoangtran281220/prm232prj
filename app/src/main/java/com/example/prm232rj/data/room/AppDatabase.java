package com.example.prm232rj.data.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {ReadHistoryEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ReadHistoryDao readHistoryDao();
}
