package com.example.myapplication1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication1.models.Question;
import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {
    private TextView questionTextView;
    private Button option1Button, option2Button;
    private ProgressBar progressBar;

    private List<Question> questions; // JSON 데이터를 담을 리스트
    private int currentQuestionIndex = 0; // 현재 질문 인덱스
    private final int totalQuestions = 8; // 총 질문 수
    private int[] userScores = new int[8]; // 사용자 수치 데이터

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

        // 첫 질문 표시
        showQuestion(currentQuestionIndex);

        // 선택지 클릭 이벤트
        option1Button.setOnClickListener(v -> handleAnswer(0));
        option2Button.setOnClickListener(v -> handleAnswer(1));
    }

    private void loadQuestions() {
        // JSON 데이터 파싱 및 리스트 초기화 (예시용 하드코딩 데이터)
        questions = new ArrayList<>();
        questions.add(new Question("매운 음식을 좋아하십니까?",
                new String[]{"좋아한다", "좋아하지 않는다"},
                new int[][]{{1, 0, 0, 0, 0, 0, 0, -1}, {-1, 0, 0, 0, 0, 0, 0, 1}}));
        // 추가 질문 데이터 로드...
    }
    private void updateProgress() {
        int progress = (int) ((currentQuestionIndex / (float) totalQuestions) * 100);
        progressBar.setProgress(progress);
    }

    private void showQuestion(int index) {
        if (index > totalQuestions) {
            // 모든 질문 완료 -> 결과 화면으로 이동
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("userScores", userScores);
            startActivity(intent);
            finish();
            return;
        }

        // 현재 질문 데이터
        Question question = questions.get(index);
        questionTextView.setText(question.getText());
        option1Button.setText(question.getOptions()[0]);
        option2Button.setText(question.getOptions()[1]);
    }

    private void handleAnswer(int optionIndex) {
        // 선택한 답변의 scoreImpact 적용
        Question question = questions.get(currentQuestionIndex);
        int[] scoreImpact = question.getScoreImpact()[optionIndex];
        for (int i = 0; i < userScores.length; i++) {
            userScores[i] += scoreImpact[i];
        }

        // 다음 질문으로 이동
        currentQuestionIndex++;
        showQuestion(currentQuestionIndex);
    }
}

