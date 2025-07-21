package com.example.prm232rj.data.repository;

import android.content.Context;

import com.example.prm232rj.data.firebase.AuthService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthRepository {
    private final AuthService authService;
    @Inject
    public AuthRepository(AuthService authService) {
        this.authService = authService;
    }
    public void registerWithUsername(String username, String password, AuthService.RegisterCallback callback) {
        authService.registerWithUsernameOnly(username, password, callback);
    }
    public void loginWithFirestore(String username, String password, AuthService.LoginCallback callback) {
        authService.loginWithFirestore(username, password, callback);
    }

    public void signInWithGoogle(String idToken, AuthService.GoogleSignInCallback callback) {
        authService.signInWithGoogle(idToken, callback);
    }

    public void followComic(String userId, String comicId, AuthService.FirebaseCallbackUser<Void> callback) {
        authService.followComic(userId, comicId, callback);
    }

    public void unfollowComic(String userId, String comicId, AuthService.FirebaseCallbackUser<Void> callback) {
        authService.unfollowComic(userId, comicId, callback);
    }
    public void forgotPassword(String email, AuthService.ForgotPasswordCallback callback) {
        authService.forgotPassword(email, callback);
    }

}
