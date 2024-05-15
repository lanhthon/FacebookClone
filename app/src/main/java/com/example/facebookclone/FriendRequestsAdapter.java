package com.example.facebookclone;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendRequestsAdapter extends ArrayAdapter<FriendRequest> {

    private Context mContext;
    private List<FriendRequest> mFriendRequests;

    public FriendRequestsAdapter(Context context, List<FriendRequest> friendRequests) {
        super(context, 0, friendRequests);
        mContext = context;
        mFriendRequests = friendRequests;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FriendRequest currentRequest = mFriendRequests.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_friend_request, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.textViewUserName2);
        ImageView imageViewProfile = convertView.findViewById(R.id.imageViewAvatar2);
        Button agreeFriend = convertView.findViewById(R.id.Agreefriend);
        Button disagreeFriend = convertView.findViewById(R.id.disagreefriend);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentRequest.senderId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);
                    String avatarSrc = dataSnapshot.child("avatarsrc").getValue(String.class);
                    if (avatarSrc != null && !avatarSrc.isEmpty()) {
                        Picasso.get().load(avatarSrc).into(imageViewProfile);
                    }
                    textViewName.setText(String.format("%s %s", firstName, lastName));
                } else {
                    Log.d("UserInfo", "User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserInfo", "Error: " + databaseError.getMessage());
            }
        });

        agreeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptFriendRequest(currentRequest);
            }
        });

        disagreeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineFriendRequest(currentRequest);
            }
        });

        return convertView;
    }

    private void acceptFriendRequest(FriendRequest request) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference().child("friends");

        // Add each other to friends list
        friendsRef.child(currentUserId).child(request.senderId).setValue(true);
        friendsRef.child(request.senderId).child(currentUserId).setValue(true);

        // Remove friend request from both sent and received requests
        removeFriendRequest(request);

        // Optionally notify the user or update the UI
    }

    private void declineFriendRequest(FriendRequest request) {
        // Remove friend request from both sent and received requests
        removeFriendRequest(request);

        // Optionally notify the user or update the UI
    }

    private void removeFriendRequest(FriendRequest request) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference friendRequestsRef = FirebaseDatabase.getInstance().getReference().child("friend_requests");

        // Remove from sent requests
        friendRequestsRef.child("sent_requests").child(request.senderId).orderByChild("receiverId").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FriendRequest", "Error: " + databaseError.getMessage());
            }
        });

        // Remove from received requests
        friendRequestsRef.child("received_requests").child(currentUserId).orderByChild("senderId").equalTo(request.senderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FriendRequest", "Error: " + databaseError.getMessage());
            }
        });
    }
}
