package dev.jinkim.snappollandroid.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.ui.fragment.InviteFriendsFragment;

/**
 * Created by Jin on 3/31/15.
 */
public class InviteFriendsActivity extends SnapPollBaseActivity {

    public static String TAG = InviteFriendsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);

        int pollId = getIntent().getIntExtra(getString(R.string.key_poll_id), -1);

        if (getIntent().getBooleanExtra(getString(R.string.key_show_poll_created_msg), false)) {
            // if flag is set, show poll created message here
            displaySnackBar(getString(R.string.msg_poll_created));
        }
        Log.d(TAG, "## pollId: " + String.valueOf(pollId));

        if (findViewById(R.id.invite_friends_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            InviteFriendsFragment inviteFriendsFragment = new InviteFriendsFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            inviteFriendsFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.invite_friends_fragment_container, inviteFriendsFragment, InviteFriendsFragment.TAG).commit();
        }
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_invite_friends, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        InviteFriendsFragment f = (InviteFriendsFragment) getSupportFragmentManager().findFragmentById(R.id.invite_friends_fragment_container);

        switch (item.getItemId()) {
            // Respond to the action bar's Up button
            case R.id.action_invite_friends_invite:
                f.inviteFriends();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
