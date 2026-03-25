package com.example.cyberguard.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "advisories")
public class AdvisoryEntity {
    @PrimaryKey
    @NonNull
    private final String id;
    private final String title;
    private final String content;
    private final long timestamp;
    private final String source;

    public AdvisoryEntity(@NonNull String id, String title, String content, long timestamp, String source) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.source = source;
    }

    @NonNull
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public String getSource() { return source; }
}
