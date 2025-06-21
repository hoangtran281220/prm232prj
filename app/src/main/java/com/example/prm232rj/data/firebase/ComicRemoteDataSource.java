package com.example.prm232rj.data.firebase;

import com.example.prm232rj.data.dto.ComicDtoBanner;
import com.example.prm232rj.data.dto.ComicDtoPreview;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
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

                        if (image != null && !image.isEmpty() && title != null && !title.isEmpty()) {
                            result.add(new ComicDtoBanner(image, title));
                        }
                    }
                    callback.onComplete(result);
                })
                .addOnFailureListener(e -> {
                    // Trả về danh sách rỗng nếu có lỗi
                    callback.onComplete(new ArrayList<>());
                });
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
                                    status != null ? status : "",
                                    cover,
                                    title
                            ));
                        }
                    }
                    callback.onComplete(result);
                })
                .addOnFailureListener(e -> {
                    // Có thể log hoặc trả list rỗng
                    callback.onComplete(new ArrayList<>());
                });
    }

    public interface FirebaseCallback<T> {
        void onComplete(List<T> result);
    }
}


