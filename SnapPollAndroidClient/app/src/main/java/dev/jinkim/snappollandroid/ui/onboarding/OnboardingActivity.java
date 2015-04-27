package dev.jinkim.snappollandroid.ui.onboarding;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.ui.activity.SnapPollBaseActivity;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Jin on 4/24/15.
 *
 * Display onboarding tutorial screens for the first time user
 */
public class OnboardingActivity extends SnapPollBaseActivity {

    private OnboardingActivity mActivity;
    private Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding);

        mActivity = this;

        Button btnSkip = (Button) findViewById(R.id.btn_onboarding_skip);
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });

        btnDone = (Button) findViewById(R.id.btn_onboarding_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });

        // DEFAULT
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager_onboarding);
        CircleIndicator circleIndicator = (CircleIndicator) findViewById(R.id.indicator_default);
        final OnboardingPagerAdapter pagerAdapter = new OnboardingPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);
        circleIndicator.setViewPager(viewPager);

        circleIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position == pagerAdapter.getCount() - 1) {
                    btnDone.setVisibility(View.VISIBLE);
                } else {
                    btnDone.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
