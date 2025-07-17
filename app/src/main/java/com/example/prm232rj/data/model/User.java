package com.example.prm232rj.data.model;

import com.google.firebase.Timestamp;

public class User {
    private String id;
    private String email;
    private String username;
    private String avatarUrl;
    private int roleId;
    private boolean isEmailLinked;
    private String linkedProvider;
    private Timestamp createdAt;
    private Timestamp updateAt;

    public User() {
        // Needed for Firestore deserialization
    }

    public User(String id, String email, String username, String avatarUrl, int roleId,
                boolean isEmailLinked, String linkedProvider,
                Timestamp createdAt, Timestamp updateAt) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.roleId = roleId;
        this.isEmailLinked = isEmailLinked;
        this.linkedProvider = linkedProvider;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
    }

    // Getters & Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public boolean isEmailLinked() {
        return isEmailLinked;
    }

    public void setEmailLinked(boolean emailLinked) {
        isEmailLinked = emailLinked;
    }

    public String getLinkedProvider() {
        return linkedProvider;
    }

    public void setLinkedProvider(String linkedProvider) {
        this.linkedProvider = linkedProvider;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Timestamp updateAt) {
        this.updateAt = updateAt;
    }
}
