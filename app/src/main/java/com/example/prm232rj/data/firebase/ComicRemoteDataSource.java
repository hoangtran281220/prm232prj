package com.example.prm232rj.data.firebase;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.prm232rj.data.dto.ChapterReadingDto;
import com.example.prm232rj.data.dto.ComicDtoBanner;
import com.example.prm232rj.data.dto.ComicDtoPreview;
import com.example.prm232rj.data.dto.ComicDtoWithTags;
import com.example.prm232rj.data.dto.TagDto;
import com.example.prm232rj.data.model.Author;
import com.example.prm232rj.data.model.Chapter;
import com.example.prm232rj.data.model.Comic;
import com.example.prm232rj.data.model.RatingResult;
import com.example.prm232rj.data.model.Tag;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ComicRemoteDataSource {
    private final FirebaseFirestore db;
    private static final String TAG = "ComicRemoteDataSource";

    @Inject
    public ComicRemoteDataSource() {
        this.db = FirebaseFirestore.getInstance();
    }

    //test real time firestore
    public ListenerRegistration getComicBanners(Activity activity, FirebaseCallback<ComicDtoBanner> callback) {
        return db.collection("comics")
                .orderBy("Views", Query.Direction.DESCENDING)
                .limit(5)
                .addSnapshotListener(activity, (snapshot, error) -> {
                    if (error != null || snapshot == null) {
                        callback.onFailure(error);
                        return;
                    }

                    List<ComicDtoBanner> result = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot) {
                        String image = doc.getString("CoverImage");
                        String title = doc.getString("Title");
                        String id = doc.getId();
                        Long viewsObj = doc.getLong("Views");
                        long views = viewsObj != null ? viewsObj : 0;

                        if (image != null && !image.isEmpty() && title != null && !title.isEmpty()) {
                            result.add(new ComicDtoBanner(image, title, id, views));
                        }
                    }
                    callback.onComplete(result);
                });
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
                        long ratingCount = doc.getLong("RatingCount");
                        String id = doc.getId();
                        int currentChapter = 0;
                        Number num = doc.get("CurrentChapter", Number.class);
                        if (num != null) {
                            currentChapter = num.intValue();
                        }
                        Timestamp timestamp = doc.getTimestamp("UpdatedAt");
                        if (rating != null && title != null && cover != null) {
                            result.add(new ComicDtoPreview(
                                    rating,
                                    title,
                                    cover,
                                    status != null ? status : "",
                                    id,
                                    timestamp,
                                    currentChapter,
                                    ratingCount
                            ));
                        }
                    }
                    callback.onComplete(result);
                })
                .addOnFailureListener(callback::onFailure);

    }

    //top manga hot ở home page
    public ListenerRegistration getComicsHotTop3(Activity activity, FirebaseCallback<ComicDtoWithTags> callback) {
        return db.collection("comics")
                .orderBy("UpdatedAt", Query.Direction.DESCENDING)
                .orderBy("Views", Query.Direction.DESCENDING)
                .orderBy("Rating", Query.Direction.DESCENDING)
                .limit(9)
                .addSnapshotListener(activity, (snapshot, error) -> {
                    if (error != null) {
                        Log.e("getComicsHotTop3", "Lỗi khi lắng nghe dữ liệu Firestore", error);
                        callback.onFailure(error);
                        return;
                    }

                    if (snapshot != null) {
                        List<ComicDtoWithTags> result = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            ComicDtoWithTags comic = doc.toObject(ComicDtoWithTags.class);
                            if (comic != null) {
                                if (comic.getId() == null) {
                                    comic.setId(doc.getId()); // ✅ Gán document ID vào đối tượng
                                }
                                result.add(comic);
                            }
                        }

                        callback.onComplete(result);
                    } else {
                        callback.onComplete(Collections.emptyList());
                    }
                });
    }

    //list truyện hot
    public void getComicsFallback(@Nullable DocumentSnapshot lastVisible, int pageSize, FirebasePagingCallback<ComicDtoWithTags> callback) {
        Query query = db.collection("comics")
                .orderBy("UpdatedAt", Query.Direction.DESCENDING)
                .orderBy("Views", Query.Direction.DESCENDING)
                .orderBy("Rating", Query.Direction.DESCENDING)
                .limit(pageSize);

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        query.get()
                .addOnSuccessListener(snapshot -> {
                    List<ComicDtoWithTags> result = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        ComicDtoWithTags comic = doc.toObject(ComicDtoWithTags.class);
                        if (comic != null) {
                            if (comic.getId() == null) comic.setId(doc.getId());
                            result.add(comic);
                        }
                    }

                    DocumentSnapshot newLastVisible = snapshot.isEmpty() ? null : snapshot.getDocuments().get(snapshot.size() - 1);
                    callback.onComplete(result, newLastVisible);
                })
                .addOnFailureListener(callback::onFailure);
    }

    //list paging(phân trang)
    public void getComicsByTagIdPaging(String tagId, @Nullable DocumentSnapshot lastVisible, int pageSize, FirebasePagingCallback<ComicDtoWithTags> callback) {
        Query query = db.collection("comics")
                .whereArrayContains("TagId", tagId)
                .orderBy("Rating", Query.Direction.DESCENDING)
                .orderBy("Views", Query.Direction.DESCENDING)
                .orderBy("UpdatedAt", Query.Direction.DESCENDING)
                .limit(pageSize);

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        query.get()
                .addOnSuccessListener(snapshot -> {
                    List<ComicDtoWithTags> result = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        ComicDtoWithTags comic = doc.toObject(ComicDtoWithTags.class);
                        if (comic != null) {
                            comic.setId(doc.getId());
                            result.add(comic);
                        }
                    }

                    DocumentSnapshot newLast = snapshot.isEmpty() ? null : snapshot.getDocuments().get(snapshot.size() - 1);
                    callback.onComplete(result, newLast);
                })
                .addOnFailureListener(callback::onFailure);
    }
    //filter
    public void getFilteredComics(List<String> tagIds, String status, String sortBy,
                                  @Nullable DocumentSnapshot lastVisible, int pageSize,
                                  FirebasePagingCallback<ComicDtoWithTags> callback) {
        Query query = db.collection("comics");

        // 1. Filter theo tagIds nếu có
        if (tagIds != null && !tagIds.isEmpty()) {
            if (tagIds.size() > 10) {
                callback.onFailure(new IllegalArgumentException("Không được truyền quá 10 tagId"));
                return;
            }
            query = query.whereArrayContainsAny("TagId", tagIds);
        }

        // 2. Filter theo status nếu không phải là "Tất cả"
        if (status != null && !status.equalsIgnoreCase("all")) {
            query = query.whereEqualTo("Status", status);
        }

        // 3. Order by duy nhất theo sortBy
        switch (sortBy != null ? sortBy : "") {
            case "Rating":
                query = query.orderBy("Rating", Query.Direction.DESCENDING);
                break;
            case "Views":
                query = query.orderBy("Views", Query.Direction.DESCENDING);
                break;
            case "UpdatedAt":
            default:
                query = query.orderBy("UpdatedAt", Query.Direction.DESCENDING);
                break;
        }

        // 4. Pagination
        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        query = query.limit(pageSize);

        // 5. Execute
        query.get()
                .addOnSuccessListener(snapshot -> {
                    List<ComicDtoWithTags> result = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        ComicDtoWithTags comic = doc.toObject(ComicDtoWithTags.class);
                        if (comic != null) {
                            comic.setId(doc.getId());
                            result.add(comic);
                        }
                    }
                    DocumentSnapshot newLastVisible = snapshot.isEmpty() ? null : snapshot.getDocuments().get(snapshot.size() - 1);
                    callback.onComplete(result, newLastVisible);
                })
                .addOnFailureListener(callback::onFailure);
    }



    //cho list để get top manga theo từng tagid
    public ListenerRegistration getComicsByTagIdTop(Activity activity, String tagId, FirebaseCallback<ComicDtoWithTags> callback) {
        return db.collection("comics")
                .whereArrayContains("TagId", tagId)
                .orderBy("Rating", Query.Direction.DESCENDING)
                .orderBy("Views", Query.Direction.DESCENDING)
                .orderBy("UpdatedAt", Query.Direction.DESCENDING)
                .limit(8)
                .addSnapshotListener(activity, (snapshot, error) -> {
                    if (error != null) {
                        callback.onFailure(error);
                        return;
                    }

                    if (snapshot != null && !snapshot.isEmpty()) {
                        List<ComicDtoWithTags> result = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            ComicDtoWithTags comic = doc.toObject(ComicDtoWithTags.class);
                            if (comic != null) {
                                if (comic.getId() == null) {
                                    comic.setId(doc.getId()); // Gán document ID vào đối tượng
                                }
                                result.add(comic);
                            }
                        }
                        callback.onComplete(result);
                    } else {
                        callback.onComplete(Collections.emptyList());
                    }
                });
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

    public void getChapterByIdForReading(String comicId, String chapterId, FirebaseCallback<ChapterReadingDto> callback) {
        db.collection("chapters").document(chapterId)
                .get()
                .addOnSuccessListener(document -> {
                    List<ChapterReadingDto> result = new ArrayList<>();
                    if (document.exists()) {
                        String id = document.getId();
                        Long chapterNumber = document.getLong("ChapterNumber");
                        String chapterTitle = document.getString("ChapterTitle");
                        String comic = document.getString("ComicId");
                        List<String> contentImages = (List<String>) document.get("Content");
                        Long viewsObj = document.getLong("Views");
                        long views = viewsObj != null ? viewsObj : 0;

                        if (chapterNumber != null && chapterTitle != null && comic != null && contentImages != null) {
                            result.add(new ChapterReadingDto(id, chapterNumber.intValue(), chapterTitle, comic, contentImages, views));
                        }
                    } else {
                        Log.w(TAG, "Chapter not found: " + chapterId);
                    }
                    callback.onComplete(result);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching chapter " + chapterId + ": " + e.getMessage());
                    callback.onFailure(e);
                });
    }


    public void getAllChapters(String comicId, FirebaseCallback<ChapterReadingDto> callback) {
        db.collection("chapters")
                .whereEqualTo("ComicId", comicId)
                .orderBy("ChapterNumber", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<ChapterReadingDto> result = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot) {
                        String id = doc.getId();
                        Long chapterNumber = doc.getLong("ChapterNumber");
                        String chapterTitle = doc.getString("ChapterTitle");
                        String comic = doc.getString("ComicId");
                        List<String> contentImages = (List<String>) doc.get("Content");

                        Long viewsObj = doc.getLong("Views");
                        long views = viewsObj != null ? viewsObj : 0;

                        if (chapterNumber != null && chapterTitle != null && comic != null && contentImages != null) {
                            result.add(new ChapterReadingDto(id, chapterNumber.intValue(), chapterTitle, comic, contentImages, views));
                        }
                    }
                    Log.d(TAG, "Fetched " + result.size() + " chapters for comic " + comicId);
                    callback.onComplete(result);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching chapters for " + comicId + ": " + e.getMessage());
                    callback.onFailure(e);
                });
    }

    public void getAllTags(FirebaseCallback<TagDto> callback) {
        db.collection("Tags")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<TagDto> tagList = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        String id = doc.getId();
                        String name = doc.getString("Name");

                        if (name != null) {
                            tagList.add(new TagDto(id, name));
                        }
                    }
                    callback.onComplete(tagList);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getComicsByIds(List<String> comicIds, FirebaseCallback<ComicDtoPreview> callback) {
        if (comicIds == null || comicIds.isEmpty()) {
            callback.onComplete(new ArrayList<>());
            return;
        }

        List<ComicDtoPreview> allResults = new ArrayList<>();
        List<List<String>> batches = new ArrayList<>();
        for (int i = 0; i < comicIds.size(); i += 10) {
            int end = Math.min(i + 10, comicIds.size());
            batches.add(comicIds.subList(i, end));
        }

        int totalBatches = batches.size();
        int[] completed = {0};
        boolean[] hasFailed = {false};

        for (List<String> batch : batches) {
            db.collection("comics")
                    .whereIn(FieldPath.documentId(), batch)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            ComicDtoPreview comic = doc.toObject(ComicDtoPreview.class);
                            if (comic != null) {
                                comic.setId(doc.getId());
                                allResults.add(comic);
                            }
                        }
                        completed[0]++;
                        if (completed[0] == totalBatches && !hasFailed[0]) {
                            allResults.sort(Comparator.comparingInt(c -> comicIds.indexOf(c.getId())));
                            callback.onComplete(allResults);
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (!hasFailed[0]) {
                            hasFailed[0] = true;
                            callback.onFailure(e);
                        }
                    });
        }
    }
    //danh sách follow
    public ListenerRegistration observeFollowedComicsRealtime(String userId, FirebaseCallback<ComicDtoPreview> callback) {
        return db.collection("User")
                .document(userId)
                .collection("followed_comics")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null || snapshot == null) {
                        callback.onFailure(e);
                        return;
                    }

                    List<String> comicIds = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        comicIds.add(doc.getId()); // dùng ID document là comicId
                    }

                    // Truy xuất chi tiết comic từ danh sách ID
                    getComicsByIds(comicIds, new ComicRemoteDataSource.FirebaseCallback<>() {
                        @Override
                        public void onComplete(List<ComicDtoPreview> result) {
                            callback.onComplete(result);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            callback.onFailure(e);
                        }
                    });
                });
    }

    //cập nhật views
    public void incrementComicViews(String comicId) {
        DocumentReference comicRef = db.collection("comics").document(comicId);
        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(comicRef);
            Long currentViews = snapshot.getLong("Views");
            if (currentViews == null) currentViews = 0L;
            transaction.update(comicRef, "Views", currentViews + 1);
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "Đã cập nhật views cho comic: " + comicId);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Lỗi cập nhật views comic " + comicId + ": " + e.getMessage());
        });
    }

    public void incrementChapterViews(String chapterId) {
        DocumentReference chapterRef = db.collection("chapters").document(chapterId);
        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(chapterRef);
            Long currentViews = snapshot.getLong("Views");
            if (currentViews == null) currentViews = 0L;
            transaction.update(chapterRef, "Views", currentViews + 1);
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "Đã cập nhật views cho chapter: " + chapterId);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Lỗi cập nhật views chapter " + chapterId + ": " + e.getMessage());
        });
    }

    public void rateComic(String comicId, String userId, double newRating, OnCompleteListener<RatingResult> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference comicRef = db.collection("comics").document(comicId);
        DocumentReference userRatingRef = comicRef.collection("userRatings").document(userId);

        db.runTransaction(transaction -> {
            // Lấy dữ liệu comic
            DocumentSnapshot comicSnap = transaction.get(comicRef);
            if (!comicSnap.exists()) {
                throw new FirebaseFirestoreException("Comic not found",
                        FirebaseFirestoreException.Code.NOT_FOUND);
            }

            // Lấy rating hiện tại của comic
            double ratingAvg = comicSnap.contains("Rating") ? comicSnap.getDouble("Rating") : 0.0;
            long ratingCount = comicSnap.contains("RatingCount") ? comicSnap.getLong("RatingCount") : 0;

            // Lấy dữ liệu rating trước của user
            DocumentSnapshot userRatingSnap = transaction.get(userRatingRef);
            double updatedAvg;

            if (userRatingSnap.exists()) {
                // Re-rating
                double oldRating = userRatingSnap.getDouble("rating");
                updatedAvg = ((ratingAvg * ratingCount) - oldRating + newRating) / ratingCount;
            } else {
                // Rating lần đầu
                updatedAvg = ((ratingAvg * ratingCount) + newRating) / (ratingCount + 1);
                ratingCount += 1;
            }

            // Ghi user rating
            Map<String, Object> userRating = new HashMap<>();
            userRating.put("rating", newRating);
            userRating.put("timestamp", FieldValue.serverTimestamp());
            transaction.set(userRatingRef, userRating);

            // Cập nhật comic
            Map<String, Object> comicUpdate = new HashMap<>();
            comicUpdate.put("Rating", updatedAvg);
            comicUpdate.put("RatingCount", ratingCount);
            transaction.update(comicRef, comicUpdate);

            return new RatingResult(updatedAvg, ratingCount);
        }).addOnCompleteListener(listener);
    }



    public interface FirebaseCallback<T> {
        void onComplete(List<T> result);
        void onFailure(Exception e);
    }

    public interface FirebasePagingCallback<T> {
        void onComplete(List<T> result, @Nullable DocumentSnapshot lastVisible);
        void onFailure(Exception e);
    }
    public interface RealtimeComicCallback {
        void onSuccess(List<ComicDtoPreview> comics);
        void onFailure(Exception e);
    }

}


