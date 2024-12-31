package com.example.myapplication1.repository;

import android.content.Context;
import com.example.myapplication1.models.Menu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MenuRepository {
    private static MenuRepository instance;
    private final List<Menu> menus = new ArrayList<>();

    private MenuRepository(Context context) {
        loadMenusFromJson(context);
    }

    public static MenuRepository getInstance(Context context) {
        if (instance == null) {
            instance = new MenuRepository(context);
        }
        return instance;
    }

    private void loadMenusFromJson(Context context) {
        try {
            String jsonData = loadJsonFromAssets(context, "menus.json");
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject menuObject = jsonArray.getJSONObject(i);
                menus.add(Menu.fromJsonObject(menuObject));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private String loadJsonFromAssets(Context context, String fileName) throws IOException {
        InputStream inputStream = context.getAssets().open(fileName);
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        inputStream.close();
        return new String(buffer, "UTF-8");
    }

    public List<Menu> calculateMatchScores(double[] userScores, List<String> rankedLocations) {
        for (Menu menu : menus) {
            double matchScore = calculateMatchScore(menu, userScores, rankedLocations);
            menu.setMatchScore(matchScore);
        }
        menus.sort(Comparator.comparingDouble(Menu::getMatchScore));
        return new ArrayList<>(menus); // 정렬된 메뉴 리스트 반환
    }

    private double calculateMatchScore(Menu menu, double[] userScores, List<String> rankedLocations) {
        double matchScore = 0;
        double[] menuScores = menu.getScores();

        for (int i = 0; i < userScores.length; i++) {
            matchScore += Math.pow(userScores[i] - menuScores[i], 2);
        }

        int rank = rankedLocations.indexOf(menu.getLocation());
        double distanceWeight = (rank >= 0) ? 11 - rank : 1; // 거리 가중치
        return matchScore / distanceWeight;
    }
}
