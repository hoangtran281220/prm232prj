package com.example.prm232rj.data.repository;

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
}
