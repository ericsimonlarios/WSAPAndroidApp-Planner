package com.example.wsapandroidapp.DataModel;

public class WeddingTips {

    private String id, topic, description, tips;
    private String dateCreated;
    private TipsImages tipsImages;

    public WeddingTips() {
    }

    public WeddingTips(String id, String topic, String description, TipsImages  tipsImages, String dateCreated) {
        this.id = id;
        this.topic = topic;
        this.description = description;
        this.tipsImages = tipsImages;
        this.dateCreated = dateCreated;
    }
    public WeddingTips(String id, String topic, String description, String tips, TipsImages tipsImages, String dateCreated) {
        this.id = id;
        this.topic = topic;
        this.description = description;
        this.tips = tips;
        this.tipsImages = tipsImages;
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
    public TipsImages getTipsImages() {return tipsImages;}

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
    public void setTipsImages(TipsImages tipsImages) {
        this.tipsImages = tipsImages;
    }

}
