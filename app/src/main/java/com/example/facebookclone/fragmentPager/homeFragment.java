// homeFragment.java
package com.example.facebookclone.fragmentPager;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facebookclone.Post;
import com.example.facebookclone.PostAdapter;
import com.example.facebookclone.R;
import com.example.facebookclone.userPost;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class homeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    EditText editTextPost;

    // Truy cập vào Firebase Realtime Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPosts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, getActivity());
        recyclerView.setAdapter(postAdapter);

        // Lấy tham chiếu của EditText
        editTextPost = view.findViewById(R.id.editTextPost);

        // Thiết lập OnClickListener cho EditText
        editTextPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi EditText được nhấn, mở userPost activity
                Intent intent = new Intent(getActivity(), userPost.class);
                startActivity(intent);
            }
        });

        // Lắng nghe sự thay đổi trong cơ sở dữ liệu Firebase
        database.getReference("posts").addValueEventListener(new ValueEventListener() {
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
                // Xử lý lỗi nếu có
            }
        });

        return view;
    }
}
