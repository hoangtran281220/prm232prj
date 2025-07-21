package com.example.prm232rj.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm232rj.data.dto.ComicDtoPreview;
import com.example.prm232rj.data.firebase.AuthService;
import com.example.prm232rj.data.firebase.ComicRemoteDataSource;
import com.example.prm232rj.data.repository.AuthRepository;
import com.example.prm232rj.data.repository.ComicRepository;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FollowedComicsViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final ComicRepository comicRepository;


    private final MutableLiveData<List<ComicDtoPreview>> followedComics = new MutableLiveData<>();
    private final MutableLiveData<Boolean> followSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> unfollowSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private ListenerRegistration registration;



    @Inject
    public FollowedComicsViewModel(AuthRepository authRepository, ComicRepository comicRepository) {
        this.authRepository = authRepository;
        this.comicRepository = comicRepository;
    }

    public void observeFollowedComics(String userId) {
        // Hủy listener cũ nếu có
        if (registration != null) {
            registration.remove();
        }

        registration = comicRepository.observeFollowedComics(userId, new ComicRemoteDataSource.RealtimeComicCallback() {
            @Override
            public void onSuccess(List<ComicDtoPreview> comics) {
                followedComics.postValue(comics);
            }

            @Override
            public void onFailure(Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    public void followComic(String userId, String comicId) {
        authRepository.followComic(userId, comicId, new AuthService.FirebaseCallbackUser<>() {
            @Override
            public void onComplete(Void result) {
                followSuccess.postValue(true);
            }

            @Override
            public void onFailure(Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    public void unfollowComic(String userId, String comicId) {
        authRepository.unfollowComic(userId, comicId, new AuthService.FirebaseCallbackUser<>() {
            @Override
            public void onComplete(Void result) {
                unfollowSuccess.postValue(true);
            }

            @Override
            public void onFailure(Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    public LiveData<List<ComicDtoPreview>> getFollowedComics() {
        return followedComics;
    }

    public LiveData<Boolean> getFollowSuccess() {
        return followSuccess;
    }

    public LiveData<Boolean> getUnfollowSuccess() {
        return unfollowSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (registration != null) {
            registration.remove();
        }
    }
}
