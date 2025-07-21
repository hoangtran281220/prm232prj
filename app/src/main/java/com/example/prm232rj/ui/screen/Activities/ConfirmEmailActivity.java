package com.example.prm232rj.ui.screen.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm232rj.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ConfirmEmailActivity extends AppCompatActivity {
    private EditText emailInput, otpInput;
    private Button sendCodeBtn, confirmBtn;

    private FirebaseFirestore db;
    private String userId;
    private String generatedOtp;
    private boolean isEmailLinkedInDB = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_email);
        emailInput = findViewById(R.id.emailInput);
        otpInput = findViewById(R.id.otpInput);
        sendCodeBtn = findViewById(R.id.sendCodeBtn);
        confirmBtn = findViewById(R.id.confirmBtn);
        db = FirebaseFirestore.getInstance();
        String username = getSharedPreferences("USER_PREF", MODE_PRIVATE).getString("username", null);

        if (username == null || username.isEmpty()) {
            Log.d("ConfirmEmail", "Username from SharedPreferences: " + username);
            Toast.makeText(this, "Không tìm thấy tên người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        db.collection("User")
                .whereEqualTo("Username", username)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        DocumentSnapshot document = snapshot.getDocuments().get(0);
                        userId = document.getId();
                        loadUserFromFirestore(document);
                    } else {
                        Toast.makeText(this, "Không tìm thấy người dùng trong hệ thống", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi truy vấn người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
    private void loadUserFromFirestore(DocumentSnapshot snapshot) {
        if (snapshot.exists()) {
            String email = snapshot.getString("Email");
            Boolean isLinked = snapshot.getBoolean("isEmailLinked");
            isEmailLinkedInDB = (isLinked != null && isLinked);
            String linkedProvider = snapshot.getString("linkedProvider");
            emailInput.setText(email != null ? email : "");

            if ( "google.com".equals(linkedProvider) || "google".equals(linkedProvider)) {
                emailInput.setEnabled(false);
                sendCodeBtn.setEnabled(false);
                Toast.makeText(this, "Email đã được xác minh", Toast.LENGTH_SHORT).show();
            } else {
                setupOtpFlow();
            }
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private void setupOtpFlow() {
        sendCodeBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }
            generatedOtp = generateOtp();
            checkEmailBeforeSendingOtp(email);
        });

        confirmBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String enteredOtp = otpInput.getText().toString().trim();

            if (!enteredOtp.equals(generatedOtp)) {
                Toast.makeText(this, "Mã xác thực không đúng", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("USER_PREF", MODE_PRIVATE);
            String storedPassword = prefs.getString("password", null);
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (firebaseUser == null || storedPassword == null) {
                Toast.makeText(this, "Không xác định được người dùng hiện tại hoặc thiếu mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            String providerId = firebaseUser.getProviderData().get(1).getProviderId(); // bỏ [0] vì là anonymous record

            if (!"password".equals(providerId)) {
                Toast.makeText(this, "Tài khoản này không hỗ trợ cập nhật email (Google Login)" +providerId, Toast.LENGTH_LONG).show();
                return;
            }

            // ✅ Xác thực lại bằng email/password
            String currentEmail = firebaseUser.getEmail();
            AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, storedPassword);

            firebaseUser.reauthenticate(credential)
                    .addOnSuccessListener(authResult -> {
                        firebaseUser.updateEmail(email)
                                .addOnSuccessListener(aVoid -> {
                                    db.collection("User").document(userId)
                                            .update("Email", email, "isEmailLinked", true, "linkedProvider", "email")
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(this, "Xác nhận email thành công", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(this, "Lỗi cập nhật Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                            );
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Lỗi cập nhật email Firebase Auth: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Xác thực thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

    }
    private void checkEmailBeforeSendingOtp(String email) {
        db.collection("User")
                .whereEqualTo("Email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    boolean emailInUseByOtherUser = false;
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String foundUid = doc.getId();
                        if (!foundUid.equals(userId)) {
                            emailInUseByOtherUser = true;
                            break;
                        }
                    }

                    if (emailInUseByOtherUser) {
                        Toast.makeText(this, "Email này đã được dùng bởi tài khoản khác", Toast.LENGTH_LONG).show();
                    } else {
                        generatedOtp = generateOtp();

                        new Thread(() -> {
                            try {
                                sendEmail(email, generatedOtp);
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "OTP đã gửi đến email", Toast.LENGTH_SHORT).show();
                                    otpInput.setVisibility(View.VISIBLE);
                                    confirmBtn.setVisibility(View.VISIBLE);
                                });
                            } catch (MessagingException e) {
                                e.printStackTrace();
                                runOnUiThread(() ->
                                        Toast.makeText(this, "Gửi OTP thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                );
                            }
                        }).start();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi kiểm tra email: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    private void sendOtpToEmail(String email, String otp) {
        // TODO: Gửi OTP thật (hiện tại demo)
        Toast.makeText(this, "OTP: " + otp + " (demo)", Toast.LENGTH_LONG).show();
    }
    private void sendEmail(String toEmail, String otp) throws MessagingException {
        final String fromEmail = "voanhhoangpw2112@gmail.com"; // Gmail bạn dùng để gửi
        final String password = "ouvh hnua hswf ayuh"; // App password (không phải mật khẩu Gmail thường)

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, password);
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("OTP xác thực từ PRM232");
        message.setText("Mã OTP của bạn là: " + otp);

        Transport.send(message);
    }
}