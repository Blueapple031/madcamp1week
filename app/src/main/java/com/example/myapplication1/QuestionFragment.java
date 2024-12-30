package com.example.myapplication1;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

public class QuestionFragment extends Fragment {

    private TextView questionTextView;
    private Button option1Button, option2Button;
    private ProgressBar progressBar;

    private List<Question> questions; // JSON 데이터를 담을 리스트
    private List<Question> randomQuestions; // questions 중에서 선택된 8개의 랜덤 질문
    private int currentQuestionIndex = 0; // 현재 질문 인덱스
    private final int totalQuestions = 8; // 총 질문 수
    private double[] userScores = new double[8]; // 사용자 수치 데이터

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_questions, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        questionTextView = view.findViewById(R.id.questiontext);
        option1Button = view.findViewById(R.id.option1Button);
        option2Button = view.findViewById(R.id.option2Button);

        // 초기 진행도 설정
        updateProgress();

        // JSON 데이터 로드
        loadQuestions();

        randomQuestions = getRandomQuestions(questions, totalQuestions);

        // 첫 질문 표시
        showQuestion(currentQuestionIndex);

        // 선택지 클릭 이벤트
        option1Button.setOnClickListener(v -> handleAnswer(0));
        option2Button.setOnClickListener(v -> handleAnswer(1));

        return view;
    }

    private void loadQuestions() {
        // JSON 데이터 파싱 및 리스트 초기화
        questions = new ArrayList<>();
        String json = loadJsonDataFromAssets("questions.json");

        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject questionObject = jsonArray.getJSONObject(i);

                int id = questionObject.getInt("id");
                String questionText = questionObject.getString("question");
                JSONArray optionsArray = questionObject.getJSONArray("options");
                String option1 = optionsArray.getString(0);
                String option2 = optionsArray.getString(1);

                JSONArray scoreImpactArray = questionObject.getJSONArray("scoreImpact");
                int[] option1Impact = jsonArrayToIntArray(scoreImpactArray.getJSONArray(0));
                int[] option2Impact = jsonArrayToIntArray(scoreImpactArray.getJSONArray(1));

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
            InputStream inputStream = getActivity().getAssets().open(fileName);

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
        Collections.shuffle(questions);
        return questions.subList(0, Math.min(count, questions.size()));
    }

    private void updateProgress() {
        int progress = (int) ((currentQuestionIndex / (float) totalQuestions) * 100);
        ValueAnimator animator = ValueAnimator.ofInt(progressBar.getProgress(), progress);
        animator.setDuration(500); // 애니메이션 지속 시간 (500ms)
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation ->
                progressBar.setProgress((int) animation.getAnimatedValue())
        );
        animator.start();
    }

    private void showQuestion(int index) {
        if (index >= totalQuestions) {
            for (int i = 0; i < userScores.length; i++) {
                userScores[i] /= totalQuestions; // 평균 값으로 변환
            }
            Bundle bundle = new Bundle();
            bundle.putDoubleArray("userScores", userScores);

            ResultFragment resultFragment = new ResultFragment();

            resultFragment.setArguments(bundle);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, resultFragment);
            transaction.commit();
            return;
        }

        Question question = randomQuestions.get(index);
        questionTextView.setText(question.getQuestion());
        option1Button.setText(question.getOption1());
        option2Button.setText(question.getOption2());
    }

    private void handleAnswer(int optionIndex) {
        Question question = randomQuestions.get(currentQuestionIndex);
        int[] scoreImpact = optionIndex == 0 ? question.getOption1Impact() : question.getOption2Impact();

        for (int i = 0; i < userScores.length; i++) {
            userScores[i] += scoreImpact[i];
        }

        currentQuestionIndex++;
        updateProgress();
        showQuestion(currentQuestionIndex);
    }
}
