package com.example.prm232rj.data.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FieldValue;
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
    //register người dùng với username, password
    public void registerWithUsernameOnly(String username, String password, RegisterCallback callback) {
        String fakeEmail = username + "@fakeapp.com"; //email ảo để lưu trên firebase auth

        //tạo email ảo
        auth.createUserWithEmailAndPassword(fakeEmail, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser == null) {
                            callback.onFailure("Không lấy được thông tin người dùng");
                            return;
                        }
                        //lấy uid từ auth
                        String uid = firebaseUser.getUid();

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("Username", username);
                        userMap.put("RoleId", 2);
                        userMap.put("CreatedAt", System.currentTimeMillis());
                        userMap.put("UpdateAt", System.currentTimeMillis());
                        userMap.put("isEmailLinked", false);
                        userMap.put("linkedProvider", "password");
                        //tạo user trong firestore
                        db.collection("User").document(uid)
                                .set(userMap)
                                .addOnSuccessListener(unused -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onFailure("Lưu thông tin thất bại: " + e.getMessage()));

                    } else {
                        callback.onFailure("Đăng ký thất bại: " + task.getException().getMessage());
                    }
                });
    }

    // Direct Firestore login method (your existing logic)
    public void loginWithFirestore(String username, String password, LoginCallback callback) {
        db.collection("User")
                .whereEqualTo("Username", username)
                .whereEqualTo("Password", password)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String displayName = document.getString("Username");
                        String documentId = document.getId();
                        callback.onSuccess(documentId, displayName);
                    } else {
                        callback.onFailure("Sai tên đăng nhập hoặc mật khẩu");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure("Lỗi khi đăng nhập: " + e.getMessage()));
    }

    // Google Sign-in method
    public void signInWithGoogle(String idToken, GoogleSignInCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            checkAndCreateGoogleUser(user, callback);
                        } else {
                            callback.onFailure("Không lấy được thông tin người dùng");
                        }
                    } else {
                        callback.onFailure("Đăng nhập Firebase thất bại");
                    }
                });
    }

    private void checkAndCreateGoogleUser(FirebaseUser user, GoogleSignInCallback callback) {
        String uid = user.getUid();

        db.collection("User").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("Username", user.getDisplayName());
                        userMap.put("Email", user.getEmail());
                        userMap.put("RoleId", 1);
                        userMap.put("avatarUrl", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");
                        userMap.put("isEmailLinked", true);
                        userMap.put("linkedProvider", "google");
                        userMap.put("CreatedAt", FieldValue.serverTimestamp());
                        userMap.put("UpdateAt", FieldValue.serverTimestamp());

                        db.collection("User").document(uid)
                                .set(userMap)
                                .addOnSuccessListener(aVoid -> callback.onSuccess(uid, user.getDisplayName(), user.getEmail(), user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null))
                                .addOnFailureListener(e -> callback.onFailure("Lỗi tạo user Firestore"));
                    } else {
                        callback.onSuccess(uid, user.getDisplayName(), user.getEmail(), user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
                    }
                })
                .addOnFailureListener(e -> callback.onFailure("Lỗi kiểm tra user: " + e.getMessage()));
    }

    // Interfaces
    public interface RegisterCallback {
        void onSuccess();
        void onFailure(String message);
    }

    public interface LoginCallback {
        void onSuccess(String documentId, String username);

        void onSuccess(String uid, String displayName, String email, String photoUrl);

        void onFailure(String message);
    }

    public interface GoogleSignInCallback {
        void onSuccess(String uid, String displayName, String email, String photoUrl);
        void onFailure(String message);
    }
}
