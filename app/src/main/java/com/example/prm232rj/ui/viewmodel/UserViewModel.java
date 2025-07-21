package com.example.prm232rj.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm232rj.data.dto.UserDto;
import com.example.prm232rj.data.firebase.ComicRemoteDataSource;
import com.example.prm232rj.data.repository.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class UserViewModel extends ViewModel {

    private final UserRepository repository;
    private final MutableLiveData<UserDto> userLiveData = new MutableLiveData<>();

    @Inject
    public UserViewModel(UserRepository repository) {
        this.repository = repository;
    }

    public LiveData<UserDto> getUserLiveData() {
        return userLiveData;
    }

    public void loadUser(String userId) {
        repository.getUser(userId, new ComicRemoteDataSource.FirestoreCallbackOne<UserDto>() {
            @Override
            public void onSuccess(UserDto user) {
                userLiveData.postValue(user);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("UserViewModel", "Lỗi khi tải user: " + e.getMessage());
                userLiveData.postValue(null); // hoặc xử lý theo logic của bạn
            }
        });
    }
}