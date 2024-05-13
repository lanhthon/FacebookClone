package com.example.facebookclone;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class searchUser extends AppCompatActivity {
    private EditText searchEditText;
    private ListView friendListView;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchEditText = findViewById(R.id.searchEditText);
        Button searchButton = findViewById(R.id.searchButton);
        friendListView = findViewById(R.id.friendListView);

        userAdapter = new UserAdapter(this, userList);
        friendListView.setAdapter(userAdapter);
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = searchEditText.getText().toString();
                if (!searchQuery.isEmpty()) {
                    fetchUsers(searchQuery);
                }
            }
        });


    }

    private void fetchUsers(String searchQuery) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");

        // Xử lý chuỗi tìm kiếm để phân biệt firstName và lastName
        String[] parts = searchQuery.split(" ");
        Query query;
        if (parts.length == 2) {
            // Tìm kiếm với cả firstName và lastName
            String firstName = parts[0].trim();
            String lastName = parts[1].trim();
            query = database.orderByChild("firstName").equalTo(firstName);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("lastName").getValue(String.class).equals(lastName)) {
                            String userId = snapshot.getKey();
                            User user = new User(userId, firstName, lastName);
                            userList.add(user);
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("Database error: " + databaseError.getMessage());
                }
            });
        } else if (parts.length == 1) {
            // Tìm kiếm với firstName hoặc lastName
            String name = parts[0].trim();
            query = database.orderByChild("firstName").equalTo(name);
            processQuery(query, name);
        }
    }

    private void processQuery(Query query, String name) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean added = false;
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey();
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    if (firstName.equals(name) || lastName.equals(name)) {
                        User user = new User(userId, firstName, lastName);
                        userList.add(user);
                        added = true;
                    }
                }
                if (!added) { // Nếu không tìm thấy theo firstName, tìm theo lastName
                    findLastName(name);
                } else {
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Database error: " + databaseError.getMessage());
            }
        });
    }

    private void findLastName(String lastName) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");
        Query query = database.orderByChild("lastName").equalTo(lastName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey();
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    User user = new User(userId, firstName, lastName);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Database error: " + databaseError.getMessage());
            }
        });
    }
}