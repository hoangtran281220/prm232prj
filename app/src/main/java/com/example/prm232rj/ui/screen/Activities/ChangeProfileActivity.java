package com.example.prm232rj.ui.screen.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.prm232rj.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangeProfileActivity extends AppCompatActivity {

    private EditText fullNameInput, phoneInput;
    private Button btnSave;
    private ImageView avatarImage;
    private TextView emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        fullNameInput = findViewById(R.id.fullNameInput);
        phoneInput = findViewById(R.id.phoneInput);
        avatarImage = findViewById(R.id.avatarImage);
        emailText = findViewById(R.id.emailText);

        loadUserProfile();
    }

    private void loadUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("User").document(user.getUid());

        userRef.get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String fullName = doc.getString("Username");
                String phone = doc.getString("Email");
                String avatarUrl = doc.getString("avatarUrl");

                fullNameInput.setText(fullName);
                phoneInput.setText(phone);
                emailText.setText(user.getEmail());

                Glide.with(this)
                        .load(avatarUrl)
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.logo)
                        .apply(RequestOptions.circleCropTransform())
                        .into(avatarImage);
            }
        });
    }

    private void updateUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String newName = fullNameInput.getText().toString().trim();
        String newPhone = phoneInput.getText().toString().trim();

        DocumentReference userRef = FirebaseFirestore.getInstance()
                .collection("User")
                .document(user.getUid());

        userRef.update("Username", newName, "Email", newPhone)
                .addOnSuccessListener(unused -> {
                    btnSave.setVisibility(View.GONE);
                    fullNameInput.setEnabled(false);
                    phoneInput.setEnabled(false);
                });
    }
}
