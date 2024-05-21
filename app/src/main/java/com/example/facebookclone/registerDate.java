package com.example.facebookclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class registerDate extends AppCompatActivity {

    private String ngaySinh; // Declare ngaySinh variable to store the selected date

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_date);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button nextButton = findViewById(R.id.button_conf_date);
        DatePicker datePicker = findViewById(R.id.DatePickerView_date); // Get reference to DatePicker

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ho = getIntent().getStringExtra("HO");
                String ten = getIntent().getStringExtra("TEN");

                // Extract selected date from DatePicker
                int year = datePicker.getYear();
                int month = datePicker.getMonth() + 1; // Month is zero-based
                int day = datePicker.getDayOfMonth();

                // Format the date
                ngaySinh = String.format("%02d/%02d/%d", day, month, year);

                Intent intent = new Intent(registerDate.this, registerUser.class);
                intent.putExtra("HO", ho);
                intent.putExtra("TEN", ten);
                intent.putExtra("NGAY_SINH", ngaySinh);
                startActivity(intent);
            }
        });
    }
}
