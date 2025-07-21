package com.example.prm232rj.ui.viewmodel;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

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
    // Login LiveData
    private final MutableLiveData<LoginResult> _loginSuccess = new MutableLiveData<LoginResult>();
    public LiveData<LoginResult> loginSuccess = _loginSuccess;

    private final MutableLiveData<String> _loginError = new MutableLiveData<>();
    public LiveData<String> loginError = _loginError;

    // Google Sign-in LiveData
    private final MutableLiveData<GoogleSignInResult> _googleSignInSuccess = new MutableLiveData<GoogleSignInResult>();
    public LiveData<GoogleSignInResult> googleSignInSuccess = _googleSignInSuccess;

    private final MutableLiveData<String> _googleSignInError = new MutableLiveData<>();
    public LiveData<String> googleSignInError = _googleSignInError;
    private final MutableLiveData<String> _forgotPasswordResult = new MutableLiveData<>();
    public LiveData<String> forgotPasswordResult = _forgotPasswordResult;
    @Inject
    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }


    public void register(String username, String password)  {
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

    public void login(String username, String password) {
        authRepository.loginWithFirestore(username, password, new AuthService.LoginCallback() {
            @Override
            public void onSuccess(String documentId, String username) {
                _loginSuccess.postValue(new LoginResult(documentId, username));
            }

            @Override
            public void onSuccess(String uid, String displayName, String email, String photoUrl) {

            }

            @Override
            public void onFailure(String message) {
                _loginError.postValue(message);
            }
        });
    }

    public void signInWithGoogle(String idToken) {
        authRepository.signInWithGoogle(idToken, new AuthService.GoogleSignInCallback() {
            @Override
            public void onSuccess(String uid, String displayName, String email, String photoUrl) {
                _googleSignInSuccess.postValue(new GoogleSignInResult(uid, displayName, email, photoUrl));
            }

            @Override
            public void onFailure(String message) {
                _googleSignInError.postValue(message);
            }
        });
    }
    public void forgotPassword(String email) {
        authRepository.forgotPassword(email, new AuthService.ForgotPasswordCallback() {
            @Override
            public void onSuccess() {
                _forgotPasswordResult.postValue("Đã gửi email đặt lại mật khẩu");
            }

            @Override
            public void onFailure(String message) {
                _forgotPasswordResult.postValue("Lỗi: " + message);
            }
        });
    }
    // Data classes
    public static class LoginResult {
        public final String documentId;
        public final String username;

        public LoginResult(String documentId, String username) {
            this.documentId = documentId;
            this.username = username;
        }
    }

    public static class GoogleSignInResult {
        public final String uid;
        public final String displayName;
        public final String email;
        public final String photoUrl;

        public GoogleSignInResult(String uid, String displayName, String email, String photoUrl) {
            this.uid = uid;
            this.displayName = displayName;
            this.email = email;
            this.photoUrl = photoUrl;
        }
    }
}
