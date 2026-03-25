package com.example.cyberguard.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "fraud_types")
public class FraudEntity {
    @PrimaryKey
    private final int id;
    private final String title;
    private final String description;
    private final String preventionTips; // Comma separated
    private final String commonTactics;  // Comma separated

    public FraudEntity(int id, String title, String description, String preventionTips, String commonTactics) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.preventionTips = preventionTips;
        this.commonTactics = commonTactics;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPreventionTips() { return preventionTips; }
    public String getCommonTactics() { return commonTactics; }
}
