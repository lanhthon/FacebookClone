package com.example.facebookclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class registerDate extends AppCompatActivity {

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
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ho = getIntent().getStringExtra("HO");
                String ten = getIntent().getStringExtra("TEN");

                // Lấy ngày từ CalendarView
                CalendarView calendarView = findViewById(R.id.calendarView_date);
                long selectedDateInMillis = calendarView.getDate();
                Date selectedDate = new Date(selectedDateInMillis);

                // Chuyển đổi ngày thành định dạng mong muốn
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String ngaySinh = sdf.format(selectedDate);

                Intent intent = new Intent(registerDate.this, registerUser.class);
                intent.putExtra("HO", ho);
                intent.putExtra("TEN", ten);
                intent.putExtra("NGAY_SINH", ngaySinh);
                startActivity(intent);
            }
        });

    }
}