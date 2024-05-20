package com.example.facebookclone;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class tableLayout extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private DatabaseReference notificationRef;
    private ValueEventListener notificationListener;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_table_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager2 = findViewById(R.id.viewparent);
        tabLayout = findViewById(R.id.tablayout);

        viewPager2.setAdapter(new FragmentStateAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setIcon(R.drawable.outline_home_24);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.outline_escalator_warning_24);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.baseline_ondemand_video_24);
                        break;
                    case 3:
                        tab.setIcon(R.drawable.baseline_notifications_none_24);
                        break;
                    case 4:
                        tab.setIcon(R.drawable.baseline_density_medium_24);
                        break;
                }
            }
        }).attach();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            setupNotificationBadge();
        }
    }

    private void setupNotificationBadge() {
        notificationRef = FirebaseDatabase.getInstance().getReference().child("friend_requests/received_requests").child(userId);
        notificationListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean hasNotifications = false;
                for (DataSnapshot notificationSnapshot : dataSnapshot.getChildren()) {
                    if (notificationSnapshot.exists()) {
                        hasNotifications = true;
                        break;
                    }
                }
                TabLayout.Tab notificationTab = tabLayout.getTabAt(3); // Assuming notifications are at position 3
                if (notificationTab != null) {
                    if (hasNotifications) {
                        BadgeDrawable badgeDrawable = notificationTab.getOrCreateBadge();
                        badgeDrawable.setVisible(true);
                    } else {
                        notificationTab.removeBadge();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        };
        notificationRef.addValueEventListener(notificationListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (notificationRef != null && notificationListener != null) {
            notificationRef.removeEventListener(notificationListener);
        }
    }
}
