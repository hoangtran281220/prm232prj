package com.example.prm232rj.data.model;
import com.google.firebase.Timestamp;
import java.util.List;
public class Chapter {
    private String id; // Document ID from Firestore
    private int ChapterNumber;
    private String ComicId;
    private List<String> Content; // Array of image URLs
    private Timestamp CreatedAt;
    private long Views;
    private  String ChapterTitle;

    public Chapter(long views, Timestamp createdAt, List<String> content, String comicId, int chapterNumber, String id, String chapterTitle) {
        Views = views;
        CreatedAt = createdAt;
        Content = content;
        ComicId = comicId;
        ChapterNumber = chapterNumber;
        this.id = id;
        ChapterTitle = chapterTitle;
    }

    public Chapter() {

    }

    public int getChapterNumber() {
        return ChapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        ChapterNumber = chapterNumber;
    }

    public List<String> getContent() {
        return Content;
    }

    public void setContent(List<String> content) {
        Content = content;
    }

    public long getViews() {
        return Views;
    }

    public void setViews(long views) {
        Views = views;
    }

    public Timestamp getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        CreatedAt = createdAt;
    }

    public String getComicId() {
        return ComicId;
    }

    public void setComicId(String comicId) {
        ComicId = comicId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChapterTitle() {
        return ChapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        ChapterTitle = chapterTitle;
    }
}
