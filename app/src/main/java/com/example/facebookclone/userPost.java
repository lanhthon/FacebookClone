package com.example.facebookclone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class userPost extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgSelectPost, avatarImageView;
    private Button postButton;
    private EditText inputEditText;
    private TextView usernameTextView;
    private HashMap<String, Object> postInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post);

        postButton = findViewById(R.id.postButton);
        inputEditText = findViewById(R.id.inputEditText);
        imgSelectPost = findViewById(R.id.imgSelectPost);
        avatarImageView = findViewById(R.id.avatarImageView);
        usernameTextView = findViewById(R.id.usernameTextView);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loadUserInfo(currentUser.getUid());
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        postButton.setVisibility(View.GONE);
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updatePostButtonVisibility();
            }
        });

        Button chooseImageButton = findViewById(R.id.chooseImageButton);
        chooseImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        postInfo = new HashMap<>();
    }

    private void loadUserInfo(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String avatarSrc = dataSnapshot.child("avatarsrc").getValue(String.class);
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);

                    if (avatarSrc != null) {
                        Picasso.get().load(avatarSrc).into(avatarImageView);
                    }
                    usernameTextView.setText(String.format("%s %s", firstName, lastName));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(userPost.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePostButtonVisibility() {
        postButton.setVisibility(inputEditText.getText().toString().isEmpty() && imgSelectPost.getDrawable() == null ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            imgSelectPost.setImageURI(uri);
            uploadImageToFirebase(uri);
        }
    }

    private void uploadImageToFirebase(Uri uri) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "You need to log in to post.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("user_posts").child(userId).child("post_" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    storePostInfo(downloadUri.toString(), userId);
                    Toast.makeText(userPost.this, "Image uploaded successfully.", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(userPost.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("UploadError", "Failed to upload image", e);
                });
    }

    private void storePostInfo(String imageUrl, String userId) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("posts").push();
        String postId = postRef.getKey(); // Get unique postId

        String textValue = inputEditText.getText().toString();
        postInfo.put("postId", postId); // Add postId to the postInfo
        postInfo.put("image_url", imageUrl);
        postInfo.put("userId", userId);
        postInfo.put("time", ServerValue.TIMESTAMP);
        postInfo.put("content", textValue);
        postInfo.put("likesCount", 0); // Initial likes count

        // Initialize likes with an empty object
        HashMap<String, Object> initialLikes = new HashMap<>();
        initialLikes.put("userId", "");
        postInfo.put("likes", initialLikes);

        postRef.setValue(postInfo);
    }

}