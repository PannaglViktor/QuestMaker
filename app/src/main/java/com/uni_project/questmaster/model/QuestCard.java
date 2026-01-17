package com.uni_project.questmaster.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.List;

public class QuestCard {
    private String username;
    private String description;
    private List<String> imageUrls;
    @ServerTimestamp
    private Date timestamp;

    public QuestCard() {}

    public QuestCard(String username, String description, List<String> imageUrls) {
        this.username = username;
        this.description = description;
        this.imageUrls = imageUrls;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
