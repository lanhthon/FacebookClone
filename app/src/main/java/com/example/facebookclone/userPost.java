package com.example.facebookclone;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class userPost extends AppCompatActivity {

    private Button postButton;
    private ImageView imgSelectPost;
    private static final int PICK_IMAGE_REQUEST = 1;
    private HashMap<String, Object> postInfo;
    EditText inputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post);

        postButton = findViewById(R.id.postButton);
        postButton.setVisibility(View.GONE);
        inputEditText = findViewById(R.id.inputEditText);

        // Thêm TextWatcher cho EditText
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Kiểm tra nếu có dữ liệu nhập vào EditText thì hiển thị nút "Đăng bài"
                if (!inputEditText.getText().toString().isEmpty() || imgSelectPost.getDrawable() != null) {
                    postButton.setVisibility(View.VISIBLE);
                } else {
                    postButton.setVisibility(View.GONE);
                }
            }
        });

        imgSelectPost = findViewById(R.id.imgSelectPost);

        postInfo = new HashMap<>();

        Button chooseImageButton = findViewById(R.id.chooseImageButton);
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            if (requestCode == PICK_IMAGE_REQUEST || resultCode == RESULT_OK && data != null && data.getData() != null) {
                // Hiển thị nút "Đăng bài"
                postButton.setVisibility(View.VISIBLE);
                Uri uri = data.getData();
                imgSelectPost.setImageURI(uri);
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("user_posts").child(userId).child("post_" + System.currentTimeMillis() + ".jpg");

                storageRef.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri downloadUri) {
                                        String imageUrl = downloadUri.toString();
                                        String textValue = inputEditText.getText().toString();
                                        postInfo.put("image_url", imageUrl);
                                        postInfo.put("userId", userId);
                                        postInfo.put("time", ServerValue.TIMESTAMP);
                                        postInfo.put("like", 0);
                                        postInfo.put("comment", 0);
                                        postInfo.put("content", textValue);
                                        postInfo.put("share", 0);

                                        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("posts").push();
                                        postRef.setValue(postInfo);

                                        Toast.makeText(userPost.this, "Ảnh đã được đăng thành công", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(userPost.this, "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
}
