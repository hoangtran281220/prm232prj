package com.example.prm232rj.ui.viewmodel;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm232rj.data.dto.ComicDtoBanner;
import com.example.prm232rj.data.dto.ComicDtoPreview;
import com.example.prm232rj.data.dto.ComicDtoWithTags;
import com.example.prm232rj.data.firebase.ComicRemoteDataSource;
import com.example.prm232rj.data.repository.ComicRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ComicViewModel extends ViewModel {
    private final ComicRepository repository;

    private final MutableLiveData<List<ComicDtoBanner>> bannerList = new MutableLiveData<>();
    private final MutableLiveData<List<ComicDtoPreview>> previewList = new MutableLiveData<>();
    private final MutableLiveData<List<ComicDtoWithTags>> comicsTop3 = new MutableLiveData<>();
    private final Map<String, MutableLiveData<List<ComicDtoWithTags>>> homeTagMap = new HashMap<>();
    private final Map<String, MutableLiveData<List<ComicDtoWithTags>>> fullTagMap = new HashMap<>();
    private final Map<String, ListenerRegistration> homeTagListeners = new HashMap<>();
    private ListenerRegistration comicTopListener;
    private ListenerRegistration bannerListener;
    private static final int PAGE_SIZE = 12;

    private String currentPagingTagId;
    @Inject
    public ComicViewModel(ComicRepository repository) {
        this.repository = repository;
    }

    // ------------------- BANNER ---------------------
    public LiveData<List<ComicDtoBanner>> getBanners() {
        return bannerList;
    }

    public void loadBanners(Activity activity) {
        if (bannerListener != null) {
            bannerListener.remove();
        }

        bannerListener = repository.getComicBanners(activity, new ComicRemoteDataSource.FirebaseCallback<>() {
            @Override
            public void onComplete(List<ComicDtoBanner> result) {
                bannerList.postValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ComicViewModel", "Failed to load banners", e);
            }
        });
    }

    // ------------------- PREVIEW ---------------------
    public LiveData<List<ComicDtoPreview>> getPreviews() {
        return previewList;
    }

    public void loadPreviews() {
        repository.getComicPreviews(new ComicRemoteDataSource.FirebaseCallback<>() {
            @Override
            public void onComplete(List<ComicDtoPreview> result) {
                previewList.postValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ComicViewModel", "Failed to load previews", e);
            }
        });
    }

    // ------------------- TOP 3 HOT ---------------------
    public LiveData<List<ComicDtoWithTags>> getComicsTop3() {
        return comicsTop3;
    }

    public void loadComicsTop3(Activity activity) {
        if (comicTopListener != null) {
            comicTopListener.remove();
        }


        comicTopListener = repository.getComicsHotTop3(activity, new ComicRemoteDataSource.FirebaseCallback<>() {
            @Override
            public void onComplete(List<ComicDtoWithTags> result) {
                comicsTop3.postValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ComicViewModel", "Failed to load top 3", e);
            }
        });
    }

    // ------------------- COMICS BY TAGS ---------------------

    public LiveData<List<ComicDtoWithTags>> getComicsForHome(String tagId) {
        if (!homeTagMap.containsKey(tagId)) {
            MutableLiveData<List<ComicDtoWithTags>> liveData = new MutableLiveData<>();
            homeTagMap.put(tagId, liveData);
        }
        return homeTagMap.get(tagId);
    }

    public LiveData<List<ComicDtoWithTags>> getComicsForList(String tagId) {
        if (!fullTagMap.containsKey(tagId)) {
            MutableLiveData<List<ComicDtoWithTags>> liveData = new MutableLiveData<>();
            fullTagMap.put(tagId, liveData);
        }
        return fullTagMap.get(tagId);
    }

    public void loadComicsByTagForHome(Activity activity, String tagId) {
        // Nếu đã có listener cũ, huỷ trước khi đăng ký mới
        if (homeTagListeners.containsKey(tagId)) {
            homeTagListeners.get(tagId).remove();
        }

        ListenerRegistration registration = repository.getComicsByTagIdTop(activity, tagId, new ComicRemoteDataSource.FirebaseCallback<>() {
            @Override
            public void onComplete(List<ComicDtoWithTags> result) {
                homeTagMap.get(tagId).postValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ComicViewModel", "Failed to load tag: " + tagId, e);
            }
        });

        homeTagListeners.put(tagId, registration);
    }


    public void loadComicsByTagForList(String tagId) {
        boolean isFallback = (tagId == null || tagId.trim().isEmpty());
        String key = isFallback ? "fallback" : tagId;

        currentPagingTagId = key;

        // Nếu chưa có LiveData cho tag này, khởi tạo
        if (!fullTagMap.containsKey(key)) {
            fullTagMap.put(key, new MutableLiveData<>(new ArrayList<>()));
        }

        if (isFallback) {
            repository.resetFallbackPaging();

            repository.getComicsFallback(true, PAGE_SIZE, new ComicRemoteDataSource.FirebasePagingCallback<ComicDtoWithTags>() {
                @Override
                public void onComplete(List<ComicDtoWithTags> result, @Nullable DocumentSnapshot lastVisible) {
                    fullTagMap.get("fallback").postValue(result);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("ComicViewModel", "Failed to load fallback comics", e);
                }
            });
        } else {
            repository.resetPaging();

            repository.getComicsByTagIdPaging(tagId, true, PAGE_SIZE, new ComicRemoteDataSource.FirebasePagingCallback<ComicDtoWithTags>() {
                @Override
                public void onComplete(List<ComicDtoWithTags> result, @Nullable DocumentSnapshot lastVisible) {
                    fullTagMap.get(tagId).postValue(result);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("ComicViewModel", "Failed to load tag: " + tagId, e);
                }
            });
        }
    }

    public void loadNextPageForTagList() {
        if (currentPagingTagId == null) return;

        boolean isFallback = currentPagingTagId.equals("fallback");

        if (isFallback) {
            repository.getComicsFallback(false, PAGE_SIZE, new ComicRemoteDataSource.FirebasePagingCallback<ComicDtoWithTags>() {
                @Override
                public void onComplete(List<ComicDtoWithTags> result, @Nullable DocumentSnapshot lastVisible) {
                    List<ComicDtoWithTags> current = fullTagMap.get("fallback").getValue();
                    if (current != null) {
                        current.addAll(result);
                        fullTagMap.get("fallback").postValue(current);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("ComicViewModel", "Failed to load next fallback page", e);
                }
            });
        } else {
            repository.getComicsByTagIdPaging(currentPagingTagId, false, PAGE_SIZE, new ComicRemoteDataSource.FirebasePagingCallback<ComicDtoWithTags>() {
                @Override
                public void onComplete(List<ComicDtoWithTags> result, @Nullable DocumentSnapshot lastVisible) {
                    List<ComicDtoWithTags> current = fullTagMap.get(currentPagingTagId).getValue();
                    if (current != null) {
                        current.addAll(result);
                        fullTagMap.get(currentPagingTagId).postValue(current);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("ComicViewModel", "Failed to load next page for: " + currentPagingTagId, e);
                }
            });
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (bannerListener != null) {
            bannerListener.remove();
        }
        for (ListenerRegistration reg : homeTagListeners.values()) {
            reg.remove();
        }
        homeTagListeners.clear();
        if (comicTopListener != null) {
            comicTopListener.remove();
        }
    }
}

