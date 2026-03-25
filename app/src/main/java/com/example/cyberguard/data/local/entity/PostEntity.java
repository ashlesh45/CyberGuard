package com.example.cyberguard.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "posts")
public class PostEntity {
    @PrimaryKey
    @NonNull
    private final String id;
    private final String author;
    private final String content;
    private final long timestamp;

    public PostEntity(@NonNull String id, String author, String content, long timestamp) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.timestamp = timestamp;
    }

    @NonNull
    public String getId() { return id; }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
}
