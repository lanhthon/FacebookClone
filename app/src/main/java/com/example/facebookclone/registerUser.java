package com.example.facebookclone;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class registerUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String ho = getIntent().getStringExtra("HO");
        String ten = getIntent().getStringExtra("TEN");
        String ngaySinh = getIntent().getStringExtra("NGAY_SINH");

        // Hiển thị dữ liệu trên giao diện của registerUser
        TextView hoTextView = findViewById(R.id.textView2);
        TextView tenTextView = findViewById(R.id.textView3);
        TextView ngaySinhTextView = findViewById(R.id.textView4);
        hoTextView.setText("Họ: " + ho);
        tenTextView.setText("Tên: " + ten);
        ngaySinhTextView.setText("Ngày sinh: " + ngaySinh);
    }
}