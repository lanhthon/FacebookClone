package com.example.facebookclone.fragmentPager;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.facebookclone.MainActivity;
import com.example.facebookclone.R;
import com.example.facebookclone.profile;
import com.example.facebookclone.searchUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class menuFragment extends Fragment {

    private FirebaseAuth mAuth;
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // Tìm nút đăng xuất từ layout
        Button logoutButton = view.findViewById(R.id.logoutButton);
        ImageButton searchButton = view.findViewById(R.id.searchButton);
        TextView textViewuser=view.findViewById(R.id.textViewUserName2);
        ImageView avartaruser=view.findViewById(R.id.imageViewAvatar2);
        LinearLayout infoprofile=view.findViewById(R.id.infoprofile);
        searchButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), searchUser.class)));
        infoprofile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), profile.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });


        // Gắn sự kiện click cho nút đăng xuất
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                FirebaseAuth.getInstance().signOut();  // Đăng xuất khỏi Firebase
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });




        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);
                    String avatarSrc = dataSnapshot.child("avatarsrc").getValue(String.class);
                    textViewuser.setText(String.format("%s %s", firstName, lastName));
                    if (avatarSrc != null) {
                        Glide.with(getContext()).load(avatarSrc).into(avartaruser);
                    }
                } else {
                    Log.d("UserInfo", "User not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });



        return view;
    }


}
