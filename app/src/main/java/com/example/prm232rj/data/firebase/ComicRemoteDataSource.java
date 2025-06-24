package com.example.prm232rj.data.firebase;

import android.util.Log;

import com.example.prm232rj.data.dto.ComicDtoBanner;
import com.example.prm232rj.data.dto.ComicDtoPreview;
import com.example.prm232rj.data.dto.ComicDtoWithTags;
import com.example.prm232rj.data.model.Author;
import com.example.prm232rj.data.model.Chapter;
import com.example.prm232rj.data.model.Comic;
import com.example.prm232rj.data.model.Tag;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

    public void getComicsHotTop3(FirebaseCallback<ComicDtoWithTags> callback){
        db.collection("comics")
                .orderBy("UpdatedAt", Query.Direction.DESCENDING)
                .orderBy("Views", Query.Direction.DESCENDING)
                .orderBy("Rating", Query.Direction.DESCENDING)
                .limit(3) // Có thể giới hạn số lượng
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<ComicDtoWithTags> result = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        ComicDtoWithTags comic = doc.toObject(ComicDtoWithTags.class);
                        result.add(comic);
                    }
                    callback.onComplete(result);
                })
                .addOnFailureListener(e -> {
                    Log.e("getComicsByTagIds", "Lỗi khi truy vấn Firestore", e);
                    callback.onFailure(e);
                });
    }

    public void getComicsByTagIds(List<String> tagIds, FirebaseCallback<ComicDtoWithTags> callback) {

        if (tagIds == null || tagIds.isEmpty()) {

            // Trường hợp không có tag: fallback theo UpdatedAt, Rating, View giảm dần
            db.collection("comics")
                    .orderBy("UpdatedAt", Query.Direction.DESCENDING)
                    .orderBy("Views", Query.Direction.DESCENDING)
                    .orderBy("Rating", Query.Direction.DESCENDING)
                    .limit(20) // Có thể giới hạn số lượng
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        List<ComicDtoWithTags> result = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            ComicDtoWithTags comic = doc.toObject(ComicDtoWithTags.class);
                            result.add(comic);
                        }
                        callback.onComplete(result);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("getComicsByTagIds", "Lỗi khi truy vấn Firestore", e);
                        callback.onFailure(e);
                    });
            return;
        }

        // Nếu có tagIds thì dùng whereArrayContainsAny (tối đa 10 phần tử)
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

                    result.sort((a, b) -> {
                        int r = Double.compare(b.getRating(), a.getRating());
                        if (r != 0) return r;

                        r = Long.compare(b.getViews(), a.getViews());
                        if (r != 0) return r;

                        if (b.getUpdatedAt() != null && a.getUpdatedAt() != null)
                            return b.getUpdatedAt().compareTo(a.getUpdatedAt());

                        return 0;
                    });
                    callback.onComplete(result);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getRecentComics(int limit, FirebaseCallback<Comic> callback) {
        db.collection("comics")
                .orderBy("UpdatedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Comic> comics = new ArrayList<>();
                    queryDocumentSnapshots.forEach(doc -> {
                        Comic comic = doc.toObject(Comic.class);
                        comic.setId(doc.getId());
                        comics.add(comic);
                    });
                    callback.onComplete(comics);
                })
                .addOnFailureListener(callback::onFailure);
    }
    // Thêm method để lấy comic theo ID
    public void getComicById(String comicId, FirebaseCallback<Comic> callback) {
        db.collection("comics")
                .document(comicId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Comic comic = doc.toObject(Comic.class);
                        if (comic != null) {
                            comic.setId(doc.getId());
                            List<Comic> result = new ArrayList<>();
                            result.add(comic);
                            callback.onComplete(result);
                        } else {
                            callback.onFailure(new Exception("Không thể chuyển đổi dữ liệu truyện"));
                        }
                    } else {
                        callback.onFailure(new Exception("Không tìm thấy truyện với id: " + comicId));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    //Lấy chapter của comic
    public void getChaptersByComicId(String comicId, FirebaseCallback<Chapter> callback) {
        db.collection("chapters")
                .whereEqualTo("ComicId", comicId)
                .orderBy("ChapterNumber", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(query -> {
                    List<Chapter> chapters = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : query) {
                        Chapter chapter = doc.toObject(Chapter.class);
                        chapter.setId(doc.getId());
                        chapters.add(chapter);
                    }
                    callback.onComplete(chapters);
                })
                .addOnFailureListener(callback::onFailure);
    }
    //Lấy tác giả theo id truyện
    public void getAuthorsByIds(List<String> ids, FirebaseCallback<Author> callback) {
        Log.d("ComicRepository", "getAuthorsByIds called with IDs: " + (ids != null ? ids : "null"));
        if (ids == null || ids.isEmpty()) {
            Log.w("ComicRepository", "Author IDs list is null or empty");
            callback.onComplete(new ArrayList<>());
            return;
        }

        Log.d("ComicRepository", "Executing query for authors with IDs: " + ids);
        db.collection("Authors")
                .whereIn(FieldPath.documentId(), ids)
                .get()
                .addOnSuccessListener(query -> {
                    Log.d("ComicRepository", "Authors query successful, documents found: " + query.size());
                    List<Author> result = new ArrayList<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Log.d("ComicRepository", "Processing document ID: " + doc.getId());
                        Author a = doc.toObject(Author.class);
                        if (a != null) {
                            a.setId(doc.getId());
                            Log.d("ComicRepository", "Author converted: " + (a.getName() != null ? a.getName() : "null") + ", ID: " + doc.getId());
                        } else {
                            Log.w("ComicRepository", "Failed to convert document to Author: " + doc.getId());
                        }
                        result.add(a);
                    }
                    Log.d("ComicRepository", "Total authors loaded: " + result.size());
                    callback.onComplete(result);
                })
                .addOnFailureListener(e -> {
                    Log.e("ComicRepository", "Failed to fetch authors: " + e.getMessage(), e);
                    if (e instanceof FirebaseFirestoreException) {
                        FirebaseFirestoreException fse = (FirebaseFirestoreException) e;
                        Log.e("ComicRepository", "Firestore error code: " + fse.getCode());
                    }
                    callback.onFailure(e);
                });
    }
    //Lấy thể loại theo id truyện
    public void getTagsByIds(List<String> ids, FirebaseCallback<Tag> callback) {
        db.collection("Tags")
                .whereIn(FieldPath.documentId(), ids)
                .get()
                .addOnSuccessListener(query -> {
                    List<Tag> result = new ArrayList<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Tag t = doc.toObject(Tag.class);
                        if (t != null) t.setId(doc.getId());
                        result.add(t);
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


