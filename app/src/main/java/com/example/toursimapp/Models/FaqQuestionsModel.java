package com.example.toursimapp.Models;

public class FaqQuestionsModel {

    String faq_ques, faq_ans;

    public FaqQuestionsModel(String faq_ques, String faq_ans) {
        this.faq_ques = faq_ques;
        this.faq_ans = faq_ans;
    }

    public String getFaq_ques() {
        return faq_ques;
    }

    public String getFaq_ans() {
        return faq_ans;
    }
}
