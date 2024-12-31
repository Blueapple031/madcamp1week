package com.example.myapplication1.repository;

import android.content.Context;
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

public class QuestionRepository {
    private static QuestionRepository instance;
    private final List<Question> questions = new ArrayList<>();

    private QuestionRepository(Context context) {
        loadQuestionsFromJson(context);
    }

    public static QuestionRepository getInstance(Context context) {
        if (instance == null) {
            instance = new QuestionRepository(context);
        }
        return instance;
    }

    private void loadQuestionsFromJson(Context context) {
        try {
            String jsonData = loadJsonFromAssets(context, "questions.json");
            JSONArray jsonArray = new JSONArray(jsonData);

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
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private String loadJsonFromAssets(Context context, String fileName) throws IOException {
        try (InputStream inputStream = context.getAssets().open(fileName)) {
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        }
    }

    private int[] jsonArrayToIntArray(JSONArray jsonArray) throws JSONException {
        int[] result = new int[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            result[i] = jsonArray.getInt(i);
        }
        return result;
    }

    public List<Question> getAllQuestions() {
        return new ArrayList<>(questions); // 복사본 반환
    }

    public List<Question> getRandomQuestions(int count) {
        Collections.shuffle(questions);
        return questions.subList(0, Math.min(count, questions.size()));
    }
}
