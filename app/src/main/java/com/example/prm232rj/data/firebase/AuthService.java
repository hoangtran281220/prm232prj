package com.example.prm232rj.data.firebase;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.prm232rj.data.dto.ComicDtoPreview;
import com.example.prm232rj.data.model.Comic;
import com.example.prm232rj.data.model.User;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthService {
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private Context mContext;
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
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        String linkedProvider = document.getString("linkedProvider");

                        // Xác định email nào sẽ dùng để đăng nhập
                        String loginEmail;
                        if ("email".equals(linkedProvider)) {
                            loginEmail = document.getString("Email"); // Lấy email thật từ Firestore
                            if (loginEmail == null || loginEmail.isEmpty()) {
                                callback.onFailure("Tài khoản không có email thật.");
                                return;
                            }
                        } else if ("password".equals(linkedProvider)) {
                            loginEmail = username + "@fakeapp.com"; // email ảo
                        } else {
                            callback.onFailure("Loại tài khoản không được hỗ trợ.");
                            return;
                        }

                        // Đăng nhập với email xác định và password
                        auth.signInWithEmailAndPassword(loginEmail, password)
                                .addOnSuccessListener(authResult -> {
                                    FirebaseUser firebaseUser = auth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        callback.onSuccess(firebaseUser.getUid(), username);
                                    } else {
                                        callback.onFailure("Không lấy được người dùng sau khi đăng nhập.");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    callback.onFailure("Sai mật khẩu hoặc lỗi đăng nhập: " + e.getMessage());
                                });
                    } else {
                        callback.onFailure("Không tìm thấy người dùng.");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure("Lỗi truy vấn Firestore: " + e.getMessage());
                });
    }

    private void saveUserToPref(User user) {
        SharedPreferences userPrefs;
        userPrefs = this.mContext.getSharedPreferences("USER_PREF", MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putString("Email", user.Email != null ? user.Email : "");
        editor.putString("Username", user.Username != null ? user.Username : "");
        editor.putString("avatarUrl", user.avatarUrl != null ? user.avatarUrl : "");
        editor.putInt("RoleId", user.RoleId != 0 ? user.RoleId : 0);
        // isEmailLinked
        editor.putBoolean("isEmailLinked", user.isEmailLinked );
        // linkedProvider
        editor.putString("linkedProvider", user.linkedProvider != null ? user.linkedProvider : "");

        if (user.Password != null) {
            editor.putString("Password", user.Password);
        }
        editor.apply();
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

    public void followComic(String userId, String comicId, FirebaseCallbackUser<Void> callback) {
        Map<String, Object> followData = new HashMap<>();
        followData.put("comicId", comicId);
        followData.put("followedAt", System.currentTimeMillis());

        db.collection("User")
                .document(userId)
                .collection("followed_comics")
                .document(comicId)
                .set(followData)
                .addOnSuccessListener(unused -> callback.onComplete(null))
                .addOnFailureListener(callback::onFailure);
    }

    public void unfollowComic(String userId, String comicId, FirebaseCallbackUser<Void> callback) {
        FirebaseFirestore.getInstance()
                .collection("User")
                .document(userId)
                .collection("followed_comics")
                .document(comicId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onComplete(null))
                .addOnFailureListener(callback::onFailure);
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

    public interface FirebaseCallbackUser<T> {
        void onComplete(T result);
        void onFailure(Exception e);
    }


}
