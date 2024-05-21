// HomeActivity.java
package com.example.facebookclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostDetailActivity  extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private String postId;

    // Access Firebase Realtime Database
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        recyclerView = findViewById(R.id.recyclerViewPosts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, this);
        recyclerView.setAdapter(postAdapter);

        // Get reference to EditText


        // Set OnClickListener for EditText


        // Get postId from intent
        postId = getIntent().getStringExtra("postId");

        // Listen for changes in Firebase database
        database.getReference("posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String firebasePostId = postSnapshot.child("postId").getValue(String.class);
                    if (firebasePostId.equals(postId)) {
                        // Get data from Firebase
                        Integer likesCount = postSnapshot.child("likesCount").getValue(Integer.class);
                        String userName = postSnapshot.child("userName").getValue(String.class);
                        String content = postSnapshot.child("content").getValue(String.class);
                        String imageUrl = postSnapshot.child("image_url").getValue(String.class);
                        String userIdPost = postSnapshot.child("userId").getValue(String.class);
                        String time = postSnapshot.child("time").getValue(String.class);
                        HashMap<String, Object> likesMap = (HashMap<String, Object>) postSnapshot.child("likes").getValue();

                        // Create Post object from Firebase data
                        Post post = new Post(postId, likesCount, userName, content, imageUrl, likesMap, userIdPost, time);
                        postList.add(post);

                        // Notify adapter of data change
                        postAdapter.notifyDataSetChanged();
                        return; // No need to continue iterating
                    }
                }
                // If no post found with postId
                Toast.makeText(PostDetailActivity.this, "No post found with postId: " + postId, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors
                Toast.makeText(PostDetailActivity.this, "Failed to retrieve post: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
