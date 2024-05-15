package com.example.facebookclone;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {
    public UserAdapter(Context context, List<User> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user_search, parent, false);
        }

        TextView textViewUserName = convertView.findViewById(R.id.textViewUserName);
        TextView textViewUserDescription = convertView.findViewById(R.id.textViewUserDescription);
        ImageView avatarImageView = convertView.findViewById(R.id.imageViewAvatar);
        Button addFriendButton = convertView.findViewById(R.id.addfriend);

        if (user.avatarsrc != null && !user.avatarsrc.isEmpty()) {
            Picasso.get().load(user.avatarsrc).into(avatarImageView);
        }
        textViewUserName.setText(user.firstName + " " + user.lastName);
        textViewUserDescription.setText(user.getHometown());

        checkFriendStatus(user.userId, addFriendButton);

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonText = addFriendButton.getText().toString();
                if (buttonText.equals("Thêm bạn bè")) {
                    sendFriendRequest(user.userId, addFriendButton);
                } else if (buttonText.equals("Hủy")) {
                    cancelFriendRequest(user.userId, addFriendButton);
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), profile.class);
                intent.putExtra("userId", user.userId);
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }

    void sendFriendRequest(String receiverId, Button addFriendButton) {
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String requestId = FirebaseDatabase.getInstance().getReference("friend_requests").push().getKey();

        FriendRequest request = new FriendRequest(senderId, receiverId, "pending");

        FirebaseDatabase.getInstance().getReference("friend_requests")
                .child("sent_requests")
                .child(senderId)
                .child(requestId)
                .setValue(request);

        FirebaseDatabase.getInstance().getReference("friend_requests")
                .child("received_requests")
                .child(receiverId)
                .child(requestId)
                .setValue(request);

        addFriendButton.setText("Hủy");
    }

    void cancelFriendRequest(String receiverId, Button addFriendButton) {
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference sentRequestsRef = FirebaseDatabase.getInstance().getReference("friend_requests")
                .child("sent_requests")
                .child(senderId);

        DatabaseReference receivedRequestsRef = FirebaseDatabase.getInstance().getReference("friend_requests")
                .child("received_requests")
                .child(receiverId);

        sentRequestsRef.orderByChild("receiverId").equalTo(receiverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
                addFriendButton.setText("Thêm bạn bè");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });

        receivedRequestsRef.orderByChild("senderId").equalTo(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
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
    }

    void checkFriendStatus(String userId, Button addFriendButton) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("friends").child(currentUserId);

        friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId)) {
                    addFriendButton.setText("Bạn bè");
                    addFriendButton.setEnabled(false);
                } else {
                    checkFriendRequestStatus(userId, addFriendButton);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    void checkFriendRequestStatus(String receiverId, Button addFriendButton) {
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference sentRequestsRef = FirebaseDatabase.getInstance().getReference("friend_requests")
                .child("sent_requests")
                .child(senderId);

        sentRequestsRef.orderByChild("receiverId").equalTo(receiverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FriendRequest request = snapshot.getValue(FriendRequest.class);
                        if (request != null && "pending".equals(request.status)) {
                            addFriendButton.setText("Hủy");
                            return;
                        }
                    }
                }
                addFriendButton.setText("Thêm bạn bè");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
}
