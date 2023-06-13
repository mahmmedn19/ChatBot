package com.example.chatbot.data;

import java.util.List;

public class Question {
    String tag;
    List<String> patterns;
    List<String> responses;
    String context_set;

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    public void setResponses(List<String> responses) {
        this.responses = responses;
    }

    public void setContext_set(String context_set) {
        this.context_set = context_set;
    }

    public String getTag() {
        return tag;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public List<String> getResponses() {
        return responses;
    }

    public String getContext_set() {
        return context_set;
    }
}
