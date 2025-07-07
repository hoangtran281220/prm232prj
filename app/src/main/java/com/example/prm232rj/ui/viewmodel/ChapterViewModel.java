package com.example.prm232rj.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.prm232rj.data.dto.ChapterReadingDto;
import com.example.prm232rj.data.dto.ChapterReadingDto;
import com.example.prm232rj.data.repository.ComicRepository;
import com.example.prm232rj.data.firebase.ComicRemoteDataSource.FirebaseCallback;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChapterViewModel extends ViewModel {
    private final ComicRepository repository;
    private final MutableLiveData<ChapterReadingDto> currentChapter = new MutableLiveData<>();
    private final MutableLiveData<List<ChapterReadingDto>> chapters = new MutableLiveData<>();
    private String comicId;

    @Inject
    public ChapterViewModel(ComicRepository repository) {
        this.repository = repository;
    }

    public void init(String comicId, String chapterId) {
        this.comicId = comicId;
        loadChapter(chapterId);
        loadAllChapters();
    }

    public LiveData<ChapterReadingDto> getCurrentChapter() {
        return currentChapter;
    }

    public LiveData<List<ChapterReadingDto>> getChapters() {
        return chapters;
    }

    public void loadChapter(String chapterId) {
        repository.getChapterByIdForReading(comicId, chapterId, new FirebaseCallback<>() {
            @Override
            public void onComplete(List<ChapterReadingDto> result) {
                if (!result.isEmpty()) {
                    currentChapter.postValue(result.get(0));
                }
            }

            @Override
            public void onFailure(Exception e) {
                currentChapter.postValue(null);
            }
        });
    }

    public void loadAllChapters() {
        repository.getAllChapters(comicId, new FirebaseCallback<>() {
            @Override
            public void onComplete(List<ChapterReadingDto> result) {
                chapters.postValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                chapters.postValue(new ArrayList<>());
            }
        });
    }

    public void loadNextChapter() {
        ChapterReadingDto current = currentChapter.getValue();
        List<ChapterReadingDto> chapterList = chapters.getValue();
        if (current != null && chapterList != null) {
            for (int i = 0; i < chapterList.size(); i++) {
                if (chapterList.get(i).getChapterId().equals(current.getChapterId()) && i < chapterList.size() - 1) {
                    loadChapter(chapterList.get(i + 1).getChapterId());
                    break;
                }
            }
        }
    }

    public void loadPreviousChapter() {
        ChapterReadingDto current = currentChapter.getValue();
        List<ChapterReadingDto> chapterList = chapters.getValue();
        if (current != null && chapterList != null) {
            for (int i = 0; i < chapterList.size(); i++) {
                if (chapterList.get(i).getChapterId().equals(current.getChapterId()) && i > 0) {
                    loadChapter(chapterList.get(i - 1).getChapterId());
                    break;
                }
            }
        }
    }
}