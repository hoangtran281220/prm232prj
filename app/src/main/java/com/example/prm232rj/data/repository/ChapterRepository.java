package com.example.prm232rj.data.repository;

import com.example.prm232rj.data.model.Chapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ChapterRepository {
    private final FirebaseFirestore db;

    public interface FirebaseCallback<T> {
        void onComplete(List<T> result);
        void onFailure(Exception e);
    }

    @Inject
    public ChapterRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void getChaptersByComicId(String comicId, FirebaseCallback<Chapter> callback) {
        db.collection("chapters")
                .whereEqualTo("ComicId", comicId)
                .orderBy("ChapterNumber", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Chapter> chapters = new ArrayList<>();
                    queryDocumentSnapshots.forEach(doc -> {
                        Chapter chapter = doc.toObject(Chapter.class);
                        chapter.setId(doc.getId()); // Set document ID
                        chapters.add(chapter);
                    });
                    callback.onComplete(chapters);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getChapterById(String chapterId, FirebaseCallback<Chapter> callback) {
        db.collection("chapters")
                .document(chapterId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Chapter chapter = doc.toObject(Chapter.class);
                        if (chapter != null) {
                            chapter.setId(doc.getId());
                        }
                        List<Chapter> result = new ArrayList<>();
                        result.add(chapter);
                        callback.onComplete(result);
                    } else {
                        callback.onFailure(new Exception("Không tìm thấy chương với id: " + chapterId));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void increaseViews(String chapterId) {
        db.collection("chapters")
                .document(chapterId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Long currentViews = doc.getLong("Views");
                        long newViews = (currentViews != null ? currentViews : 0) + 1;

                        db.collection("chapters")
                                .document(chapterId)
                                .update("Views", newViews);
                    }
                });
    }

    public void getNextChapter(String comicId, int currentChapterNumber, FirebaseCallback<Chapter> callback) {
        db.collection("chapters")
                .whereEqualTo("ComicId", comicId)
                .whereGreaterThan("ChapterNumber", currentChapterNumber)
                .orderBy("ChapterNumber", Query.Direction.ASCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Chapter> chapters = new ArrayList<>();
                    queryDocumentSnapshots.forEach(doc -> {
                        Chapter chapter = doc.toObject(Chapter.class);
                        chapter.setId(doc.getId());
                        chapters.add(chapter);
                    });
                    callback.onComplete(chapters);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getPreviousChapter(String comicId, int currentChapterNumber, FirebaseCallback<Chapter> callback) {
        db.collection("chapters")
                .whereEqualTo("ComicId", comicId)
                .whereLessThan("ChapterNumber", currentChapterNumber)
                .orderBy("ChapterNumber", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Chapter> chapters = new ArrayList<>();
                    queryDocumentSnapshots.forEach(doc -> {
                        Chapter chapter = doc.toObject(Chapter.class);
                        chapter.setId(doc.getId());
                        chapters.add(chapter);
                    });
                    callback.onComplete(chapters);
                })
                .addOnFailureListener(callback::onFailure);
    }
}