package dev.jinkim.snappollandroid.ui.onboarding;

/**
 * Created by Jin on 4/24/15.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class OnboardingPagerAdapter extends FragmentStatePagerAdapter {

    private int pagerCount = 6;
    private FragmentManager fm;

    public OnboardingPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int index) {
        // update the main content by replacing fragments

        return OnboardingPageFragment.newInstance(index);
    }

    @Override
    public int getCount() {
        return pagerCount;
    }

}