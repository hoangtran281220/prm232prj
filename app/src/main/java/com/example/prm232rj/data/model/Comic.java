package com.example.prm232rj.data.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class Comic {
    private String Title;
    private String Description;
    private String CoverImage;
    private String Status;
    private double Rating;
    private long Views;
    private List<String> TagId;
    private String AuthorId;
    private Timestamp CreatedAt;
    private Timestamp UpdatedAt;

    public Comic(String title, String description, String coverImage, String status,
                 double rating, long views, List<String> tagId, String authorId,
                 Timestamp createdAt, Timestamp updatedAt) {
        Title = title;
        Description = description;
        CoverImage = coverImage;
        Status = status;
        Rating = rating;
        Views = views;
        TagId = tagId;
        AuthorId = authorId;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
    }

    public Comic() {
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCoverImage() {
        return CoverImage;
    }

    public void setCoverImage(String coverImage) {
        CoverImage = coverImage;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
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

    public List<String> getTagId() {
        return TagId;
    }

    public void setTagId(List<String> tagId) {
        TagId = tagId;
    }

    public String getAuthorId() {
        return AuthorId;
    }

    public void setAuthorId(String authorId) {
        AuthorId = authorId;
    }

    public Timestamp getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        CreatedAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        UpdatedAt = updatedAt;
    }
}
