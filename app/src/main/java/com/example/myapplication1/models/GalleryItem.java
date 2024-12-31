package com.example.myapplication1.models;

public class GalleryItem {
    private int imageResId; // 이미지 리소스 ID
    private String description; // 사진 설명

    public GalleryItem(int imageResId, String description) {
        this.imageResId = imageResId;
        this.description = description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getDescription() {
        return description;
    }
}
