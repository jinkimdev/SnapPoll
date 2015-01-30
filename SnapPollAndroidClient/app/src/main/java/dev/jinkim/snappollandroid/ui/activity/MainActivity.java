package dev.jinkim.snappollandroid.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.ui.NavigationDrawerFragment;
import dev.jinkim.snappollandroid.ui.fragment.CreatePollFragment;
import dev.jinkim.snappollandroid.ui.fragment.MyPollsFragment;
import dev.jinkim.snappollandroid.ui.fragment.PollsTabFragment;
import dev.jinkim.snappollandroid.ui.fragment.ProfileFragment;


public class MainActivity extends SnapPollBaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        // update the main content by replacing fragments
        FragmentManager fm = getSupportFragmentManager();
        fm.findFragmentByTag("");

        switch (position) {

            case 0:
                Fragment respondFrag = fm.findFragmentByTag(PollsTabFragment.TAG);
                if (respondFrag == null) {
                    respondFrag = new PollsTabFragment();
                }
                fm.beginTransaction().replace(R.id.container, respondFrag, PollsTabFragment.TAG).commit();
                break;

            case 1:
                Fragment profileFrag = fm.findFragmentByTag(MyPollsFragment.TAG);
                if (profileFrag == null) {
                    profileFrag = new ProfileFragment();
                }
                fm.beginTransaction().replace(R.id.container, profileFrag, ProfileFragment.TAG).commit();
                break;

            default:
                Fragment defaultFrag = fm.findFragmentByTag(CreatePollFragment.TAG);
                if (defaultFrag == null) {
                    defaultFrag = new CreatePollFragment();
                }
                fm.beginTransaction().replace(R.id.container, defaultFrag, CreatePollFragment.TAG).commit();
                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.title_respond);
                break;
            case 1:
                mTitle = getString(R.string.title_profile);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_new_poll) {
            Intent in = new Intent(this, CreatePollActivity.class);
            startActivity(in);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void revokeGoogleAccess() {
        revokeGplusAccess();
        session.validateLogin();
    }

    public void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
//            updateUI(false);
            session.validateLogin();
        }
    }
}