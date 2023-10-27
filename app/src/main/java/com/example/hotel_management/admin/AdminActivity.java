package com.example.hotel_management.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import com.example.hotel_management.ProfileActivity;
import com.example.hotel_management.R;
import com.google.android.material.tabs.TabLayout;

public class AdminActivity extends AppCompatActivity {
    TabLayout adminTaskTabLayout;
    ViewPager2 adminTaskViewPager;
    AdminTaskAdapter adminTaskAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        adminTaskTabLayout = findViewById(R.id.tabLayout);
        adminTaskViewPager = findViewById(R.id.adminViewPager);
        adminTaskAdapter = new AdminTaskAdapter(this);
        adminTaskViewPager.setAdapter(adminTaskAdapter);
        adminTaskTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab waiterTaskTab) {
                adminTaskViewPager.setCurrentItem(waiterTaskTab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab waiterTaskTab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab waiterTaskTab) {

            }
        });
        adminTaskViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                adminTaskTabLayout.selectTab(adminTaskTabLayout.getTabAt(position));
            }
        });

        //setup the profile button
        findViewById(R.id.profileButton).setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

    }
}
