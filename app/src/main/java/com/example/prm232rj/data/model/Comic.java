package com.example.prm232rj.data.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

import java.util.List;

public class Comic {
    private String id;
    private String Title;
    private String Description;
    private String CoverImage;
    private String Status;
    private double Rating;
    private long Views;
    private List<String> TagId;
    private List<String> AuthorId;
    private Timestamp CreatedAt;
    private Timestamp UpdatedAt;

    public Comic(String id, Timestamp updatedAt, Timestamp createdAt, List<String> authorId, List<String> tagId, long views, double rating, String status, String coverImage, String description, String title) {
        this.id = id;
        UpdatedAt = updatedAt;
        CreatedAt = createdAt;
        AuthorId = authorId;
        TagId = tagId;
        Views = views;
        Rating = rating;
        Status = status;
        CoverImage = coverImage;
        Description = description;
        Title = title;
    }



    public Comic() {
    }

    public Timestamp getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        UpdatedAt = updatedAt;
    }

    public Timestamp getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        CreatedAt = createdAt;
    }
    @PropertyName("AuthorId")
    public List<String> getAuthorId() {
        return AuthorId;
    }
    @PropertyName("AuthorId")
    public void setAuthorId(List<String> authorId) {
        AuthorId = authorId;
    }

    public List<String> getTagId() {
        return TagId;
    }

    public void setTagId(List<String> tagId) {
        TagId = tagId;
    }

    public long getViews() {
        return Views;
    }

    public void setViews(long views) {
        Views = views;
    }

    public double getRating() {
        return Rating;
    }

    public void setRating(double rating) {
        Rating = rating;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getCoverImage() {
        return CoverImage;
    }

    public void setCoverImage(String coverImage) {
        CoverImage = coverImage;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}


