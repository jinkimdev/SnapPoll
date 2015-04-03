package dev.jinkim.snappollandroid.ui.polldetail;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.RowFriend;
import dev.jinkim.snappollandroid.ui.activity.SnapPollBaseActivity;
import dev.jinkim.snappollandroid.ui.invite.InviteFriendsController;
import dev.jinkim.snappollandroid.ui.invite.InviteFriendsDialog;

/**
 * Created by Jin on 1/11/15.
 */
public class PollDetailActivity extends SnapPollBaseActivity {

    public static String TAG = PollDetailActivity.class.getSimpleName();
    private PollDetailActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_detail);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.poll_detail_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            PollDetailFragment detailFragment = new PollDetailFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            detailFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.poll_detail_fragment_container, detailFragment).commit();
        }
        bus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Bus getEventBus() {
        return bus;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

}