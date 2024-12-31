package com.example.myapplication1.models;

import com.google.android.gms.maps.model.LatLng;

public class Building {
    private final String name;
    private final LatLng location;
    private final String details;
    private final int imageResource;
    private float distance; // 거리 추가

    public Building(String name, LatLng location, String details, int imageResource) {
        this.name = name;
        this.location = location;
        this.details = details;
        this.imageResource = imageResource;
        this.distance = -1; // 초기값
    }

    public String getName() {
        return name;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getDetails() {
        return details;
    }

    public int getImageResource() {
        return imageResource;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getUpdatedDetails() {
        if (distance >= 0) {
            return details + String.format("\n거리: %.2f m", distance);
        }
        return details;
    }
}
