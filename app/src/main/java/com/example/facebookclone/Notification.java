package com.example.facebookclone;

public class Notification {
    private String message;
    private String timestamp;
    private String senderName;
    private String senderAvatar;

    public Notification(String message, String timestamp, String senderName, String senderAvatar) {
        this.message = message;
        this.timestamp = timestamp;
        this.senderName = senderName;
        this.senderAvatar = senderAvatar;
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
