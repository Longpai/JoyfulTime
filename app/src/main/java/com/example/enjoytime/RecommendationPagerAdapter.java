package com.example.enjoytime;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class RecommendationPagerAdapter extends FragmentStateAdapter {

    public RecommendationPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new SeriesRecommendationFragment();
        }
        return new MovieRecommendationFragment();
    }

    @Override
    public int getItemCount() {
        return 2; // We have two tabs: Movies and Series
    }
}
