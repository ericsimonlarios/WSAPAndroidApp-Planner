package com.example.wsapandroidapp.DataModel;

public class WeddingTips {

    private String id, topic, description, tips, image;
    private String dateCreated;

    public WeddingTips() {
    }

    public WeddingTips(String id, String topic, String description, String image, String dateCreated) {
        this.id = id;
        this.topic = topic;
        this.description = description;
        this.image = image;
        this.dateCreated = dateCreated;
    }
    public WeddingTips(String id, String topic, String description, String tips, String image, String dateCreated) {
        this.id = id;
        this.topic = topic;
        this.description = description;
        this.tips = tips;
        this.image = image;
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
    public String getImage() {
        return image;
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
    public void setImage(String image) {
        this.image = image;
    }
    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

}
