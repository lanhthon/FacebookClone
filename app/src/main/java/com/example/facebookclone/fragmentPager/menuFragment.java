package com.example.facebookclone.fragmentPager;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.facebookclone.MainActivity;
import com.example.facebookclone.R;
import com.google.firebase.auth.FirebaseAuth;

public class menuFragment extends Fragment {

    private FirebaseAuth mAuth;

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

        // Gắn sự kiện click cho nút đăng xuất
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đăng xuất người dùn hiện tại
                mAuth.signOut();
                // Redirect hoặc thực hiện hành động phù hợp sau khi đăng xuất
                // Ví dụ: chuyển về màn hình đăng nhập
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

            }
        });

        return view;
    }
}
