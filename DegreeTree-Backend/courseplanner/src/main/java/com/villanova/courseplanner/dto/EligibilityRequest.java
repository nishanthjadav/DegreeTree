package com.villanova.courseplanner.dto;

import java.util.List;

public class EligibilityRequest {
    private List<String> completed;

    public EligibilityRequest() {
    }

    public List<String> getCompleted() {
        return completed;
    }

    public void setCompleted(List<String> completed) {
        this.completed = completed;
    }
}
