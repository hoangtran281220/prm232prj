package com.example.prm232rj.data.dto;

import java.util.List;

public class ChapterReadingDto {
    private String chapterId;
    private int chapterNumber;
    private String chapterTitle;
    private String comicId;
    private List<String> contentImages;
    private long views;

    public ChapterReadingDto(String chapterId, int chapterNumber, String chapterTitle, String comicId, List<String> contentImages, long views) {
        this.chapterId = chapterId;
        this.chapterNumber = chapterNumber;
        this.chapterTitle = chapterTitle;
        this.comicId = comicId;
        this.contentImages = contentImages;
        this.views = views;
    }

    public String getChapterId() {
        return chapterId;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public String getComicId() {
        return comicId;
    }

    public List<String> getContentImages() {
        return contentImages;
    }

    public long getViews() {
        return views;
    }
}