package com.uni_project.questmaster.model;

public class User {
    private String uid;
    private String username;
    private String email;
    private String avatarUrl;
    private long ppq;

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
}
