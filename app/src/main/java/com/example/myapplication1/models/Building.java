package com.example.myapplication1.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Building implements Parcelable {
    private final String name;
    private final LatLng location;
    private final String details;
    private final int imageResource;
    private float distance;

    public Building(String name, LatLng location, String details, int imageResource) {
        this.name = name;
        this.location = location;
        this.details = details;
        this.imageResource = imageResource;
        this.distance = -1;
    }

    protected Building(Parcel in) {
        name = in.readString();
        location = new LatLng(in.readDouble(), in.readDouble());
        details = in.readString();
        imageResource = in.readInt();
        distance = in.readFloat();
    }

    public static final Creator<Building> CREATOR = new Creator<Building>() {
        @Override
        public Building createFromParcel(Parcel in) {
            return new Building(in);
        }

        @Override
        public Building[] newArray(int size) {
            return new Building[size];
        }
    };

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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeFloat(distance);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    public String getUpdatedDetails() {
        if (distance >= 0) {
            return details + String.format("\n거리: %.2f m", distance);
        }
        return details;
    }
}
