package com.example.facebookclone;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class registerUser extends AppCompatActivity {

    private EditText editTextHo;
    private EditText editTextTen;
    private EditText editTextNgaySinh;
    private EditText editTextEmailOrPhone;
    private EditText editTextPassword;
    private Button buttonConfUser;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        // Khởi tạo FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

      
        editTextEmailOrPhone = findViewById(R.id.editText_register_user);
        editTextPassword = findViewById(R.id.editText_register_password);
        buttonConfUser = findViewById(R.id.button_conf_user);

        buttonConfUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String ho = getIntent().getStringExtra("HO");
        String ten = getIntent().getStringExtra("TEN");
        String ngaySinh = getIntent().getStringExtra("NGAY_SINH");
        String emailOrPhone = editTextEmailOrPhone.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Kiểm tra xem các trường có được điền đầy đủ không
        if (TextUtils.isEmpty(ho)) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập họ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(ten)) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(ngaySinh)) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập ngày sinh", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(emailOrPhone)) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập email hoặc số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Đăng ký người dùng mới
        mAuth.createUserWithEmailAndPassword(emailOrPhone, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng ký thành công, làm bất cứ điều gì bạn muốn ở đây, chẳng hạn như chuyển hướng người dùng đến một màn hình khác
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(getApplicationContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Đăng ký thất bại, hiển thị thông báo lỗi
                        Toast.makeText(getApplicationContext(), "Đăng ký thất bại! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
