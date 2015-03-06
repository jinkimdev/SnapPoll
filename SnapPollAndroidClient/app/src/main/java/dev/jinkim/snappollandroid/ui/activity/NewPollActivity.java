package dev.jinkim.snappollandroid.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.squareup.otto.Bus;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.event.BusProvider;
import dev.jinkim.snappollandroid.ui.fragment.NewPollDetailFragment;
import dev.jinkim.snappollandroid.ui.fragment.NewPollImageFragment;

/**
 * Created by Jin on 1/11/15.
 */
public class NewPollActivity extends ActionBarActivity {

    public static String TAG = "NewPollActivity";
    private Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.new_poll_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            NewPollImageFragment newPollImageFragment = new NewPollImageFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            newPollImageFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.new_poll_fragment_container, newPollImageFragment, NewPollImageFragment.TAG).commit();
        }

        bus = BusProvider.getInstance();
        bus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bus != null) bus.unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_poll, menu);

        // TODO: Determine which action button to show (next or submit)

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_new_poll_next:

                Log.d(TAG, "NEXT CLICKED");

                moveToNewPollDetail();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Bus getEventBus() {
        return bus;
    }

    public void moveToNewPollDetail() {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        NewPollDetailFragment frag =
                (NewPollDetailFragment) fm.findFragmentByTag(NewPollDetailFragment.TAG);

        if (frag == null) {
            frag = new NewPollDetailFragment();
        }

        ft.addToBackStack(NewPollImageFragment.TAG);
        ft.setCustomAnimations(R.anim.anim_enter_from_right, R.anim.anim_exit_to_right,R.anim.anim_enter_from_right, R.anim.anim_exit_to_right);
        ft.replace(R.id.new_poll_fragment_container, frag, NewPollDetailFragment.TAG);
        ft.commit();
    }

}