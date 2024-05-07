package com.example.facebookclone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class userPost extends AppCompatActivity {

    private Button postButton;
    private boolean isEditTextFilled = false;
    private ImageView imgSelectPost;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post);

        // Tìm ImageView để đăng bài viết
        postButton = findViewById(R.id.postButton);
        postButton.setVisibility(View.GONE); // Ẩn nút đăng bài ban đầu

        // Tìm EditText
        EditText inputEditText = findViewById(R.id.inputEditText);

        // Tìm ImageView cho việc chọn ảnh
        imgSelectPost = findViewById(R.id.imgSelectPost);

        // Lắng nghe sự thay đổi trong EditText để kiểm tra xem nó có chứa văn bản hay không
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Kiểm tra xem EditText có chứa văn bản hay không
                isEditTextFilled = s.length() > 0;

                // Hiển thị hoặc ẩn nút đăng bài dựa trên trạng thái của EditText
                postButton.setVisibility(isEditTextFilled ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Bắt sự kiện khi người dùng nhấn nút chọn ảnh
        Button chooseImageButton = findViewById(R.id.chooseImageButton);
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở một Intent để chọn ảnh từ bộ nhớ hoặc máy ảnh của thiết bị
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Quay về màn hình trước đó khi người dùng nhấn nút quay về
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Lấy đường dẫn của ảnh từ Intent
            Uri uri = data.getData();

            // Hiển thị ảnh đã chọn trên giao diện người dùng
            imgSelectPost.setImageURI(uri);
        }
    }
}
