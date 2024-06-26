package com.example.facebookclone;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class registerUser extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText emailField, passwordField, retypePasswordField;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Bind views
        emailField = findViewById(R.id.editText_register_user);
        passwordField = findViewById(R.id.editText_register_password);
        registerButton = findViewById(R.id.button_conf_user);
        retypePasswordField = findViewById(R.id.editText_retype_password);
        registerButton.setOnClickListener(view -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String retypePassword = retypePasswordField.getText().toString().trim();

            // Check if passwords match
            if (!password.equals(retypePassword)) {
                Toast.makeText(this, "Mật khẩu nhập lại không khớp.", Toast.LENGTH_SHORT).show();
                return;
            }

            createAccount(email, password);
        });
    }

    private void createAccount(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email và password không được để trống.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            updateUserData(user.getUid());
                        }
                        Toast.makeText(registerUser.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(registerUser.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateUserData(String userId) {
        // Get extra data from Intent
        String firstName = getIntent().getStringExtra("HO");
        String lastName = getIntent().getStringExtra("TEN");
        String birthDate = getIntent().getStringExtra("NGAY_SINH");

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("firstName", firstName);
        userMap.put("lastName", lastName);
        userMap.put("birthDate", birthDate);
        userMap.put("avatarsrc", "https://firebasestorage.googleapis.com/v0/b/fbnhom4-2d36c.appspot.com/o/users%2Fimg_avatar.png?alt=media&token=08464413-f374-4b96-ac92-6ff868808c22");
        userMap.put("coversrc", "https://firebasestorage.googleapis.com/v0/b/fbnhom4-2d36c.appspot.com/o/users%2Fcover.jpg?alt=media&token=850484b8-901d-4dd9-b629-ae76a7bbffbb");
        userMap.put("hometown", "Kiên Giang");
        userMap.put("shool", "Cao Đẳng Kiên Giang");


        // Write a message to the database
        mDatabase.child("users").child(userId).setValue(userMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(registerUser.this, "Đăng ký thành công!.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, tableLayout.class));
                        finish(); // Optional: finish current activity to prevent going back to login screen
                    } else {
                        Toast.makeText(registerUser.this, "Đăng ký thất bại!.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
