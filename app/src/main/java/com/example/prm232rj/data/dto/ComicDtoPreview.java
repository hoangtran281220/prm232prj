package com.example.prm232rj.data.dto;

import com.example.prm232rj.data.interfaces.IComicPreview;

public class ComicDtoPreview implements IComicPreview {
        private String Title;
        private String CoverImage;
        private String Status;
        private double Rating;

        public ComicDtoPreview() {}

        // ✅ Constructor tiện cho tạo object bằng tay
        public ComicDtoPreview(double rating, String title, String coverImage, String status) {
                this.Title = title;
                this.CoverImage = coverImage;
                this.Status = status;
                this.Rating = rating;
        }

        @Override
        public String getTitle() { return Title; }
        @Override
        public String getCoverImage() { return CoverImage; }
        @Override
        public String getStatus() { return Status; }
        @Override
        public double getRating() { return Rating; }

}
