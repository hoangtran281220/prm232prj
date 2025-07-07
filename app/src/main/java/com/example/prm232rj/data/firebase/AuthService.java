package com.example.prm232rj.data.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthService {
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    @Inject
    public AuthService() {
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    public void registerWithUsernameOnly(String username, String password, RegisterCallback callback) {
        String fakeEmail = username + "@fakeapp.com";

        auth.createUserWithEmailAndPassword(fakeEmail, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser == null) {
                            callback.onFailure("Không lấy được thông tin người dùng");
                            return;
                        }

                        String uid = firebaseUser.getUid();

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("Username", username);
                        userMap.put("RoleId", 2);
                        userMap.put("CreatedAt", System.currentTimeMillis());
                        userMap.put("UpdateAt", System.currentTimeMillis());
                        userMap.put("isEmailLinked", false);
                        userMap.put("linkedProvider", "password");

                        db.collection("User").document(uid)
                                .set(userMap)
                                .addOnSuccessListener(unused -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onFailure("Lưu thông tin thất bại: " + e.getMessage()));

                    } else {
                        callback.onFailure("Đăng ký thất bại: " + task.getException().getMessage());
                    }
                });
    }







    public interface RegisterCallback {
        void onSuccess();
        void onFailure(String message);
    }
}
