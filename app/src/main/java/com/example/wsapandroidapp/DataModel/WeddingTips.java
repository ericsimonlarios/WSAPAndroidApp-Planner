package com.example.wsapandroidapp.DataModel;

import java.util.ArrayList;
import java.util.List;

public class WeddingTips {

    private String id, topic, description, tips;
    private String dateCreated, author;
    private List<TipsImages> tipsImages;

    public WeddingTips() {
    }

    public WeddingTips(String id, String topic, String description, List<TipsImages>  tipsImages,String dateCreated) {
        this.id = id;
        this.topic = topic;
        this.description = description;
        this.tipsImages = tipsImages;
        this.dateCreated = dateCreated;

    }
    public WeddingTips(String id, String topic, String description, String tips, List<TipsImages> tipsImages, String author, String dateCreated) {
        this.id = id;
        this.topic = topic;
        this.description = description;
        this.tips = tips;
        this.tipsImages = tipsImages;
        this.dateCreated = dateCreated;
        this.author = author;
    }
    public WeddingTips(String id, String topic, String description, String tips, String author, String dateCreated) {
        this.id = id;
        this.topic = topic;
        this.description = description;
        this.tips = tips;
        this.dateCreated = dateCreated;
        this.author = author;
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
    public String getAuthor() {return  author;}
    public List<TipsImages> getTipsImages() {return tipsImages;}

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
    public void setAuthor(String author) {
        this.author = author;
    }
    public void setTipsImages(List<TipsImages> tipsImages) {
        this.tipsImages = tipsImages;
    }

}
