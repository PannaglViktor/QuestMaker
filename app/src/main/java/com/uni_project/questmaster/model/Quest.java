package com.uni_project.questmaster.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Quest {

    @Exclude
    private String id;

    private String title;
    private String description;
    private String ownerId;
    private String ownerName;
    private List<String> imageUrls;
    @ServerTimestamp
    private Date timestamp;
    private List<String> savedBy;

    private long ppq;

    private QuestLocation startPoint;
    private QuestLocation endPoint;
    private QuestLocation location;


    public Quest() {
    }

    public Quest(String title, String description, String ownerId, String ownerName, List<String> imageUrls, List<String> savedBy, long ppq) {
        this.title = title;
        this.description = description;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.imageUrls = imageUrls;
        this.savedBy = savedBy;
        this.ppq = ppq;
    }

    public Quest(String title, String description, String ownerId, String ownerName, List<String> imageUrls, List<String> savedBy, QuestLocation startPoint, QuestLocation endPoint, QuestLocation location, long ppq) {
        this.title = title;
        this.description = description;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.imageUrls = imageUrls;
        this.savedBy = savedBy;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.location = location;
        this.ppq = ppq;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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

    public List<String> getSavedBy() {
        return savedBy;
    }

    public void setSavedBy(List<String> savedBy) {
        this.savedBy = savedBy;
    }

    public QuestLocation getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(QuestLocation startPoint) {
        this.startPoint = startPoint;
    }

    public QuestLocation getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(QuestLocation endPoint) {
        this.endPoint = endPoint;
    }

    public QuestLocation getLocation() {
        return location;
    }

    public void setLocation(QuestLocation location) {
        this.location = location;
    }

    public long getPpq() {
        return ppq;
    }

    public void setPpq(long ppq) {
        this.ppq = ppq;
    }
}
