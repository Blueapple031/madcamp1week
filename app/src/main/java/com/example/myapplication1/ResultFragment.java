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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ResultFragment extends Fragment {

    private TextView resultText;
    private ProgressBar loadingBar;
    private TextView mealResult;
    private TextView mealLocation;
    private ImageView iconimage;
    private Button backButton;
    private Button mapButton;

    private double[] userScores; // 사용자 점수 배열
    private List<JSONObject> menuList = new ArrayList<>();
    private List<JSONObject> topMenus = new ArrayList<>();
    private String meallocation = "";
    private String mealrestaurant = "";
    String recommendedMeal = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_result, container, false);

        // View 연결
        resultText = view.findViewById(R.id.resultText);
        loadingBar = view.findViewById(R.id.loadingBar);
        mealResult = view.findViewById(R.id.mealResult);
        iconimage = view.findViewById(R.id.iconimage);
        backButton = view.findViewById(R.id.backButton);
        mapButton = view.findViewById(R.id.mapButton);
        mealLocation = view.findViewById(R.id.mealLocation);

        backButton.setVisibility(Button.INVISIBLE);
        mapButton.setVisibility(Button.INVISIBLE);
        mealResult.setVisibility(TextView.INVISIBLE);
        mealLocation.setVisibility(TextView.INVISIBLE);

        iconimage.setVisibility(ImageView.INVISIBLE);

        // Intent에서 사용자 점수 받기
        Bundle arguments = getArguments();
        if(arguments != null){
            userScores = arguments.getDoubleArray("userScores");
        }
        // 3초 동안 로딩 후 결과 표시
        new Handler().postDelayed(this::showResult, 3000);
        return view;
    }

    private void showResult()  {
        // ProgressBar 숨기기
        loadingBar.setVisibility(ProgressBar.GONE);

        // 추천 학식 계산
        String recommendedMeal = calculateMealRecommendation(userScores);

        // 결과 표시
        resultText.setText("추천 학식 결과:");
        mealResult.setText(recommendedMeal);
        mealLocation.setText(meallocation + " " + mealrestaurant);
        iconimage.setImageResource(getDrawableResourceId(mapLocation()));
        backButton.setVisibility(Button.VISIBLE);
        mapButton.setVisibility(Button.VISIBLE);
        mealResult.setVisibility(TextView.VISIBLE);
        mealLocation.setVisibility(TextView.VISIBLE);
        iconimage.setVisibility(ImageView.VISIBLE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionFragment questionFragment = new QuestionFragment();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, questionFragment);
                transaction.setCustomAnimations(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                );
                transaction.commit();
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragment mapFragment = new MapFragment();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, mapFragment);
                transaction.setCustomAnimations(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                );
                transaction.commit();
            }
        });
    }
    private int getDrawableResourceId(String location) {
        // location에 맞는 이미지 리소스 ID를 찾아 반환
        // 예: "park"에 해당하는 이미지가 drawable 폴더에 있어야 함.
        return getResources().getIdentifier(location, "drawable", requireContext().getPackageName());
    }

    private String mapLocation(){
        String picturename="";
        if(Objects.equals(meallocation, "카이마루")){
            picturename="kaimaru";
        }
        else if(Objects.equals(meallocation, "태울관")){
            picturename="taeul";
        }
        else if(Objects.equals(meallocation, "장영신학생회관")){
            picturename="jangyungsin";
        }
        else if(Objects.equals(meallocation, "정문술빌딩")){
            picturename="jungmun_building";
        }
        else if(Objects.equals(meallocation, "동측식당")){
            picturename="professor_castle";
        }
        else if(Objects.equals(meallocation, "서측식당")){
            picturename="west_dining";
        }
        else if(Objects.equals(meallocation, "매점 건물")){
            picturename="maejum";
        }
        else if(Objects.equals(meallocation, "세종관")){
            picturename="sejong";
        }
        else if(Objects.equals(meallocation, "희망/다솜관")){
            picturename="hope_dasom";
        }
        else if(Objects.equals(meallocation, "나들/여울관")){
            picturename="nadle_yuul";
        }
        else if(Objects.equals(meallocation, "미르/나래관")){
            picturename="mir_narae";
        }

        return picturename;
    }

    private String calculateMealRecommendation(double[] scores) {
        // JSON 데이터에서 학식 메뉴 로드

        String jsonMenuData = loadJsonData();

        //String secondrecommendedMeal = "";

        try {
            JSONArray menuArray = new JSONArray(jsonMenuData);
            //double minDistance=Double.MAX_VALUE;
            //double secondminDistance = Double.MAX_VALUE;

            for (int i = 0; i < menuArray.length(); i++) {
                JSONObject menuItem = menuArray.getJSONObject(i);

                // 메뉴의 점수 배열
                JSONArray menuScoresArray = menuItem.getJSONArray("scores");
                double[] menuScores = jsonArrayToDoubleArray(menuScoresArray);
                double matchScore = 0;

                // 점수 유사도 계산 (예: 내적 계산)
                for (int j = 0; j < scores.length-1; j++) {
                    matchScore += Math.pow(scores[j] - menuScores[j], 2);
                }

                menuItem.put("matchscore", matchScore);
                menuList.add(menuItem);

                // 최고 점수를 가진 메뉴를 추천
//                if (matchScore < minDistance) {
//                    minDistance = matchScore;
//                    recommendedMeal = menuItem.getString("menu");
//                }
//                else if(matchScore == minDistance){
//                    //secondminDistance=matchScore;
//                    secondrecommendedMeal=menuItem.getString("menu");
//                }
//                Random random = new Random();
//                if(random.nextBoolean()){
//                    recommendedMeal=secondrecommendedMeal;
            //}
            }
            menuList.sort(Comparator.comparingDouble(menuItem -> {
                try {
                    return menuItem.getDouble("matchscore");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }));
            for (int i = 0; i < Math.min(6, menuList.size()); i++) {
                topMenus.add(menuList.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            recommendedMeal = "추천 학식을 불러오는 중 오류 발생";
        }

        return findMenuWithLowestLastScore(topMenus);
    }

    // topMenus에서 가장 마지막 점수가 낮은 메뉴를 찾는 메서드
    private String findMenuWithLowestLastScore(List<JSONObject> topMenus){
        JSONObject lowestMenu = null;
        double minLastScore = Double.MAX_VALUE; // 매우 큰 값으로 초기화

        for (JSONObject menu : topMenus) {
            try {
                // scores 배열 가져오기
                JSONArray scores = menu.getJSONArray("scores");
                // 마지막 점수 가져오기
                double lastScore = scores.getDouble(scores.length() - 1);

                // 최소값 비교
                if (lastScore < minLastScore) {
                    minLastScore = lastScore;
                    lowestMenu = menu;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            meallocation=lowestMenu.getString("location");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        try {
            mealrestaurant=lowestMenu.getString("restaurant");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        try {
            return lowestMenu.getString("menu");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    private double[] jsonArrayToDoubleArray(JSONArray jsonArray) throws JSONException {
        double[] result = new double[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            result[i] = (double)jsonArray.getInt(i);
        }
        //여기서는 첫 번째 인수인 가격을 -5와 5 사이로 만들것임.
        double cost=result[0];
        result[0]=(cost - 25) * ((5 - (-5)) / (200 - 25)) + (-5);
        result[0]=-result[0];
        return result;
    }


    private String loadJsonData() {
        // JSON 학식 데이터를 String으로 반환
        String json = null;
        AssetManager assetManager= getContext().getAssets();

        try {
            // assets 폴더에서 menus.json 파일 열기
            InputStream inputStream = assetManager.open("menus.json");

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
