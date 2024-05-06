package com.example.facebookclone;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class tableLayout extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
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

        // Setup the adapter for ViewPager2

        viewPager2.setAdapter(new FragmentStateAdapter(this));

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setIcon(R.drawable.outline_add_home_24);
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
    }

}