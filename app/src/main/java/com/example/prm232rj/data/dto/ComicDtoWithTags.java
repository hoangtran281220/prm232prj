package com.example.prm232rj.data.dto;

import com.example.prm232rj.data.interfaces.IComicPreview;

import java.util.List;

public class ComicDtoWithTags implements IComicPreview {
    private String Title;
    private String CoverImage;
    private String Status;
    private double Rating;

    private String Id;
    private List<String> tagIds;

    public ComicDtoWithTags() {
        // Firestore requires a public no-args constructor
    }

    public ComicDtoWithTags(String title, String coverImage, String status, double rating, List<String> tagIds, String id) {
        this.Title = title;
        this.CoverImage = coverImage;
        this.Status = status;
        this.Rating = rating;
        this.tagIds = tagIds;
        this.Id = id;
    }

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
