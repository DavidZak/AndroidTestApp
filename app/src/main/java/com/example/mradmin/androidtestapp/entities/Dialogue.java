package com.example.mradmin.androidtestapp.entities;

/**
 * Created by mrAdmin on 09.08.2017.
 */

public class Dialogue {

    private String name;
    private String image;
    private String lastMessage;
    private String thumbImage;
    private long time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(String thumbImage) {
        this.thumbImage = thumbImage;
    }

    public Dialogue(){

    }

    public Dialogue(String name, String image, String lastMessage, String thumbImage, long time) {
        this.name = name;
        this.image = image;
        this.lastMessage = lastMessage;
        this.thumbImage = thumbImage;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}

