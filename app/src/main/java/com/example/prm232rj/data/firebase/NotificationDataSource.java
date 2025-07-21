package com.example.prm232rj.data.firebase;

import android.util.Log;

import com.example.prm232rj.data.dto.NotificationDto;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NotificationDataSource {
    private final FirebaseFirestore db;
    private static final String TAG = "ComicRemoteDataSource";


    @Inject
    public NotificationDataSource() {
        this.db = FirebaseFirestore.getInstance();
    }
    public interface FirestoreCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface ValueEventListener<T> {
        void onData(T value);
        void onError(Exception e);
    }

    public void saveNotification(String userId, NotificationDto notification, FirestoreCallback callback) {
        db.collection("users").document(userId).collection("notifications")
                .add(notification)
                .addOnSuccessListener(doc -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void getNotifications(String userId, ValueEventListener<List<NotificationDto>> callback) {
        Log.d("NotificationDataSource", "Đang gọi getNotifications với userId = " + userId);
        db.collection("notifications") // 🔥 Collection gốc
                .whereEqualTo("userId", userId) // 🔥 Lọc theo userId
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("NotificationDataSource", "Lỗi khi lắng nghe snapshot", e);
                        callback.onError(e);
                        return;
                    }

                    if (snapshots == null || snapshots.isEmpty()) {
                        Log.d("NotificationDataSource", "Không có thông báo nào");
                        callback.onData(new ArrayList<>()); // 👈 đừng quên callback ngay cả khi rỗng
                        return;
                    }

                    List<NotificationDto> result = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        NotificationDto dto = doc.toObject(NotificationDto.class);
                        dto.setId(doc.getId());
                        result.add(dto);
                    }

                    Log.d("NotificationDataSource", "Số lượng thông báo lấy được: " + result.size());
                    callback.onData(result);
                });
    }


}