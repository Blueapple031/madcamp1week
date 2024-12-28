package com.example.myapplication1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication1.models.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {
    private TextView questionTextView;
    private Button option1Button, option2Button;
    private ProgressBar progressBar;

    private List<Question> questions; // JSON 데이터를 담을 리스트
    
    private List<Question> randomQuestions; //questions 중에서 선택된 8개의 랜덤 질문
    private int currentQuestionIndex = 0; // 현재 질문 인덱스
    private final int totalQuestions = 8; // 총 질문 수
    private double[] userScores = new double[8]; // 사용자 수치 데이터

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        progressBar = findViewById(R.id.progressBar);
        questionTextView = findViewById(R.id.questionTextView);
        option1Button = findViewById(R.id.option1Button);
        option2Button = findViewById(R.id.option2Button);

        // 초기 진행도 설정
        updateProgress();

        // JSON 데이터 로드
        loadQuestions();

        randomQuestions = getRandomQuestions(questions, 8);

        // 첫 질문 표시
        showQuestion(currentQuestionIndex);

        // 선택지 클릭 이벤트
        option1Button.setOnClickListener(v -> handleAnswer(0));
        option2Button.setOnClickListener(v -> handleAnswer(1));
    }

    private void loadQuestions() {
        // JSON 데이터 파싱 및 리스트 초기화 (예시용 하드코딩 데이터)
        questions = new ArrayList<>();
        String json = loadJsonDataFromAssets("questions.json");

        try {
            // JSON 데이터를 배열로 파싱
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject questionObject = jsonArray.getJSONObject(i);

                // 질문 데이터 추출
                int id = questionObject.getInt("id");
                String questionText = questionObject.getString("question");
                JSONArray optionsArray = questionObject.getJSONArray("options");
                String option1 = optionsArray.getString(0);
                String option2 = optionsArray.getString(1);

                // scoreImpact 배열 파싱
                JSONArray scoreImpactArray = questionObject.getJSONArray("scoreImpact");
                int[] option1Impact = jsonArrayToIntArray(scoreImpactArray.getJSONArray(0));
                int[] option2Impact = jsonArrayToIntArray(scoreImpactArray.getJSONArray(1));

                // Question 객체 생성 및 추가
                Question question = new Question(id, questionText, option1, option2, option1Impact, option2Impact);
                questions.add(question);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String loadJsonDataFromAssets(String fileName) {
        String json = null;
        try {
            InputStream inputStream = getAssets().open(fileName);

            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    private int[] jsonArrayToIntArray(JSONArray jsonArray) throws JSONException {
        int[] result = new int[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            result[i] = jsonArray.getInt(i);
        }
        return result;
    }
    private List<Question> getRandomQuestions(List<Question> questions, int count) {
        // 질문 리스트를 섞는다.
        Collections.shuffle(questions);

        // 섞은 리스트에서 count 개수만큼 반환
        return questions.subList(0, Math.min(count, questions.size()));
    }



    private void updateProgress() {
        int progress = (int) ((currentQuestionIndex / (float) totalQuestions) * 100);
        progressBar.setProgress(progress);
    }

    private void showQuestion(int index) {
        if (index >= totalQuestions) {
            // 모든 질문 완료 -> 결과 화면으로 이동
            Intent intent = new Intent(this, ResultActivity.class);
            for(int i=0; i < userScores.length; i++){
                userScores[i]/=totalQuestions; //8개의 질문 평균내서 -5와 5 사이에 있는 수로 만들기
            }
            intent.putExtra("userScores", userScores); //ResultActivity에 userScores 배열 전달
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // 현재 질문 데이터
        Question question = randomQuestions.get(index);
        questionTextView.setText(question.getQuestion());
        option1Button.setText(question.getOption1());
        option2Button.setText(question.getOption2());
    }

    private void handleAnswer(int optionIndex) {
        // 선택한 답변의 scoreImpact 적용
        Question question = randomQuestions.get(currentQuestionIndex);
        int[] scoreImpact=question.getOption1Impact();
        if (optionIndex==0){
            scoreImpact = question.getOption1Impact();
        }
        else if(optionIndex==1){
            scoreImpact = question.getOption2Impact();
        }

        for (int i = 0; i < userScores.length; i++) {
            userScores[i] += scoreImpact[i];
        }

        // 다음 질문으로 이동
        currentQuestionIndex++;
        showQuestion(currentQuestionIndex);
    }
}

