package com.example.facebookclone.fragmentPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.facebookclone.R;
import com.example.facebookclone.searchUser;
import com.example.facebookclone.FriendRequest;
import com.example.facebookclone.FriendRequestsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class friendFragment extends Fragment {

    private ListView mFriendRequestsListView;
    private FriendRequestsAdapter mFriendRequestsAdapter;
    private List<FriendRequest> mFriendRequestsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        ImageButton searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), searchUser.class)));

        // Initialize ListView and Adapter for Friend Requests
        mFriendRequestsListView = view.findViewById(R.id.friendRequestsListView);
        mFriendRequestsList = new ArrayList<>();
        mFriendRequestsAdapter = new FriendRequestsAdapter(getActivity(), mFriendRequestsList);
        mFriendRequestsListView.setAdapter(mFriendRequestsAdapter);


        // Load friend requests
        loadFriendRequests();

        return view;
    }

    private void loadFriendRequests() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            return;
        }

        String userId = currentUser.getUid();
        // Assuming you have a Firebase Database structure like: "friend_requests"
        DatabaseReference friendRequestsRef = FirebaseDatabase.getInstance().getReference().child("friend_requests").child("received_requests").child(userId);
        friendRequestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mFriendRequestsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FriendRequest friendRequest = snapshot.getValue(FriendRequest.class);
                    mFriendRequestsList.add(friendRequest);
                }
                mFriendRequestsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
}
