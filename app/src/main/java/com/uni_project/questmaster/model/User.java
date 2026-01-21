package com.uni_project.questmaster.model;

import java.util.List;

public class User {
    private String uid;
    private String username;
    private String email;
    private String avatarUrl;
    private long ppq;
    private List<String> completedQuests;
    private List<String> savedQuests;
    private long totalPoints;
    private String description;
    private String profileImageUrl;


    public User() {}

    public User(String uid, String username, String email) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.avatarUrl = null;
        this.ppq = 0;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public long getPpq() {
        return ppq;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setPpq(long ppq) {
        this.ppq = ppq;
    }

    public List<String> getCompletedQuests() {
        return completedQuests;
    }

    public void setCompletedQuests(List<String> completedQuests) {
        this.completedQuests = completedQuests;
    }

    public List<String> getSavedQuests() {
        return savedQuests;
    }

    public void setSavedQuests(List<String> savedQuests) {
        this.savedQuests = savedQuests;
    }

    public long getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(long totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
