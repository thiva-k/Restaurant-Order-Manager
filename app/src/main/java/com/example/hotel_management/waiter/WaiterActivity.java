package com.example.hotel_management.waiter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import com.example.hotel_management.ProfileActivity;
import com.example.hotel_management.R;
import com.google.android.material.tabs.TabLayout;

public class WaiterActivity extends AppCompatActivity {
    private TabLayout waiterTaskTabLayout;
    private ViewPager2 waiterTaskViewPager;
    private WaiterTaskAdapter waiterTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter);
        waiterTaskTabLayout = findViewById(R.id.tabLayout);
        waiterTaskViewPager = findViewById(R.id.waiterViewPager);
        waiterTaskAdapter = new WaiterTaskAdapter(this);
        waiterTaskViewPager.setAdapter(waiterTaskAdapter);
        waiterTaskTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab waiterTaskTab) {
                waiterTaskViewPager.setCurrentItem(waiterTaskTab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab waiterTaskTab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab waiterTaskTab) {

            }
        });
        waiterTaskViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                waiterTaskTabLayout.selectTab(waiterTaskTabLayout.getTabAt(position));
            }
        });

        //setup the profile button
        findViewById(R.id.profileButton).setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
    }

}