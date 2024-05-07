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
import com.example.facebookclone.userPost; // Import ActivityUserPostHome


import java.util.ArrayList;
import java.util.List;

public class homeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    EditText editTextPost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPosts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        postList = new ArrayList<>();
        // Thêm các bài viết vào danh sách
        postList.add(new Post("User1", "Content 1", R.drawable.logo));
        postList.add(new Post("User2", "Content 2", R.drawable.logo));
        // ...

        postAdapter = new PostAdapter(postList, getActivity());
        recyclerView.setAdapter(postAdapter);

        editTextPost = view.findViewById(R.id.editTextPost);
        editTextPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), userPost.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
