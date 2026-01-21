package com.uni_project.questmaster.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Comment {
    private String userId;
    private String username;
    private String avatarUrl;
    private String text;
    @ServerTimestamp
    private Date timestamp;

    public Comment() {
    }

    public Comment(String userId, String username, String avatarUrl, String text) {
        this.userId = userId;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.text = text;
        this.timestamp = new Date();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
