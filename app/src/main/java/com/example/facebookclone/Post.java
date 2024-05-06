package com.example.facebookclone;

public class Post {
    private String userName;
    private String content;
    private int imageResource;

    public Post(String userName, String content, int imageResource) {
        this.userName = userName;
        this.content = content;
        this.imageResource = imageResource;
    }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return content;
    }

    public int getImageResource() {
        return imageResource;
    }
}
