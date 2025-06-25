package com.example.prm232rj.data.repository;

import android.util.Log;

import com.example.prm232rj.data.dto.ChapterReadingDto;
import com.example.prm232rj.data.dto.ComicDtoBanner;
import com.example.prm232rj.data.dto.ComicDtoPreview;
import com.example.prm232rj.data.dto.ComicDtoWithTags;
import com.example.prm232rj.data.firebase.ComicRemoteDataSource;
import com.example.prm232rj.data.model.Author;
import com.example.prm232rj.data.model.Chapter;
import com.example.prm232rj.data.model.Comic;
import com.example.prm232rj.data.model.Tag;

import java.util.List;

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

    public void getComicsByTagIds(List<String> tagIds, ComicRemoteDataSource.FirebaseCallback<ComicDtoWithTags> callback) {
        Log.d("mytagt","repo");

        remoteDataSource.getComicsByTagIds(tagIds, callback);
    }

    // Thêm method để lấy comic theo ID
    public void getComicById(String comicId, ComicRemoteDataSource.FirebaseCallback<Comic> callback) {
        remoteDataSource.getComicById(comicId, callback);
    }

    public void getChaptersByComicId(String comicId, ComicRemoteDataSource.FirebaseCallback<Chapter> callback) {
        remoteDataSource.getChaptersByComicId(comicId, callback);
    }

    public void getAuthorsByIds(List<String> ids, ComicRemoteDataSource.FirebaseCallback<Author> callback) {
        remoteDataSource.getAuthorsByIds(ids, callback);
    }

    public void getTagsByIds(List<String> ids, ComicRemoteDataSource.FirebaseCallback<Tag> callback) {
        remoteDataSource.getTagsByIds(ids, callback);
    }

    public void getComicsHotTop3(ComicRemoteDataSource.FirebaseCallback<ComicDtoWithTags> callback){
        remoteDataSource.getComicsHotTop3(callback);
    }

    public void getChapterByIdForReading(String comicId, String chapterId, ComicRemoteDataSource.FirebaseCallback<ChapterReadingDto> callback) {
        remoteDataSource.getChapterByIdForReading(comicId, chapterId, callback);
    }

    public void getAllChapters(String comicId, ComicRemoteDataSource.FirebaseCallback<ChapterReadingDto> callback) {
        remoteDataSource.getAllChapters(comicId, callback);
    }

}
