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

import com.example.myapplication.R;
import com.example.myapplication1.models.Question;
import com.example.myapplication1.repository.QuestionRepository;

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

    private List<Question> randomQuestions; // 선택된 랜덤 질문
    private int currentQuestionIndex = 0; // 현재 질문 인덱스
    private final int totalQuestions = 8; // 총 질문 수
    private double[] userScores = new double[7]; // 사용자 수치 데이터

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_questions, container, false);

        initViews(view);
        randomQuestions = QuestionRepository.getInstance(requireContext()).getRandomQuestions(totalQuestions);

        // 첫 질문 표시
        showQuestion(currentQuestionIndex);
        return view;
    }

    private void initViews(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        questionTextView = view.findViewById(R.id.questiontext);
        option1Button = view.findViewById(R.id.option1Button);
        option2Button = view.findViewById(R.id.option2Button);
        updateProgress();
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
            Bundle bundle = new Bundle();
            bundle.putDoubleArray("userScores", userScores);

            ResultFragment resultFragment = new ResultFragment();
            resultFragment.setArguments(bundle);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, resultFragment);
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
            transaction.commit();
            return;
        }

        Question question = randomQuestions.get(index);
        questionTextView.setText(question.getQuestion());
        option1Button.setText(question.getOption1());
        option2Button.setText(question.getOption2());
        option1Button.setOnClickListener(v -> handleAnswer(0));
        option2Button.setOnClickListener(v -> handleAnswer(1));
    }

    private void handleAnswer(int optionIndex) {
        Question question = randomQuestions.get(currentQuestionIndex);
        int[] scoreImpact = optionIndex == 0 ? question.getOption1Impact() : question.getOption2Impact();

        for (int i = 0; i < userScores.length; i++) {
            userScores[i] += (double) scoreImpact[i];
        }

        currentQuestionIndex++;
        updateProgress();
        showQuestion(currentQuestionIndex);
    }
}

