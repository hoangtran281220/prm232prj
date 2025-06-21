package com.example.prm232rj.data.dto;

public class ComicDtoBanner {
    private String CoverImage;
    private String Title;

    public ComicDtoBanner(String coverImage, String title) {
        CoverImage = coverImage;
        Title = title;
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
}
