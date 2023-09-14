package com.example.hotel_management.waiter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class WaiterTaskAdapter extends FragmentStateAdapter {

    public WaiterTaskAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0:
                return new OrderFragment();
            case 1:
                return new TableFragment();
            default:
                return new OrderFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
