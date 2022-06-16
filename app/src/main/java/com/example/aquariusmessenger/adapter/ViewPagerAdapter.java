package com.example.aquariusmessenger.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.aquariusmessenger.fragments.ChatFragment;
import com.example.aquariusmessenger.fragments.ProfileFragment;
import com.example.aquariusmessenger.fragments.UsersFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
       switch (position) {
           case 0:
               return new ChatFragment();
           case 1:
               return new UsersFragment();
           default:
               return new ProfileFragment();
       }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
