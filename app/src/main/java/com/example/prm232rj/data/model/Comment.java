package com.example.prm232rj.data.model;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;

public class Comment {
    private String id;          // ID của comment (nên gán khi lấy từ Firestore)
    private String userId;
    private String userName;
    private String avatarUrl;

    private String content;
    private Timestamp createdAt;

    public Comment() {
        // Needed for Firestore deserialization
    }

    public Comment(String userId, String userName, String avatarUrl, String content, Timestamp createdAt) {
        this.userId = userId;
        this.userName = userName;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}