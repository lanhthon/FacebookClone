package com.example.facebookclone;

public class Notification {
    private String message;
    private String timestamp;
    private String senderName;
    private String senderAvatar;
    private String id; // New field to store userId or postId
    private boolean isFriendRequest; // To distinguish between friend requests and posts


    public Notification(String message, String timestamp, String senderName, String senderAvatar,String id, boolean isFriendRequest) {
        this.message = message;
        this.timestamp = timestamp;
        this.senderName = senderName;
        this.senderAvatar = senderAvatar;
        this.id = id;
        this.isFriendRequest = isFriendRequest;
    }
    public String getId() {
        return id;
    }

    public boolean isFriendRequest() {
        return isFriendRequest;
    }
    public String getMessage() {
        return message;
    }

    public String getTime() {
        return timestamp;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }
}
