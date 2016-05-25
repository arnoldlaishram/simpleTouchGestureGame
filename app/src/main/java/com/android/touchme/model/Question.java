package com.android.touchme.model;

/**
 * Created by arnold on 24/5/16.
 */
public class Question {

    private String question;

    private String questionType;

    public Question() {
    }

    public Question(String question, String questionType) {
        this.question = question;
        this.questionType = questionType;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }
}
