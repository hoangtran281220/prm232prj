package com.example.prm232rj.data.repository;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import com.example.prm232rj.data.dto.ChapterReadingDto;
import com.example.prm232rj.data.dto.ComicDtoBanner;
import com.example.prm232rj.data.dto.ComicDtoPreview;
import com.example.prm232rj.data.dto.ComicDtoWithTags;
import com.example.prm232rj.data.firebase.ComicRemoteDataSource;
import com.example.prm232rj.data.model.Author;
import com.example.prm232rj.data.model.Chapter;
import com.example.prm232rj.data.model.Comic;
import com.example.prm232rj.data.model.Tag;
import com.google.firebase.firestore.ListenerRegistration;

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
    public ListenerRegistration getComicBanners(Activity activity, ComicRemoteDataSource.FirebaseCallback<ComicDtoBanner> callback) {
        return remoteDataSource.getComicBanners(activity, callback);
    }

    // Lấy danh sách preview truyện
    public void getComicPreviews(ComicRemoteDataSource.FirebaseCallback<ComicDtoPreview> callback) {
        remoteDataSource.getComicPreviews(callback);
    }

    public void getComicsByTagIds(List<String> tagIds, ComicRemoteDataSource.FirebaseCallback<ComicDtoWithTags> callback) {
        Log.d("mytagt","repo");

        remoteDataSource.getComicsByTagIds(tagIds, callback);
    }

    public void getComicsFallback(ComicRemoteDataSource.FirebaseCallback<ComicDtoWithTags> callback) {
        remoteDataSource.getComicsFallback(callback);
    }

    public void getComicsByTagIdPaging(String tagId, ComicRemoteDataSource.FirebaseCallback<ComicDtoWithTags> callback) {
        remoteDataSource.getComicsByTagIdPaging(tagId, callback);
    }

    public ListenerRegistration getComicsByTagIdTop(Activity activity, String tagId, ComicRemoteDataSource.FirebaseCallback<ComicDtoWithTags> callback)
    {
        return remoteDataSource.getComicsByTagIdTop(activity, tagId, callback);
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

    public ListenerRegistration getComicsHotTop3(Activity activity, ComicRemoteDataSource.FirebaseCallback<ComicDtoWithTags> callback){
        return  remoteDataSource.getComicsHotTop3(activity, callback);
    }

    public void getChapterByIdForReading(String comicId, String chapterId, ComicRemoteDataSource.FirebaseCallback<ChapterReadingDto> callback) {
        remoteDataSource.getChapterByIdForReading(comicId, chapterId, callback);
    }

    public void getAllChapters(String comicId, ComicRemoteDataSource.FirebaseCallback<ChapterReadingDto> callback) {
        remoteDataSource.getAllChapters(comicId, callback);
    }

}
