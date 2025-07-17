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
    private DocumentSnapshot lastVisible = null;
    private boolean isLastPage = false;
    private String chapterId;

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

    public void loadMoreRootComments(String chapterId, int pageSize) {
        if (isLastPage) return;

        repository.getPagedRootCommentsRealtime(chapterId, lastVisible, pageSize, new ComicRemoteDataSource.FirebaseCommentPagingCallback() {
            @Override
            public void onPage(List<Comment> newComments, @Nullable DocumentSnapshot newLastVisible) {
                lastVisible = newLastVisible;

                List<Comment> current = commentsLiveData.getValue();
                if (current == null) current = new ArrayList<>();
                current.addAll(newComments);
                commentsLiveData.postValue(current);

                if (newComments.isEmpty()) {
                    isLastPage = true;
                }
            }

            @Override
            public void onError(Exception e) {
                // TODO: Xử lý lỗi nếu cần
                Log.e("my_err",""+e);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        for (ListenerRegistration reg : replyListeners.values()) {
            reg.remove();
        }
        replyListeners.clear();
        repository.removeCommentListener(); // nếu có lắng nghe root comment
    }
}

