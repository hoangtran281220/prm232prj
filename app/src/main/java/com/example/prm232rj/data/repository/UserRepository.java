package com.example.prm232rj.data.repository;

import com.example.prm232rj.data.dto.UserDto;
import com.example.prm232rj.data.firebase.ComicRemoteDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserRepository {

    private final ComicRemoteDataSource remoteDataSource;

    @Inject
    public UserRepository(ComicRemoteDataSource remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
    }

    public void getUser(String userId, ComicRemoteDataSource.FirestoreCallbackOne<UserDto> callback) {
        remoteDataSource.getUser(userId, callback);
    }
}
