package com.example.facebookclone;

import java.util.HashMap;
import java.util.Map;

public class Post {
    private String postId;
    private String userName;
    private String content;
    private String imageUrl;
    private int likesCount;
    private HashMap<String, Object> likes;

    // No-argument constructor


    public Post(String postId, int likesCount, String userName, String content, String imageUrl, HashMap<String, Object> likes) {
        this.postId = postId;
        this.userName = userName;
        this.content = content;
        this.imageUrl = imageUrl;
        this.likesCount = likesCount;
        this.likes = likes;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public HashMap<String, Object> getLikes() {
        return likes;
    }

    public void setLikes(HashMap<String, Object> likes) {
        this.likes = likes;
    }
}
