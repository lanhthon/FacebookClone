package com.example.facebookclone;

import java.util.HashMap;

public class Post {
    private String postId;
    private Integer likesCount;
    private String userName;
    private String content;
    private String imageUrl;
    private HashMap<String, Object> likes;
    private String userId;
    private String time;
    private int commentsCount;

    // No-argument constructor
    public Post() {
    }

    public Post(String postId, Integer likesCount, String userName, String content, String imageUrl, HashMap<String, Object> likes, String userId, String time) {
        this.postId = postId;
        this.likesCount = likesCount;
        this.userName = userName;
        this.content = content;
        this.imageUrl = imageUrl;
        this.likes = likes;
        this.userId = userId;
        this.time = time;
    }

    // Getters and setters for all fields

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public HashMap<String, Object> getLikes() {
        return likes;
    }

    public void setLikes(HashMap<String, Object> likes) {
        this.likes = likes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }
}
