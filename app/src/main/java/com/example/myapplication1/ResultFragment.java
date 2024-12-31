package com.example.myapplication1;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.R;
import com.example.myapplication1.models.Building;
import com.example.myapplication1.repository.BuildingRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ResultFragment extends Fragment {

    private TextView resultText, mealResult, mealLocation;
    private ProgressBar loadingBar;
    private ImageView iconImage;
    private Button backButton, mapButton;

    private double[] userScores;
    private List<JSONObject> menuList = new ArrayList<>();
    private List<JSONObject> topMenus = new ArrayList<>();

    private String mealLocationText = "";
    private String mealRestaurant = "";
    private String recommendedMeal = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_result, container, false);
        initViews(view);

        Bundle arguments = getArguments();
        if (arguments != null) {
            userScores = arguments.getDoubleArray("userScores");
        }

        new Handler().postDelayed(this::showResult, 3000);
        return view;
    }

    private void initViews(View view) {
        resultText = view.findViewById(R.id.resultText);
        loadingBar = view.findViewById(R.id.loadingBar);
        mealResult = view.findViewById(R.id.mealResult);
        mealLocation = view.findViewById(R.id.mealLocation);
        iconImage = view.findViewById(R.id.iconimage);
        backButton = view.findViewById(R.id.backButton);
        mapButton = view.findViewById(R.id.mapButton);

        setViewsVisibility(View.INVISIBLE);
    }

    private void setViewsVisibility(int visibility) {
        backButton.setVisibility(visibility);
        mapButton.setVisibility(visibility);
        mealResult.setVisibility(visibility);
        mealLocation.setVisibility(visibility);
        iconImage.setVisibility(visibility);
    }

    private void showResult() {
        loadingBar.setVisibility(View.GONE);
        recommendedMeal = calculateMealRecommendation(userScores);

        displayResult();
        setupButtons();
    }

    private void displayResult() {
        resultText.setText("추천 학식 결과:");
        mealResult.setText(recommendedMeal);
        mealLocation.setText(mealLocationText + " " + mealRestaurant);
        iconImage.setImageResource(getDrawableResourceId(mapLocation()));
        setViewsVisibility(View.VISIBLE);
    }

    private void setupButtons() {
        backButton.setOnClickListener(v -> navigateToFragment(new QuestionFragment()));
        mapButton.setOnClickListener(v -> navigateToFragment(new MapFragment()));
    }

    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_right,
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );
        transaction.commit();
    }

    private int getDrawableResourceId(String location) {
        return getResources().getIdentifier(location, "drawable", requireContext().getPackageName());
    }

    private String mapLocation() {
        switch (mealLocationText) {
            case "카이마루":
                return "kaimaru";
            case "태울관":
                return "taeul";
            case "장영신학생회관":
                return "jangyungsin";
            case "정문술빌딩":
                return "jungmun_building";
            case "동측식당":
                return "professor_castle";
            case "서측식당":
                return "west_dining";
            case "매점 건물":
                return "maejum";
            case "세종관":
                return "sejong";
            case "희망/다솜관":
                return "hope_dasom";
            case "나들/여울관":
                return "nadle_yuul";
            case "미르/나래관":
                return "mir_narae";
            default:
                return "default_image";
        }
    }

    private String calculateMealRecommendation(double[] scores) {
        String jsonMenuData = loadJsonData();
        if (jsonMenuData == null) return "추천 학식을 불러오는 중 오류 발생";

        try {
            JSONArray menuArray = new JSONArray(jsonMenuData);

            List<String> rankedLocations = getRankedLocationsByDistance();

            for (int i = 0; i < menuArray.length(); i++) {
                JSONObject menuItem = menuArray.getJSONObject(i);
                menuItem.put("matchscore", calculateMatchScore(menuItem, scores, rankedLocations));
                menuList.add(menuItem);
            }

            menuList.sort(Comparator.comparingDouble(menuItem -> {
                try {
                    return menuItem.getDouble("matchscore");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }));

            topMenus.addAll(menuList.subList(0, Math.min(6, menuList.size())));
        } catch (JSONException e) {
            e.printStackTrace();
            return "추천 학식을 불러오는 중 오류 발생";
        }

        return findMenuWithLowestLastScore(topMenus);
    }

    private double calculateMatchScore(JSONObject menuItem, double[] scores, List<String> rankedLocations) throws JSONException {
        JSONArray menuScoresArray = menuItem.getJSONArray("scores");
        double[] menuScores = jsonArrayToDoubleArray(menuScoresArray);

        double matchScore = 0;
        for (int j = 0; j < scores.length; j++) {
            matchScore += Math.pow(scores[j] - menuScores[j], 2);
        }

        String location = menuItem.getString("location");
        int rank = rankedLocations.indexOf(location); // 거리 순위에 따라 가중치 결정
        double distanceWeight = (rank >= 0) ? 11 - rank : 1; // 순위가 없으면 최소 가중치(1)
        if(rank == 1){
            matchScore = 0;
        }
        return matchScore / distanceWeight; // 가중치를 나누어 점수에 반영
    }
    private List<String> getRankedLocationsByDistance() {
        // 거리 업데이트 및 정렬
        List<Building> buildings = BuildingRepository.getInstance().getBuildings();
        buildings.sort(Comparator.comparingDouble(Building::getDistance));

        // 거리 순으로 정렬된 위치 이름 리스트 반환
        List<String> rankedLocations = new ArrayList<>();
        for (Building building : buildings) {
            rankedLocations.add(building.getName());
        }
        return rankedLocations;
    }


    private String findMenuWithLowestLastScore(List<JSONObject> topMenus) {
        JSONObject lowestMenu = null;
        double minLastScore = Double.MAX_VALUE;

        for (JSONObject menu : topMenus) {
            try {
                JSONArray scores = menu.getJSONArray("scores");
                double lastScore = scores.getDouble(scores.length() - 1);

                String location = menu.getString("location");
                double distance = BuildingRepository.getInstance().getDistanceToBuilding(location);
                lastScore *= (distance < 100 ? 0.8 : 1.2);

                if (lastScore < minLastScore) {
                    minLastScore = lastScore;
                    lowestMenu = menu;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (lowestMenu != null) {
            try {
                mealLocationText = lowestMenu.getString("location");
                mealRestaurant = lowestMenu.getString("restaurant");
                return lowestMenu.getString("menu");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "추천 학식을 찾을 수 없습니다.";
    }

    private double[] jsonArrayToDoubleArray(JSONArray jsonArray) throws JSONException {
        double[] result = new double[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            result[i] = jsonArray.getDouble(i);
        }

        double cost = result[0];
        result[0] = (cost - 25) * ((5 - (-5)) / (200.0 - 25)) + (-5);
        result[0] = -result[0];
        return result;
    }

    private String loadJsonData() {
        AssetManager assetManager = requireContext().getAssets();
        try (InputStream inputStream = assetManager.open("menus.json")) {
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            return new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
