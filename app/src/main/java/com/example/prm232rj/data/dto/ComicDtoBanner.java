package com.example.prm232rj.data.dto;

public class ComicDtoBanner {
    private String Id;
    private String CoverImage;
    private String Title;
    private long Views;


    public ComicDtoBanner(String coverImage, String title, String id, long views) {
        CoverImage = coverImage;
        Title = title;
        Id = id;
        Views = views;
    }

    public ComicDtoBanner() {
    }

    public String getCoverImage() {
        return CoverImage;
    }

    public void setCoverImage(String coverImage) {
        CoverImage = coverImage;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public long getViews() {
        return Views;
    }

    public void setViews(long views) {
        Views = views;
    }
}
