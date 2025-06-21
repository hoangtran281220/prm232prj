package com.example.prm232rj.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm232rj.data.dto.ComicDtoPreview;
import com.example.prm232rj.data.dto.ComicDtoWithTags;
import com.example.prm232rj.data.firebase.ComicRemoteDataSource;
import com.example.prm232rj.data.repository.ComicRepository;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ComicListViewModel extends ViewModel {

    private final ComicRepository repository;

    private final MutableLiveData<List<ComicDtoWithTags>> comics = new MutableLiveData<>();

    @Inject
    public ComicListViewModel(ComicRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<ComicDtoWithTags>> getComics() {
        return comics;
    }

    public void loadComicsByTags(List<String> tagIds) {
        repository.getComicsByTagIds(tagIds, new ComicRemoteDataSource.FirebaseCallback<>() {
            @Override
            public void onComplete(List<ComicDtoWithTags> result) {
                comics.postValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                // Log hoặc xử lý lỗi
            }
        });
    }
}