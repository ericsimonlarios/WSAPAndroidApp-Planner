package com.example.wsapandroidapp.DataModel;

public class WeddingTips {

    private String id, topic, description, tips, image;
    private long date_created;

    public WeddingTips() {
    }

    public WeddingTips(String id, String topic, String description, String image, long date_created) {
        this.id = id;
        this.topic = topic;
        this.description = description;
        this.image = image;
        this.date_created = date_created;
    }
    public WeddingTips(String id, String topic, String description, String tips, String image, long date_created) {
        this.id = id;
        this.topic = topic;
        this.description = description;
        this.tips = tips;
        this.image = image;
        this.date_created = date_created;
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
    public long getDate_created() {
        return date_created;
    }

}
