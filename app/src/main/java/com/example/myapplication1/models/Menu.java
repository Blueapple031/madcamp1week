package com.example.myapplication1.models;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;

public class Menu {
    private final String location;
    private final String restaurant;
    private final String menuName;
    private final double[] scores;
    private double matchScore;

    public Menu(String location, String restaurant, String menuName, double[] scores) {
        this.location = location;
        this.restaurant = restaurant;
        this.menuName = menuName;
        this.scores = scores;
        this.matchScore = Double.MAX_VALUE; // 초기값
    }

    public String getLocation() {
        return location;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public String getMenuName() {
        return menuName;
    }

    public double[] getScores() {
        return scores;
    }

    public double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }

    public static Menu fromJsonObject(org.json.JSONObject jsonObject) throws JSONException {
        String location = jsonObject.getString("location");
        String restaurant = jsonObject.getString("restaurant");
        String menuName = jsonObject.getString("menu");
        JSONArray scoresArray = jsonObject.getJSONArray("scores");
        double[] scores = new double[scoresArray.length()];
        for (int i = 0; i < scoresArray.length(); i++) {
            scores[i] = scoresArray.getDouble(i);
        }
        return new Menu(location, restaurant, menuName, scores);
    }

    public static double getPrice(double value) {
        return (value - 25) * (10.0 / 175.0) - 5;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "location='" + location + '\'' +
                ", restaurant='" + restaurant + '\'' +
                ", menuName='" + menuName + '\'' +
                ", scores=" + Arrays.toString(scores) +
                ", matchScore=" + matchScore +
                '}';
    }

}
