package com.example.prm232rj.data.repository;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.example.prm232rj.data.dto.ChapterReadingDto;
import com.example.prm232rj.data.dto.ComicDtoBanner;
import com.example.prm232rj.data.dto.ComicDtoPreview;
import com.example.prm232rj.data.dto.ComicDtoWithTags;
import com.example.prm232rj.data.firebase.ComicRemoteDataSource;
import com.example.prm232rj.data.model.Author;
import com.example.prm232rj.data.model.Chapter;
import com.example.prm232rj.data.model.Comic;
import com.example.prm232rj.data.model.RatingResult;
import com.example.prm232rj.data.model.Reply;
import com.example.prm232rj.data.model.Tag;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.ListenerRegistration;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ComicRepository {
    private final ComicRemoteDataSource remoteDataSource;

    private DocumentSnapshot lastVisible = null;
    private DocumentSnapshot fallbackLastVisible = null;
    private DocumentSnapshot filterLastVisible = null;
    private boolean isLastTagPage = false;
    private boolean isLastFallbackPage = false;
    private boolean isLastFilteredPage = false;
    public boolean isLastTagPage() {
        return isLastTagPage;
    }

    public boolean isLastFallbackPage() {
        return isLastFallbackPage;
    }

    public boolean isLastFilteredPage() {
        return isLastFilteredPage;
    }
    @Inject
    public ComicRepository(ComicRemoteDataSource remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
    }

    public void resetPaging() {
        lastVisible = null;
        isLastTagPage = false;
    }

    public void resetFallbackPaging() {
        fallbackLastVisible = null;
        isLastFallbackPage = false;
    }

    public void resetFilterVisible(){
        filterLastVisible = null;
        isLastFilteredPage = false;
    }

    public ListenerRegistration getComicBanners(Activity activity, ComicRemoteDataSource.FirebaseCallback<ComicDtoBanner> callback) {
        return remoteDataSource.getComicBanners(activity, callback);
    }

    // Lấy danh sách preview truyện
    public void getComicPreviews(ComicRemoteDataSource.FirebaseCallback<ComicDtoPreview> callback) {
        remoteDataSource.getComicPreviews(callback);
    }

    public void getComicsFallback(boolean isFirstPage, int pageSize, ComicRemoteDataSource.FirebasePagingCallback<ComicDtoWithTags> callback) {
        if (isFirstPage) {
            fallbackLastVisible = null;
            isLastFallbackPage = false;
        }

        remoteDataSource.getComicsFallback(fallbackLastVisible, pageSize, new ComicRemoteDataSource.FirebasePagingCallback<>() {
            @Override
            public void onComplete(List<ComicDtoWithTags> result, @Nullable DocumentSnapshot newLastVisible) {
                fallbackLastVisible = newLastVisible;
                if (result.isEmpty()) {
                    isLastFallbackPage = true;
                }
                callback.onComplete(result, newLastVisible);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }


    public void getComicsByTagIdPaging(String tagId, boolean isFirstPage, int pageSize, ComicRemoteDataSource.FirebasePagingCallback<ComicDtoWithTags> callback) {
        if (isFirstPage) {
            lastVisible = null;
            isLastTagPage = false;
        }

        remoteDataSource.getComicsByTagIdPaging(tagId, lastVisible, pageSize, new ComicRemoteDataSource.FirebasePagingCallback<>() {
            @Override
            public void onComplete(List<ComicDtoWithTags> result, @Nullable DocumentSnapshot newLastVisible) {
                lastVisible = newLastVisible;
                if (result.isEmpty()) {
                    isLastTagPage = true;
                }
                callback.onComplete(result, newLastVisible);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }


    public ListenerRegistration getComicsByTagIdTop(Activity activity, String tagId, ComicRemoteDataSource.FirebaseCallback<ComicDtoWithTags> callback)
    {
        return remoteDataSource.getComicsByTagIdTop(activity, tagId, callback);
    }

    public void getFilteredComics(List<String> tagIds, String status, String sortBy,
                                  boolean isFirstPage, int pageSize,
                                  ComicRemoteDataSource.FirebasePagingCallback<ComicDtoWithTags> callback) {
        if (isFirstPage) {
            filterLastVisible = null;
            isLastFilteredPage = false;
        }

        remoteDataSource.getFilteredComics(tagIds, status, sortBy, filterLastVisible, pageSize,
                new ComicRemoteDataSource.FirebasePagingCallback<>() {
                    @Override
                    public void onComplete(List<ComicDtoWithTags> result, @Nullable DocumentSnapshot newLastVisible) {
                        filterLastVisible = newLastVisible;
                        if (result.isEmpty()) {
                            isLastFilteredPage = true;
                        }
                        callback.onComplete(result, newLastVisible);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        callback.onFailure(e);
                    }
                });
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

    public ListenerRegistration observeFollowedComics(String userId, ComicRemoteDataSource.RealtimeComicCallback callback) {
        return remoteDataSource.observeFollowedComicsRealtime(userId, new ComicRemoteDataSource.FirebaseCallback<>() {
            @Override
            public void onComplete(List<ComicDtoPreview> result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void incrementComicViews(String comicId) {
        remoteDataSource.incrementComicViews(comicId);
    }

    public void incrementChapterViews(String chapterId) {
        remoteDataSource.incrementChapterViews(chapterId);
    }

    public void rateComic(String comicId, String userId, double rating, OnCompleteListener<RatingResult> listener) {
        remoteDataSource.rateComic(comicId, userId, rating, listener);
    }

    public void addCommentToChapter(
            String chapterId,
            String userId,
            String userName,
            String avatarUrl,
            String content,
            ComicRemoteDataSource.FirestoreCallbackComment callback
    ) {
        remoteDataSource.addCommentToChapter(
                chapterId,
                userId,
                userName,
                avatarUrl,
                content,
                callback
        );
    }

    public void getPagedRootCommentsRealtime(
            String chapterId,
            @Nullable DocumentSnapshot lastVisible,
            int pageSize,
            ComicRemoteDataSource.FirebaseCommentPagingCallback callback
    ) {
        remoteDataSource.getPagedRootCommentsRealtime(chapterId, lastVisible, pageSize, callback);
    }

    public void removeCommentListener() {
        remoteDataSource.removeCommentListener();
    }

    public ListenerRegistration getRepliesRealtime(String chapterId, String commentId, ComicRemoteDataSource.FirebaseCallback<Reply> callback) {
        return remoteDataSource.getRepliesRealtime(chapterId, commentId, callback);
    }

}
