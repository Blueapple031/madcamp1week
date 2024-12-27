package com.example.myapplication1.models;

public class ImageItem {
    private int imageId;
    private String title;

    public ImageItem(int imageId, String title) {
        this.imageId = imageId;
        this.title = title;
    }

    public int getImageId() { return imageId; }
    public String getTitle() { return title; }
}
