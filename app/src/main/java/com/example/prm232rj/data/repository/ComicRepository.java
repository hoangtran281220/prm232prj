package com.example.prm232rj.data.repository;

import com.example.prm232rj.data.dto.ComicDtoBanner;
import com.example.prm232rj.data.dto.ComicDtoPreview;
import com.example.prm232rj.data.firebase.ComicRemoteDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ComicRepository {
    private final ComicRemoteDataSource remoteDataSource;
    @Inject
    public ComicRepository(ComicRemoteDataSource remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
    }
    public void getComicBanners(ComicRemoteDataSource.FirebaseCallback<ComicDtoBanner> callback) {
        remoteDataSource.getComicBanners(callback);
    }

    // Lấy danh sách preview truyện
    public void getComicPreviews(ComicRemoteDataSource.FirebaseCallback<ComicDtoPreview> callback) {
        remoteDataSource.getComicPreviews(callback);
    }
}
