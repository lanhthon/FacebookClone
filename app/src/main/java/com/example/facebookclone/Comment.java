package com.example.facebookclone;

public class Comment {
    private String commentId;
    private String postId;
    private String userId;
    private String content;

    public Comment() {
        // Constructor mặc định cần thiết cho Firebase Realtime Database
    }

    public Comment(String commentId, String postId, String userId, String content) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.content = content;
    }

    // Getter và setter cho commentId
    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    // Getter và setter cho postId
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    // Getter và setter cho userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter và setter cho content
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
