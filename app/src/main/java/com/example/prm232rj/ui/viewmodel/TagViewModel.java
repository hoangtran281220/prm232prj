package com.example.prm232rj.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prm232rj.data.dto.TagDto;
import com.example.prm232rj.data.firebase.ComicRemoteDataSource;
import com.example.prm232rj.data.repository.TagRepository;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TagViewModel extends ViewModel {
    private final MutableLiveData<List<TagDto>> tagsLiveData = new MutableLiveData<>();
    private final TagRepository tagRepository;
    @Inject
    public TagViewModel(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public LiveData<List<TagDto>> getTags() {
        // Nếu chưa có dữ liệu thì load
        if (tagsLiveData.getValue() == null) {
            loadTags();
        }
        return tagsLiveData;
    }

    public void refreshTags() {
        loadTags();
    }
    private void loadTags() {
        tagRepository.getAllTags(new ComicRemoteDataSource.FirebaseCallback<TagDto>() {
            @Override
            public void onComplete(List<TagDto> result) {
                tagsLiveData.setValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("TagViewModel", "Lỗi khi load tags: " + e.getMessage(), e);
                tagsLiveData.setValue(Collections.emptyList());
            }
        });
    }
}
