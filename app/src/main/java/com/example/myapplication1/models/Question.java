package com.example.myapplication1.models;

public class Question {
    private int id;
    private String question;
    private String option1;
    private String option2;
    private int[] option1Impact;
    private int[] option2Impact;

    public Question(int id, String question, String option1, String option2, int[] option1Impact, int[] option2Impact) {
        this.id = id;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option1Impact = option1Impact;
        this.option2Impact = option2Impact;
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public int[] getOption1Impact() {
        return option1Impact;
    }

    public int[] getOption2Impact() {
        return option2Impact;
    }
}
