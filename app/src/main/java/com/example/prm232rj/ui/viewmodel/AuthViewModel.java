package com.example.prm232rj.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm232rj.data.firebase.AuthService;
import com.example.prm232rj.data.repository.AuthRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;

    private final MutableLiveData<Boolean> _registerSuccess = new MutableLiveData<>();
    public LiveData<Boolean> registerSuccess = _registerSuccess;

    private final MutableLiveData<String> _registerError = new MutableLiveData<>();
    public LiveData<String> registerError = _registerError;

    @Inject
    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void register(String username, String password) {
        authRepository.registerWithUsername(username, password, new AuthService.RegisterCallback() {
            @Override
            public void onSuccess() {
                _registerSuccess.postValue(true);
            }

            @Override
            public void onFailure(String message) {
                _registerError.postValue(message);
            }
        });
    }
}
