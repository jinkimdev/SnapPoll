package dev.jinkim.snappollandroid.ui.onboarding;

/**
 * Created by Jin on 4/24/15.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class OnboardingPagerAdapter extends FragmentPagerAdapter {

    private int pagerCount = 5;
    private FragmentManager fm;

    public OnboardingPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int index) {
        // update the main content by replacing fragments

        switch (index) {

            case 0:
                Fragment tutorialPage1 = fm.findFragmentByTag(TutorialOneFragment.TAG);
                if (tutorialPage1 == null) {
                    tutorialPage1 = new TutorialOneFragment();
                }

                return tutorialPage1;

            case 1:
                Fragment tutorialPage2 = fm.findFragmentByTag(TutorialTwoFragment.TAG);
                if (tutorialPage2 == null) {
                    tutorialPage2 = new TutorialTwoFragment();
                }
                return tutorialPage2;

            default:
                Fragment defaultFrag = fm.findFragmentByTag(TutorialOneFragment.TAG);
                if (defaultFrag == null) {
                    defaultFrag = new TutorialOneFragment();
                }
                return defaultFrag;
        }
    }

    @Override
    public int getCount() {
        return pagerCount;
    }

}