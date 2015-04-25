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
 */
public class OnboardingActivity extends SnapPollBaseActivity {

    private OnboardingActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding);

        mActivity = this;

        // DEFAULT
        ViewPager defaultViewpager = (ViewPager) findViewById(R.id.view_pager_onboarding);
        CircleIndicator defaultIndicator = (CircleIndicator) findViewById(R.id.indicator_default);
        OnboardingPagerAdapter defaultPagerAdapter = new OnboardingPagerAdapter(getSupportFragmentManager());
        defaultViewpager.setAdapter(defaultPagerAdapter);
        defaultIndicator.setViewPager(defaultViewpager);

        Button btnSkip = (Button) findViewById(R.id.btn_onboarding_skip);
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });

        Button btnDone = (Button) findViewById(R.id.btn_onboarding_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
    }
}
