package com.example.prm232rj.ui.viewmodel;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm232rj.data.firebase.ComicRemoteDataSource;
import com.example.prm232rj.data.model.Comment;
import com.example.prm232rj.data.model.Reply;
import com.example.prm232rj.data.repository.ComicRepository;
import com.example.prm232rj.utils.Event;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CommentViewModel extends ViewModel {

    private final ComicRepository repository;

    private final MutableLiveData<List<Comment>> commentsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Map<String, List<Reply>>> repliesMapLiveData = new MutableLiveData<>(new HashMap<>());

    private final Map<String, ListenerRegistration> replyListeners = new HashMap<>();
    private ListenerRegistration commentListener;
    private String chapterId;
    private final  MutableLiveData<Event<Boolean>> replySuccess = new MutableLiveData<>();
    private final MutableLiveData<String> replyErrorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> commentAddedSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> commentErrorMessage = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Integer>> replyCounts = new MutableLiveData<>(new HashMap<>());
    private final Map<String, ListenerRegistration> replyCountListeners = new HashMap<>();

    @Inject
    public CommentViewModel(ComicRepository repository) {
        this.repository = repository;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public LiveData<List<Comment>> getCommentsLiveData() {
        return commentsLiveData;
    }

    public void setComments(List<Comment> comments) {
        commentsLiveData.setValue(comments);
    }

    public LiveData<Map<String, List<Reply>>> getRepliesLiveData() {
        return repliesMapLiveData;
    }
    public void listenToRootComments() {
        if (commentListener != null) {
            commentListener.remove();
        }

        repository.listenToRootComments(chapterId, new ComicRemoteDataSource.FirebaseCallback<Comment>() {
            @Override
            public void onComplete(List<Comment> result) {
                commentsLiveData.postValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("CommentVM", "Listen failed: " + e);
            }
        });
    }
    public void loadReplies(String commentId) {
        if (replyListeners.containsKey(commentId)) return;

        ListenerRegistration listener = repository.getRepliesRealtime(chapterId, commentId, new ComicRemoteDataSource.FirebaseCallback<Reply>() {
            @Override
            public void onComplete(List<Reply> result) {
                Map<String, List<Reply>> currentMap = repliesMapLiveData.getValue();
                if (currentMap == null) currentMap = new HashMap<>();
                currentMap.put(commentId, result);
                repliesMapLiveData.postValue(currentMap);
            }

            @Override
            public void onFailure(Exception e) {
                // TODO: handle error if needed
                Log.e("my_err" , ""+e);
            }
        });

        replyListeners.put(commentId, listener);
    }



    public void addComment(String userId, String userName, String avatarUrl, String content) {
        repository.addCommentToChapter(chapterId, userId, userName, avatarUrl, content, new ComicRemoteDataSource.FirestoreCallbackComment() {
            @Override
            public void onSuccess() {
                commentAddedSuccess.postValue(true);
            }

            @Override
            public void onFailure(Exception e) {
                commentErrorMessage.postValue(e.getMessage());
            }
        });
    }

    public void addReplyToComment(
            String commentId,       // comment gốc
            String replyId,         // bị rep
            String userReplyId,
            String userId,
            String userName,
            String avatarUrl,
            String content,
            String replyName
    ) {
        repository.addReplyToComment(
                chapterId,
                commentId,
                replyId,
                userId,
                userName,
                avatarUrl,
                content,
                replyName,
                userReplyId,
                new ComicRemoteDataSource.FirestoreCallbackComment() {
                    @Override
                    public void onSuccess() {
                        replySuccess.postValue(new Event<>(true));
                    }

                    @Override
                    public void onFailure(Exception e) {
                        replyErrorMessage.postValue(e.getMessage());
                    }
                }
        );
    }

    public LiveData<Event<Boolean>> getReplySuccess() {
        return replySuccess;
    }

    public LiveData<String> getReplyErrorMessage() {
        return replyErrorMessage;
    }

    public LiveData<Boolean> getCommentAddedSuccess() {
        return commentAddedSuccess;
    }

    public LiveData<String> getCommentErrorMessage() {
        return commentErrorMessage;
    }
    public LiveData<Map<String, Integer>> getReplyCounts() {
        return replyCounts;
    }

    public void fetchReplyCount(String chapterId, String commentId) {
        repository.getReplyCount(chapterId, commentId, new ComicRemoteDataSource.FirestoreCallbackOne<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                Map<String, Integer> map = replyCounts.getValue();
                if (map == null) map = new HashMap<>();
                map.put(commentId, result);
                replyCounts.postValue(map);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("CommentViewModel", "Failed to fetch reply count", e);
            }
        });
    }

    public void listenToReplyCountRealtime(String chapterId, String commentId) {
        if (replyCountListeners.containsKey(commentId)) return;

        ListenerRegistration reg = repository.getRepliesRealtime(chapterId, commentId, new ComicRemoteDataSource.FirebaseCallback<Reply>() {
            @Override
            public void onComplete(List<Reply> replies) {
                Map<String, Integer> currentCounts = replyCounts.getValue();
                if (currentCounts == null) currentCounts = new HashMap<>();
                currentCounts.put(commentId, replies.size());
                replyCounts.postValue(currentCounts);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("CommentViewModel", "ReplyCount realtime failed for: " + commentId, e);
            }
        });

        replyCountListeners.put(commentId, reg);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        for (ListenerRegistration reg : replyListeners.values()) {
            reg.remove();
        }
        for (ListenerRegistration reg : replyCountListeners.values()) {
            reg.remove();
        }
        if (commentListener != null) {
            commentListener.remove();
            commentListener = null;
        }
    }
}

