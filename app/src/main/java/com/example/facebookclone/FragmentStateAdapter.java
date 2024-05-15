package com.example.facebookclone;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.example.facebookclone.fragmentPager.friendFragment;
import com.example.facebookclone.fragmentPager.homeFragment;
import com.example.facebookclone.fragmentPager.menuFragment;
import com.example.facebookclone.fragmentPager.notificationFragment;
import com.example.facebookclone.fragmentPager.reelsFragment;

public class FragmentStateAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
    public FragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new homeFragment();
            case 1:
                return new friendFragment();
            case 2:
                return new reelsFragment();
            case 3:
                return new notificationFragment();
            case 4:
                return new menuFragment();
            default:
                return new homeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
