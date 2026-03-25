package com.example.cyberguard.domain.model;

public class CommunityPost {
    private final String id;
    private final String author;
    private final String content;
    private final long timestamp;
    private final boolean isModerated;

    public CommunityPost(String id, String author, String content, long timestamp, boolean isModerated) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.timestamp = timestamp;
        this.isModerated = isModerated;
    }

    public String getId() { return id; }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public boolean isModerated() { return isModerated; }
}
