package com.example.facebookclone;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Post {
    private String postId;
    private String userName;
    private String content;
    private String imageUrl;
    private int likesCount;
    private String useridpost;
    private String time;
    private HashMap<String, Object> likes;

    // No-argument constructor


    public Post(String postId, int likesCount, String userName, String content, String imageUrl, HashMap<String, Object> likes,String useridpost,String time) {
        this.postId = postId;
        this.userName = userName;
        this.content = content;
        this.imageUrl = imageUrl;
        this.likesCount = likesCount;
        this.likes = likes;
        this.useridpost = useridpost;
        this.time = time;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUseridpost() {
        return useridpost;
    }
    public String gettime() {
        return time;
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
