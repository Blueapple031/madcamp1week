package com.example.myapplication1.models;

public class Question {
    private String text;
    private String[] options;
    private int[][] scoreImpact;

    public Question(String text, String[] options, int[][] scoreImpact) {
        this.text = text;
        this.options = options;
        this.scoreImpact = scoreImpact;
    }

    public String getText() {
        return text;
    }

    public String[] getOptions() {
        return options;
    }

    public int[][] getScoreImpact() {
        return scoreImpact;
    }
}
