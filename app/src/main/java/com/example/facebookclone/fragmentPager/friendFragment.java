package com.example.facebookclone.fragmentPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import com.example.facebookclone.R;
import com.example.facebookclone.searchUser;

public class friendFragment extends Fragment {

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

        return view;
    }
}
