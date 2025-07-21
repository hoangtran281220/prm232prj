package com.example.prm232rj.di;

import android.content.Context;

import androidx.room.Room;

import com.example.prm232rj.data.room.AppDatabase;
import com.example.prm232rj.data.room.ReadHistoryDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public static AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                "app_database"
        ).fallbackToDestructiveMigration().build();
    }

    @Provides
    public static ReadHistoryDao provideReadHistoryDao(AppDatabase db) {
        return db.readHistoryDao();
    }
}