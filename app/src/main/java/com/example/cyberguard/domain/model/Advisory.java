package com.example.cyberguard.domain.model;

public class Advisory {
    private final String id;
    private final String title;
    private final String content;
    private final long timestamp;
    private final String source;

    public Advisory(String id, String title, String content, long timestamp, String source) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.source = source;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public String getSource() { return source; }
}
