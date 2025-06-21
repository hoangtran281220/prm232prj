package com.example.prm232rj.data.firebase;

import com.example.prm232rj.data.dto.ComicDtoBanner;
import com.example.prm232rj.data.dto.ComicDtoPreview;
import com.example.prm232rj.data.dto.ComicDtoWithTags;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        db.collection("comics").get()
                .addOnSuccessListener(snapshot -> {
                    List<ComicDtoBanner> result = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot) {
                        String image = doc.getString("CoverImage");
                        String title = doc.getString("Title");

                        if (image != null && !image.isEmpty() && title != null && !title.isEmpty())
                        {
                            result.add(new ComicDtoBanner(image, title));
                        }
                    }
                    callback.onComplete(result);
                })
                .addOnFailureListener(callback::onFailure);

    }

    public void getComicPreviews(FirebaseCallback<ComicDtoPreview> callback) {
        db.collection("comics").get()
                .addOnSuccessListener(snapshot -> {
                    List<ComicDtoPreview> result = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot) {
                        Double rating = doc.getDouble("Rating");
                        String status = doc.getString("Status");
                        String cover = doc.getString("CoverImage");
                        String title = doc.getString("Title");

                        if (rating != null && title != null && cover != null) {
                            result.add(new ComicDtoPreview(
                                    rating,
                                    title,
                                    cover,
                                    status != null ? status : ""
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


