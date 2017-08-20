package com.example.mradmin.androidtestapp.entities;

/**
 * Created by mrAdmin on 18.08.2017.
 */

public class Post {

    private String title;
    private String description;
    private String userImage;
    private String userName;
    private long time;
    private int likesCount;

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
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

    public Post(String title, String description, String userImage, String userName, long time, int likesCount) {
        this.title = title;
        this.description = description;
        this.userImage = userImage;
        this.userName = userName;
        this.time = time;
        this.likesCount = likesCount;
    }
}
