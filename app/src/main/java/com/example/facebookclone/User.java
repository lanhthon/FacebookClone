package com.example.facebookclone;

public class User {
    public String userId;
    public String firstName;
    public String lastName;
    public String avatarsrc;
    public String coversrc;
    public String hometown;
    public String shool;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCoversrc() {
        return coversrc;
    }

    public String getAvatarsrc() {
        return avatarsrc;
    }

    public String getHometown() {
        return hometown;
    }

    public String getShool() {
        return shool;
    }

    public User(String userId, String firstName, String lastName, String avatarsrc,String coversrc, String hometown, String shool) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatarsrc = avatarsrc;
        this.coversrc = coversrc;
        this.hometown = hometown;
        this.shool = shool;
    }
}
