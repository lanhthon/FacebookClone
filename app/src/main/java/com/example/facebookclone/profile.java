// profile.java
package com.example.facebookclone;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class profile extends AppCompatActivity {

    private RecyclerView friendsRecyclerView, postsRecyclerView;
    private FriendAdapter friendAdapter;
    private PostAdapter postAdapter;
    private List<User> friendsList;
    private List<Post> postList;
    private String userIdintent;
    private Button editProfileButton, addFriendButton, cancelRequestButton, friendButton;
    private ImageView avatarImage, coverImage, avatarUserpost;
    private TextView fullName, school, hometown, postTextView;
    private LinearLayout userPost;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userIdintent = getIntent().getStringExtra("userId");

        friendsRecyclerView = findViewById(R.id.friendls);
        friendsRecyclerView.setHasFixedSize(true);
        friendsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        postsRecyclerView = findViewById(R.id.postsRecyclerView);
        postsRecyclerView.setHasFixedSize(true);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        friendsList = new ArrayList<>();
        postList = new ArrayList<>();

        friendAdapter = new FriendAdapter(this, friendsList);
        friendsRecyclerView.setAdapter(friendAdapter);

        postAdapter = new PostAdapter(postList, this);
        postsRecyclerView.setAdapter(postAdapter);

        editProfileButton = findViewById(R.id.editProfileButton);
        addFriendButton = findViewById(R.id.addFriendButton);
        cancelRequestButton = findViewById(R.id.cancelRequestButton);
        friendButton = findViewById(R.id.friendButton);
        avatarImage = findViewById(R.id.imageViewAvatar);
        coverImage = findViewById(R.id.ImageViewcover);
        fullName = findViewById(R.id.usernameTextView);
        school = findViewById(R.id.shool);
        hometown = findViewById(R.id.hometown);
        userPost = findViewById(R.id.userpost);
        postTextView = findViewById(R.id.posttextview);
        avatarUserpost = findViewById(R.id.avatarUser);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userIdintent);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    if (userIdintent.equals(currentUserId)) {
                        editProfileButton.setVisibility(View.VISIBLE);
                        userPost.setVisibility(View.VISIBLE);
                        Glide.with(profile.this).load(user.getAvatarsrc()).into(avatarUserpost);
                    } else {
                        checkFriendStatus(currentUserId, userIdintent);
                        postTextView.setText("Bài viết của " + user.getLastName());
                    }
                    displayUserInfo(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profile.this, editprofile.class);
                startActivity(intent);
            }
        });



        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("friends").child(userIdintent);
        friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    String friendId = friendSnapshot.getKey();
                    getUserDetails(friendId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });

        // Fetch posts of the user
        fetchUserPosts(userIdintent);

        setupButtonActions(currentUserId, userIdintent);
    }

    private void fetchUserPosts(String userId) {
        Query userPostsQuery = FirebaseDatabase.getInstance().getReference("posts").orderByChild("userId").equalTo(userId);
        userPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // Lấy dữ liệu từ Firebase
                    String postId = postSnapshot.child("postId").getValue(String.class);
                    Integer likesCount = postSnapshot.child("likesCount").getValue(Integer.class);
                    String userName = postSnapshot.child("userName").getValue(String.class);
                    String content = postSnapshot.child("content").getValue(String.class);
                    String imageUrl = postSnapshot.child("image_url").getValue(String.class);
                    String useridpost = postSnapshot.child("userId").getValue(String.class);
                    String time = postSnapshot.child("time").getValue(String.class);
                    HashMap<String, Object> likesMap = (HashMap<String, Object>) postSnapshot.child("likes").getValue();
                    // Tạo đối tượng Post từ dữ liệu Firebase

                    Post post = new Post(postId, likesCount, userName, content, imageUrl, likesMap,useridpost,time);

                    postList.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void displayUserInfo(User user) {
        fullName.setText(user.getFirstName() + " " + user.getLastName());
        school.setText(user.getShool());
        hometown.setText(user.getHometown());
        Glide.with(this).load(user.getAvatarsrc()).into(avatarImage);
        Glide.with(this).load(user.getCoversrc()).into(coverImage);
    }

    private void checkFriendStatus(String currentUserId, String userIdintent) {
        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("friends").child(currentUserId);
        friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userIdintent)) {
                    friendButton.setVisibility(View.VISIBLE);
                } else {
                    checkFriendRequestStatus(currentUserId, userIdintent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void checkFriendRequestStatus(String currentUserId, String userIdintent) {
        DatabaseReference sentRequestsRef = FirebaseDatabase.getInstance().getReference("friend_requests")
                .child("sent_requests")
                .child(currentUserId);

        sentRequestsRef.orderByChild("receiverId").equalTo(userIdintent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FriendRequest request = snapshot.getValue(FriendRequest.class);
                        if (request != null && "pending".equals(request.status)) {
                            cancelRequestButton.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                }
                addFriendButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void setupButtonActions(String currentUserId, String userIdintent) {
        addFriendButton.setOnClickListener(v -> sendFriendRequest(currentUserId, userIdintent));
        cancelRequestButton.setOnClickListener(v -> cancelFriendRequest(currentUserId, userIdintent));
        friendButton.setOnClickListener(v -> removeFriend(currentUserId, userIdintent));
    }

    private void sendFriendRequest(String currentUserId, String userIdintent) {
        String requestId = FirebaseDatabase.getInstance().getReference("friend_requests").push().getKey();

        FriendRequest request = new FriendRequest(currentUserId, userIdintent, "pending");

        FirebaseDatabase.getInstance().getReference("friend_requests")
                .child("sent_requests")
                .child(currentUserId)
                .child(requestId)
                .setValue(request);

        FirebaseDatabase.getInstance().getReference("friend_requests")
                .child("received_requests")
                .child(userIdintent)
                .child(requestId)
                .setValue(request);

        addFriendButton.setVisibility(View.GONE);
        cancelRequestButton.setVisibility(View.VISIBLE);
    }

    private void cancelFriendRequest(String currentUserId, String userIdintent) {
        DatabaseReference sentRequestsRef = FirebaseDatabase.getInstance().getReference("friend_requests")
                .child("sent_requests")
                .child(currentUserId);

        DatabaseReference receivedRequestsRef = FirebaseDatabase.getInstance().getReference("friend_requests")
                .child("received_requests")
                .child(userIdintent);

        sentRequestsRef.orderByChild("receiverId").equalTo(userIdintent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });

        receivedRequestsRef.orderByChild("senderId").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });

        cancelRequestButton.setVisibility(View.GONE);
        addFriendButton.setVisibility(View.VISIBLE);
    }

    private void removeFriend(String currentUserId, String userIdintent) {
        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("friends");
        friendsRef.child(currentUserId).child(userIdintent).removeValue();
        friendsRef.child(userIdintent).child(currentUserId).removeValue();
        friendButton.setVisibility(View.GONE);
        addFriendButton.setVisibility(View.VISIBLE);
    }

    private void getUserDetails(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null && friendsList.size() < 6) {
                    friendsList.add(user);
                    friendAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
}
