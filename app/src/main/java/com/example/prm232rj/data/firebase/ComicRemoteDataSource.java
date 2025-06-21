package com.example.prm232rj.data.firebase;

import com.example.prm232rj.data.dto.ComicDtoBanner;
import com.example.prm232rj.data.dto.ComicDtoPreview;
import com.example.prm232rj.data.dto.ComicDtoWithTags;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ComicRemoteDataSource {
    private final FirebaseFirestore db;

    @Inject
    public ComicRemoteDataSource() {
        this.db = FirebaseFirestore.getInstance();
    }
    public void getComicBanners(FirebaseCallback<ComicDtoBanner> callback) {
        db.collection("comics")
                .orderBy("Views", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<ComicDtoBanner> result = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot) {
                        String image = doc.getString("CoverImage");
                        String title = doc.getString("Title");
                        String id = doc.getId();
                        Long viewsObj = doc.getLong("Views");
                        long views = viewsObj != null ? viewsObj : 0;
                        if (image != null && !image.isEmpty() && title != null && !title.isEmpty())
                        {
                            result.add(new ComicDtoBanner(image, title, id, views));
                        }
                    }
                    callback.onComplete(result);
                })
                .addOnFailureListener(callback::onFailure);

    }

    public void getComicPreviews(FirebaseCallback<ComicDtoPreview> callback) {
        db.collection("comics")
                .orderBy("UpdatedAt", Query.Direction.DESCENDING)
                .orderBy("Views", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<ComicDtoPreview> result = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot) {
                        Double rating = doc.getDouble("Rating");
                        String status = doc.getString("Status");
                        String cover = doc.getString("CoverImage");
                        String title = doc.getString("Title");
                        String id = doc.getId();
                        Timestamp timestamp = doc.getTimestamp("UpdatedAt");
                        if (rating != null && title != null && cover != null) {
                            result.add(new ComicDtoPreview(
                                    rating,
                                    title,
                                    cover,
                                    status != null ? status : "",
                                    id,
                                    timestamp
                            ));
                        }
                    }
                    callback.onComplete(result);
                })
                .addOnFailureListener(callback::onFailure);

    }

    public void getComicsByTagIds(List<String> tagIds, FirebaseCallback<ComicDtoWithTags> callback) {
        if (tagIds == null || tagIds.isEmpty()) {
            callback.onComplete(Collections.emptyList());
            return;
        }

        // Giới hạn: Firestore chỉ cho phép max 10 phần tử với whereArrayContainsAny
        if (tagIds.size() > 10) {
            callback.onFailure(new IllegalArgumentException("Không được truyền quá 10 tagId"));
            return;
        }

        db.collection("comics")
                .whereArrayContainsAny("TagId", tagIds)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<ComicDtoWithTags> result = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        ComicDtoWithTags comic = doc.toObject(ComicDtoWithTags.class);
                        result.add(comic);
                    }
                    callback.onComplete(result);
                })
                .addOnFailureListener(callback::onFailure);
    }


    public interface FirebaseCallback<T> {
        void onComplete(List<T> result);
        void onFailure(Exception e);
    }
}


