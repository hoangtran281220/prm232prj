package com.example.prm232rj.data.dto;

import com.example.prm232rj.data.interfaces.IComicPreview;
import com.google.firebase.Timestamp;

import java.util.List;

public class ComicDtoWithTags implements IComicPreview {
    private String Title;
    private String CoverImage;
    private String Status;
    private double Rating;
    private long RatingCount;
    private int CurrentChapter;
    private String Id;
    private List<String> tagIds;

    private long Views;

    private Timestamp UpdatedAt;

    public Timestamp getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        UpdatedAt = updatedAt;
    }

    public long getViews() {
        return Views;
    }

    public void setViews(long views) {
        Views = views;
    }

    public ComicDtoWithTags() {
        // Firestore requires a public no-args constructor
    }

    public ComicDtoWithTags(String title, String coverImage, String status, double rating,
                            List<String> tagIds, String id, long views, Timestamp updatedAt,
                            int currentChapter, long ratingCount) {
        this.Title = title;
        this.CoverImage = coverImage;
        this.Status = status;
        this.Rating = rating;
        this.tagIds = tagIds;
        this.Id = id;
        this.Views = views;
        this.UpdatedAt = updatedAt;
        this.CurrentChapter = currentChapter;
        this.RatingCount = ratingCount;
    }
    @Override
    public long getRatingCount(){return RatingCount;}

    @Override
    public String getId() {
        return Id;
    }

    @Override
    // Getters
    public String getTitle() {
        return Title;
    }
    @Override
    public String getCoverImage() {
        return CoverImage;
    }
    @Override
    public String getStatus() {
        return Status;
    }
    @Override
    public double getRating() {
        return Rating;
    }
    public List<String> getTagIds() {
        return tagIds;
    }
    @Override
    public int getCurrentChapter(){
        return CurrentChapter;
    }

    // Setters
    public void setTitle(String title) {
        this.Title = title;
    }

    public void setCoverImage(String coverImage) {
        this.CoverImage = coverImage;
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    public void setRating(double rating) {
        this.Rating = rating;
    }

    public void setTagIds(List<String> tagIds) {
        this.tagIds = tagIds;
    }

    public void setId(String id) {
        Id = id;
    }
}
