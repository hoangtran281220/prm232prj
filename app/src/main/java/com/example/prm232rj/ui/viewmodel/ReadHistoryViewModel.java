package com.example.prm232rj.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm232rj.data.repository.ReadHistoryRepository;
import com.example.prm232rj.data.room.ReadHistoryEntity;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ReadHistoryViewModel extends ViewModel {
    private final ReadHistoryRepository repository;
    private final LiveData<List<ReadHistoryEntity>> histories;

    @Inject
    public ReadHistoryViewModel(ReadHistoryRepository repository) {
        this.repository = repository;
        this.histories = repository.getAllHistoriesLive();
    }

    public void saveHistory(ReadHistoryEntity entity) {
        repository.insertOrUpdate(entity);
    }

    public LiveData<List<ReadHistoryEntity>> getHistories() {
        return histories;
    }
}
