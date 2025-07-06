package com.example.prm232rj.data.repository;

import com.example.prm232rj.data.dto.ComicDtoPreview;
import com.example.prm232rj.data.dto.TagDto;
import com.example.prm232rj.data.firebase.ComicRemoteDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TagRepository {
    private final ComicRemoteDataSource remoteDataSource;
    @Inject
    public TagRepository(ComicRemoteDataSource remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
    }

    public void getAllTags(ComicRemoteDataSource.FirebaseCallback<TagDto> callback){
        remoteDataSource.getAllTags(callback);
    }
}
