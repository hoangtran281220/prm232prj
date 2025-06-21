package com.example.prm232rj.data.dto;

public class ComicDtoPreview {
        private String Title;
        private String CoverImage;
        private String Status;
        private double Rating;

        public ComicDtoPreview() {} // Needed for Firestore

        public ComicDtoPreview(double rating, String status, String coverImage, String title) {
                Rating = rating;
                Status = status;
                CoverImage = coverImage;
                Title = title;
        }

        public String getTitle() { return Title; }
        public String getCoverImage() { return CoverImage; }
        public String getStatus() { return Status; }
        public double getRating() { return Rating; }

}
