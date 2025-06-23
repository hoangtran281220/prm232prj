package com.example.prm232rj.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm232rj.data.firebase.ComicRemoteDataSource;
import com.example.prm232rj.data.model.Author;
import com.example.prm232rj.data.model.Chapter;
import com.example.prm232rj.data.model.Comic;
import com.example.prm232rj.data.model.Tag;
import com.example.prm232rj.data.repository.ComicRepository;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ComicDetailViewModel extends ViewModel {
    private final ComicRepository repository;

    private final MutableLiveData<Comic> _comic = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    private final MutableLiveData<List<Chapter>> _chapters = new MutableLiveData<>();

    private final MutableLiveData<String> _authorNames = new MutableLiveData<>();
    private final MutableLiveData<String> _tagNames = new MutableLiveData<>();

    public LiveData<String> getAuthorNames() { return _authorNames; }
    public LiveData<String> getTagNames() { return _tagNames; }

    public LiveData<List<Chapter>> getChapters() { return _chapters; }
    @Inject
    public ComicDetailViewModel(ComicRepository repository) {
        this.repository = repository;
        _isLoading.setValue(false);
    }

    // Getters cho LiveData
    public LiveData<Comic> getComic() {
        return _comic;
    }

    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }

    public LiveData<String> getError() {
        return _error;
    }

    // Load comic theo ID
    public void loadComic(String comicId) {
        if (comicId == null || comicId.trim().isEmpty()) {
            _error.setValue("ID truyện không hợp lệ");
            return;
        }

        _isLoading.setValue(true);
        _error.setValue(null);

        repository.getComicById(comicId, new ComicRemoteDataSource.FirebaseCallback<Comic>() {
            @Override
            public void onComplete(java.util.List<Comic> result) {
                _isLoading.setValue(false);
                if (result != null && !result.isEmpty()) {
                    _comic.setValue(result.get(0));

                } else {
                    _error.setValue("Không tìm thấy dữ liệu truyện");
                }
            }

            @Override
            public void onFailure(Exception e) {
                _isLoading.setValue(false);
                _error.setValue(e.getMessage() != null ? e.getMessage() : "Đã xảy ra lỗi khi tải dữ liệu");
            }
        });
    }


    public void loadChapters(String comicId) {
        repository.getChaptersByComicId(comicId, new ComicRemoteDataSource.FirebaseCallback<Chapter>() {
            @Override
            public void onComplete(List<Chapter> result) {
                _chapters.setValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                _error.setValue("Không thể tải danh sách chương: " + e.getMessage());
            }
        });
    }

    //Load tác giả và thể loại
    public void fetchAuthorAndTags(Comic comic) {
        Log.d("ComicDetailViewModel", "Author IDs: " + comic.getAuthorId());
        Log.d("ComicDetailViewModel", "Tag IDs: " + comic.getTagId());
        if (comic.getAuthorId() != null && !comic.getAuthorId().isEmpty()) {
            repository.getAuthorsByIds(comic.getAuthorId(), new ComicRemoteDataSource.FirebaseCallback<Author>() {
                @Override
                public void onComplete(List<Author> result) {
                    String names = result.stream().map(Author::getName).collect(Collectors.joining(", "));
                    _authorNames.postValue("Tác giả: " + names);
                }

                @Override
                public void onFailure(Exception e) {
                    _authorNames.postValue("Tác giả: Không rõ");
                }
            });
        }

        if (comic.getTagId() != null && !comic.getTagId().isEmpty()) {
            repository.getTagsByIds(comic.getTagId(), new ComicRemoteDataSource.FirebaseCallback<Tag>() {
                @Override
                public void onComplete(List<Tag> result) {
                    String names = result.stream().map(Tag::getName).collect(Collectors.joining(", "));
                    _tagNames.postValue("Thể loại: " + names);
                }

                @Override
                public void onFailure(Exception e) {
                    _tagNames.postValue("Thể loại: Không rõ");
                }
            });
        }
    }

    // Refresh data
    public void refresh(String comicId) {
        loadComic(comicId);
    }


}