package com.example.hotel_management.admin;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.hotel_management.waiter.OrderFragment;
import com.example.hotel_management.waiter.TableFragment;


public class AdminTaskAdapter extends FragmentStateAdapter {

    public AdminTaskAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0:
                return new EmployeeFragment();
            case 1:
                return new MenuFragment();
            default:
                return new OfferFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
