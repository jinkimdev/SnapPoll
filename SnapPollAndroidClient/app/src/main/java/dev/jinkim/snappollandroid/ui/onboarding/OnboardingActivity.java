package dev.jinkim.snappollandroid.ui.onboarding;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.ui.activity.SnapPollBaseActivity;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Jin on 4/24/15.
 */
public class OnboardingActivity extends SnapPollBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding);

        // DEFAULT
        ViewPager defaultViewpager = (ViewPager) findViewById(R.id.view_pager_onboarding);
        CircleIndicator defaultIndicator = (CircleIndicator) findViewById(R.id.indicator_default);
        OnboardingPagerAdapter defaultPagerAdapter = new OnboardingPagerAdapter(getSupportFragmentManager());
        defaultViewpager.setAdapter(defaultPagerAdapter);
        defaultIndicator.setViewPager(defaultViewpager);

    }
}
