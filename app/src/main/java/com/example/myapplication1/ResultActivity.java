package com.example.myapplication1;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class ResultActivity extends AppCompatActivity {

    private TextView resultText;
    private ProgressBar loadingBar;
    private TextView mealResult;

    private double[] userScores; // 사용자 점수 배열

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // View 연결
        resultText = findViewById(R.id.resultText);
        loadingBar = findViewById(R.id.loadingBar);
        mealResult = findViewById(R.id.mealResult);

        // Intent에서 사용자 점수 받기
        userScores = getIntent().getDoubleArrayExtra("userScores");

        // 3초 동안 로딩 후 결과 표시
        new Handler().postDelayed(this::showResult, 3000);
    }

    private void showResult() {
        // ProgressBar 숨기기
        loadingBar.setVisibility(ProgressBar.GONE);

        // 추천 학식 계산
        String recommendedMeal = calculateMealRecommendation(userScores);

        // 결과 표시
        resultText.setText("추천 학식 결과:");
        mealResult.setText(recommendedMeal);
        mealResult.setVisibility(TextView.VISIBLE);
    }

    private String calculateMealRecommendation(double[] scores) {
        // JSON 데이터에서 학식 메뉴 로드
        String jsonMenuData = loadJsonData();
        String recommendedMeal = "";
        String secondrecommendedMeal = "";

        try {
            JSONArray menuArray = new JSONArray(jsonMenuData);
            double minDistance=Double.MAX_VALUE;
            //double secondminDistance = Double.MAX_VALUE;

            for (int i = 0; i < menuArray.length(); i++) {
                JSONObject menuItem = menuArray.getJSONObject(i);

                // 메뉴의 점수 배열
                JSONArray menuScoresArray = menuItem.getJSONArray("scores");
                int[] menuScores = jsonArrayToIntArray(menuScoresArray);
                double matchScore = 0;

                // 점수 유사도 계산 (예: 내적 계산)
                for (int j = 0; j < scores.length; j++) {
                    matchScore += Math.pow(scores[j] - menuScores[j], 2);
                }

                // 최고 점수를 가진 메뉴를 추천
                if (matchScore < minDistance) {
                    minDistance = matchScore;
                    recommendedMeal = menuItem.getString("menu");
                }
                else if(matchScore == minDistance){
                    //secondminDistance=matchScore;
                    secondrecommendedMeal=menuItem.getString("menu");
                }
                Random random = new Random();
                if(random.nextBoolean()){
                    recommendedMeal=secondrecommendedMeal;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            recommendedMeal = "추천 학식을 불러오는 중 오류 발생";
        }

        return recommendedMeal;
    }

    private int[] jsonArrayToIntArray(JSONArray jsonArray) throws JSONException {
        int[] result = new int[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            result[i] = jsonArray.getInt(i);
        }
        return result;
    }


    private String loadJsonData() {
        // JSON 학식 데이터를 String으로 반환
        String json = null;

        try {
            // assets 폴더에서 menus.json 파일 열기
            InputStream inputStream = getAssets().open("menus.json");

            // InputStream을 String으로 변환
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }
}
