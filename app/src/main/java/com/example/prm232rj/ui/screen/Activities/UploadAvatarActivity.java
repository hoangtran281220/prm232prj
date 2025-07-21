package com.example.prm232rj.ui.screen.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.prm232rj.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UploadAvatarActivity extends AppCompatActivity {
    private ImageView avatarPreview;
    private Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_avatar);
        try {
            MediaManager.get();
        } catch (IllegalStateException e) {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "dzcj2jbii");
            config.put("api_key", "426512986535137");
            config.put("api_secret", "7lAkCay37so2B9wGElXeDu3ncZo");
            MediaManager.init(this, config);
        }

        avatarPreview = findViewById(R.id.avatarPreview);
        Button btnChooseImage = findViewById(R.id.btnChooseImage);
        Button btnUpload = findViewById(R.id.btnUpload);

        btnChooseImage.setOnClickListener(v -> chooseImage());
        btnUpload.setOnClickListener(v -> uploadImage());
    }


    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    avatarPreview.setImageURI(uri);
                }
            });

    private void chooseImage() {
        imagePickerLauncher.launch("image/*");
    }

    private void uploadImage() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        MediaManager.get().upload(selectedImageUri)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Toast.makeText(UploadAvatarActivity.this, "Uploading...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {

                    }


                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String url = Objects.requireNonNull(resultData.get("secure_url")).toString();
                        updateUserProfile(url);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(UploadAvatarActivity.this, "Upload failed: " + error.getDescription(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        // Retry nếu cần
                    }
                    private void updateUserProfile(String avatarUrl) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user == null) return;

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference userRef = db.collection("User").document(user.getUid());

                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("avatarUrl", avatarUrl);

                        userRef.update(updateData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(UploadAvatarActivity.this, "Avatar updated!", Toast.LENGTH_LONG).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(UploadAvatarActivity.this, "Failed to update Firestore", Toast.LENGTH_LONG).show();
                                });
                    }
                }).dispatch();
    }
}