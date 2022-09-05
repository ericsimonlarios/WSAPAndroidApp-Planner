package com.example.wsapandroidapp.DataModel;

import java.util.ArrayList;
import java.util.List;

public class WeddingTips {

    private String id, topic, description, tips;
    private String dateCreated;

    public WeddingTips() {
    }

    public WeddingTips(String id, String topic, String description, String dateCreated) {
        this.id = id;
        this.topic = topic;
        this.description = description;
        this.dateCreated = dateCreated;
    }
    public WeddingTips(String id, String topic, String description, String tips, String dateCreated) {
        this.id = id;
        this.topic = topic;
        this.description = description;
        this.tips = tips;
        this.dateCreated = dateCreated;
    }

    public String getId() {
        return id;
    }
    public String getTopic() {
        return topic;
    }
    public String getDescription() {
        return description;
    }
    public String getTips() {
        return tips;
    }
    public String getDateCreated() {
        return dateCreated;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setTips(String tips) {
        this.tips = tips;
    }
    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

}
