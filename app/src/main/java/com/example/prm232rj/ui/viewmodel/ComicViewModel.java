package com.example.prm232rj.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm232rj.data.dto.ComicDtoBanner;
import com.example.prm232rj.data.dto.ComicDtoPreview;
import com.example.prm232rj.data.dto.ComicDtoWithTags;
import com.example.prm232rj.data.firebase.ComicRemoteDataSource;
import com.example.prm232rj.data.repository.ComicRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ComicViewModel extends ViewModel {
    private final ComicRepository repository;

    private final MutableLiveData<List<ComicDtoBanner>> bannerList = new MutableLiveData<>();
    private final MutableLiveData<List<ComicDtoPreview>> previewList = new MutableLiveData<>();

    private final MutableLiveData<List<ComicDtoWithTags>> comics = new MutableLiveData<>();

    @Inject
    public ComicViewModel(ComicRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<ComicDtoBanner>> getBanners() {
        return bannerList;
    }

    public LiveData<List<ComicDtoPreview>> getPreviews() {
        return previewList;
    }

    public LiveData<List<ComicDtoWithTags>> getComics() {
        return comics;
    }


    public void loadBanners() {
        repository.getComicBanners(new ComicRemoteDataSource.FirebaseCallback<>() {
            @Override
            public void onComplete(List<ComicDtoBanner> result) {
                bannerList.postValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                // Log hoặc xử lý lỗi
            }
        });
    }

    public void loadPreviews() {
        repository.getComicPreviews(new ComicRemoteDataSource.FirebaseCallback<>() {
            @Override
            public void onComplete(List<ComicDtoPreview> result) {
                previewList.postValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                // Log hoặc xử lý lỗi
            }
        });
    }
    public void loadComicsByTags(List<String> tagIds) {
        Log.d("mytagt","vm");
        repository.getComicsByTagIds(tagIds, new ComicRemoteDataSource.FirebaseCallback<>() {
            @Override
            public void onComplete(List<ComicDtoWithTags> result) {
                comics.postValue(result);
                Log.d("mytagt","vm-cmpt");

            }

            @Override
            public void onFailure(Exception e) {
                // Log hoặc xử lý lỗi
                Log.d("mytagt","vm-fail");

            }
        });
    }

}
