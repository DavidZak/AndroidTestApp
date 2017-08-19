package com.example.mradmin.androidtestapp.entities;

/**
 * Created by mrAdmin on 18.08.2017.
 */

public class Post {

    private String title;
    private String description;
    private String userImage;
    private String userName;
    private String time;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public Post() {
    }

    public Post(String title, String description, String userImage, String userName, String time) {
        this.title = title;
        this.description = description;
        this.userImage = userImage;
        this.userName = userName;
        this.time = time;
    }
}
