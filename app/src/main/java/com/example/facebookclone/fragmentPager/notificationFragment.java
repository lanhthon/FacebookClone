package com.example.facebookclone.fragmentPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facebookclone.Notification;
import com.example.facebookclone.NotificationAdapter;
import com.example.facebookclone.R;
import com.example.facebookclone.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class notificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private DatabaseReference databaseReference;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String userId = currentUser.getUid();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Listen for friend requests
        listenForFriendRequests();

        // Listen for new posts
        listenForNewPosts();

        return view;
    }

    private void listenForFriendRequests() {
        databaseReference.child("friend_requests/received_requests").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String senderId = userSnapshot.child("senderId").getValue(String.class);
                    String status = userSnapshot.child("status").getValue(String.class);

                    if ("pending".equals(status)) {
                        fetchSenderDetails(senderId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void fetchSenderDetails(String senderId) {
        databaseReference.child("users").child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User sender = dataSnapshot.getValue(User.class);
                if (sender != null) {
                    notificationList.add(new Notification(
                            sender.getFirstName() + " " + sender.getLastName() + " Đã giửi cho bạn lời mời kết bạn",
                            "Ngay bây giờ",
                            sender.getFirstName() + " " + sender.getLastName(),
                            sender.getAvatarsrc(),
                            senderId,
                            true // This is a friend request
                    ));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void listenForNewPosts() {
        databaseReference.child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String postId = postSnapshot.child("postId").getValue(String.class);
                    String postUserId = postSnapshot.child("userId").getValue(String.class);
                    String content = postSnapshot.child("content").getValue(String.class);
                    String time = postSnapshot.child("time").getValue(String.class);

                    fetchPostUserDetails(postUserId, content, time,postId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void fetchPostUserDetails(String postUserId, String content, String time,String postId) {
        databaseReference.child("users").child(postUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    notificationList.add(new Notification(
                            user.getFirstName() + " " + user.getLastName() + " Đã đăng bài viết: " + content,
                            time,
                            user.getFirstName() + " " + user.getLastName(),
                            user.getAvatarsrc(),
                            postId,
                            false // This is a new post
                    ));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
}
