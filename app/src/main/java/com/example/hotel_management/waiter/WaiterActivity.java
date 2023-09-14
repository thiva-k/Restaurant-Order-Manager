package com.example.hotel_management.waiter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.hotel_management.R;
import com.google.android.material.tabs.TabLayout;

public class WaiterActivity extends AppCompatActivity {
    TabLayout waiterTaskTabLayout;
    ViewPager2 waiterTaskViewPager;
    WaiterTaskAdapter waiterTaskAdapter;
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
    }

}