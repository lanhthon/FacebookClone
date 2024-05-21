package com.example.facebookclone;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class editprofile extends AppCompatActivity {

    private static final int PICK_IMAGE_AVATAR = 1;
    private static final int PICK_IMAGE_COVER = 2;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private StorageReference mStorageRef;

    private EditText fullNameField, dateOfBirthField, hometownField, schoolField;
    private ImageView backgroundImage, avatarImage;
    private Button saveButton;

    private Uri avatarUri, coverUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editprofile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Database, Auth, and Storage
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Bind views
        fullNameField = findViewById(R.id.fullName);
        dateOfBirthField = findViewById(R.id.dateOfBirth);
        hometownField = findViewById(R.id.hometown);
        schoolField = findViewById(R.id.school);
        backgroundImage = findViewById(R.id.backgroundImage);
        avatarImage = findViewById(R.id.avatarImage);
        saveButton = findViewById(R.id.saveButton);

        // Set click listeners to choose images
        avatarImage.setOnClickListener(v -> pickImage(PICK_IMAGE_AVATAR));
        backgroundImage.setOnClickListener(v -> pickImage(PICK_IMAGE_COVER));

        loadUserProfile();

        saveButton.setOnClickListener(view -> saveUserProfile());
    }

    private void pickImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            if (requestCode == PICK_IMAGE_AVATAR) {
                avatarUri = imageUri;
                avatarImage.setImageURI(avatarUri);
            } else if (requestCode == PICK_IMAGE_COVER) {
                coverUri = imageUri;
                backgroundImage.setImageURI(coverUri);
            }
        }
    }

    private void loadUserProfile() {
        mDatabase.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String fullName = firstName + " " + lastName;
                    String dateOfBirth = snapshot.child("birthDate").getValue(String.class);
                    String hometown = snapshot.child("hometown").getValue(String.class);
                    String school = snapshot.child("shool").getValue(String.class);
                    String avatarSrc = snapshot.child("avatarsrc").getValue(String.class);
                    String coverSrc = snapshot.child("coversrc").getValue(String.class);

                    fullNameField.setText(fullName);
                    dateOfBirthField.setText(dateOfBirth);
                    hometownField.setText(hometown);
                    schoolField.setText(school);

                    Glide.with(editprofile.this).load(avatarSrc).into(avatarImage);
                    Glide.with(editprofile.this).load(coverSrc).into(backgroundImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(editprofile.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean isAvatarUploadFinished = false;
    private boolean isCoverUploadFinished = false;

    private void saveUserProfile() {
        String fullName = fullNameField.getText().toString().trim();
        String dateOfBirth = dateOfBirthField.getText().toString().trim();
        String hometown = hometownField.getText().toString().trim();
        String school = schoolField.getText().toString().trim();

        if (fullName.isEmpty() || dateOfBirth.isEmpty() || hometown.isEmpty() || school.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] nameParts = fullName.split(" ");
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        for (int i = 2; i < nameParts.length; i++) {
            lastName += " " + nameParts[i];
        }

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("firstName", firstName);
        userMap.put("lastName", lastName);
        userMap.put("birthDate", dateOfBirth);
        userMap.put("hometown", hometown);
        userMap.put("shool", school);

        mDatabase.child("users").child(currentUser.getUid()).updateChildren(userMap)
                .addOnCompleteListener(task -> {
                    Toast.makeText(editprofile.this, "Đang cập nhật thông tin", Toast.LENGTH_SHORT).show();
                    if (task.isSuccessful()) {
                        if (avatarUri != null) {
                            isAvatarUploadFinished = false;
                            uploadImageToStorage("avatars/" + currentUser.getUid(), avatarUri, "avatarsrc");
                        } else {
                            isAvatarUploadFinished = true;
                        }

                        if (coverUri != null) {
                            isCoverUploadFinished = false;
                            uploadImageToStorage("covers/" + currentUser.getUid(), coverUri, "coversrc");
                        } else {
                            isCoverUploadFinished = true;
                        }

                        if (avatarUri == null && coverUri == null) {
                            finish();
                        }
                    } else {
                        Toast.makeText(editprofile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadImageToStorage(String path, Uri uri, String databaseField) {
        StorageReference imageRef = mStorageRef.child(path);
        imageRef.putFile(uri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                imageRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                    Map<String, Object> update = new HashMap<>();
                    update.put(databaseField, uri1.toString());
                    mDatabase.child("users").child(currentUser.getUid()).updateChildren(update)
                            .addOnCompleteListener(updateTask -> {
                                if (updateTask.isSuccessful()) {
                                    if (databaseField.equals("avatarsrc")) {
                                        isAvatarUploadFinished = true;
                                    } else if (databaseField.equals("coversrc")) {
                                        isCoverUploadFinished = true;
                                    }

                                    if (isAvatarUploadFinished && isCoverUploadFinished) {
                                        Toast.makeText(editprofile.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(editprofile.this, "Failed to update image URL", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
            } else {
                Toast.makeText(editprofile.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
