package com.example.cyberguard.domain.model;

import java.util.List;

public class FraudType {
    private final int id;
    private final String title;
    private final String description;
    private final List<String> preventionTips;
    private final List<String> commonTactics;
    private final String imageUrl;

    public FraudType(int id, String title, String description, List<String> preventionTips, List<String> commonTactics, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.preventionTips = preventionTips;
        this.commonTactics = commonTactics;
        this.imageUrl = imageUrl;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<String> getPreventionTips() { return preventionTips; }
    public List<String> getCommonTactics() { return commonTactics; }
    public String getImageUrl() { return imageUrl; }
}
