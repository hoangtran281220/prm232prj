package com.example.prm232rj.data.dto;

import com.example.prm232rj.data.interfaces.IComicPreview;
import com.google.firebase.Timestamp;

public class ComicDtoPreview implements IComicPreview {
        private String Id;
        private String Title;
        private String CoverImage;
        private String Status;
        private int CurrentChapter;
        private double Rating;
        private long RatingCount;
        private Timestamp UpdatedAt;

        public ComicDtoPreview() {}

        // ✅ Constructor tiện cho tạo object bằng tay
        public ComicDtoPreview(double rating, String title, String coverImage,
                               String status, String id, Timestamp updatedAt,
                               int currentChapter, long ratingCount) {
                Title = title;
                CoverImage = coverImage;
                Status = status;
                Rating = rating;
                Id = id;
                UpdatedAt = updatedAt;
                CurrentChapter = currentChapter;
                RatingCount = ratingCount;
        }

        @Override
        public String getId() {
                return Id;
        }

        @Override
        public String getTitle() { return Title; }
        @Override
        public String getCoverImage() { return CoverImage; }
        @Override
        public String getStatus() { return Status; }
        @Override
        public double getRating() { return Rating; }
        @Override
        public long getRatingCount(){return RatingCount;}
        @Override
        public int getCurrentChapter(){
                return CurrentChapter;
        }

        public void setId(String id) {
                Id = id;
        }

        public void setTitle(String title) {
                Title = title;
        }

        public Timestamp getUpdatedAt() {
                return UpdatedAt;
        }
}
