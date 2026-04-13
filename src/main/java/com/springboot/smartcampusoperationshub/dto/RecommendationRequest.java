package com.springboot.smartcampusoperationshub.dto;

public class RecommendationRequest {
    private String intent;

    public RecommendationRequest() {}

    public RecommendationRequest(String intent) {
        this.intent = intent;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }
}
