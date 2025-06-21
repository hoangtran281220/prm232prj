package com.example.prm232rj.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm232rj.data.dto.ComicDtoBanner;
import com.example.prm232rj.data.dto.ComicDtoPreview;
import com.example.prm232rj.data.repository.ComicRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ComicViewModel extends ViewModel {
    private final ComicRepository repository;

    private final MutableLiveData<List<ComicDtoBanner>> bannerList = new MutableLiveData<>();
    private final MutableLiveData<List<ComicDtoPreview>> previewList = new MutableLiveData<>();

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

    public void loadBanners() {
        repository.getComicBanners(bannerList::postValue);
    }

    public void loadPreviews() {
        repository.getComicPreviews(previewList::postValue);
    }
}
