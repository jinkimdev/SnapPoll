package dev.jinkim.snappollandroid.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import com.facebook.Session;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.squareup.otto.Subscribe;

import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.event.ResponseSubmittedEvent;
import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.session.SessionManager;
import dev.jinkim.snappollandroid.ui.NavigationDrawerFragment;
import dev.jinkim.snappollandroid.ui.fragment.MyPollsFragment;
import dev.jinkim.snappollandroid.ui.fragment.PollsTabFragment;
import dev.jinkim.snappollandroid.ui.fragment.ProfileFragment;
import dev.jinkim.snappollandroid.ui.widget.slidingtab.SlidingTabLayout;
import dev.jinkim.snappollandroid.ui.widget.slidingtab.ViewPagerAdapter;


public class MainActivity extends SnapPollBaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar toolbar;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private SessionManager appSession;

    private Fragment currentFragment;

    private List<Poll> invitedPolls;
    private List<Poll> myPolls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        bus.register(this);

        appSession = new SessionManager(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        // update the main content by replacing fragments
        FragmentManager fm = getSupportFragmentManager();
        fm.findFragmentByTag("");

        switch (position) {

            case 0:
                Fragment pollsTabFrag = fm.findFragmentByTag(PollsTabFragment.TAG);
                if (pollsTabFrag == null) {
                    pollsTabFrag = new PollsTabFragment();
                }
                fm.beginTransaction().replace(R.id.container, pollsTabFrag, PollsTabFragment.TAG).commit();
                break;

            case 1:
                Fragment profileFrag = fm.findFragmentByTag(MyPollsFragment.TAG);
                if (profileFrag == null) {
                    profileFrag = new ProfileFragment();
                }
                fm.beginTransaction().replace(R.id.container, profileFrag, ProfileFragment.TAG).commit();
                break;

            default:
                Fragment defaultFrag = fm.findFragmentByTag(PollsTabFragment.TAG);
                if (defaultFrag == null) {
                    defaultFrag = new PollsTabFragment();
                }
                fm.beginTransaction().replace(R.id.container, defaultFrag, PollsTabFragment.TAG).commit();
                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.nav_drawer_title_polls);
                break;
            case 1:
                mTitle = getString(R.string.nav_drawer_title_profile);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
//        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_primary)));
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

            //TODO: use validateLogin
            if (session.logoutUser()) {
                this.finish();
            }
        }
    }

    /**
     * Logout From Facebook
     */
    public void signOutFromFacebook() {
        Session session = Session.getActiveSession();
        if (session != null) {

            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
                SessionManager appSession = new SessionManager(this);

                //TODO: use validateLogin
                if (appSession.logoutUser()) {
                    this.finish();
                }
            }
        } else {

            session = new Session(this);
            Session.setActiveSession(session);

            session.closeAndClearTokenInformation();
            SessionManager appSession = new SessionManager(this);

            //TODO: use validateLogin
            if (appSession.logoutUser()) {
                this.finish();
            }
        }
    }

    @Subscribe
    public void onResponseSubmittedEvent(ResponseSubmittedEvent event) {
        Log.d(TAG, "Received response submitted event!");
        displaySnackBar(R.string.msg_response_submitted);
    }

    public SessionManager getAppSession() {
        return appSession;
    }

    public List<Poll> getInvitedPolls() {
        return invitedPolls;
    }

    public void setInvitedPolls(List<Poll> invitedPolls) {
        this.invitedPolls = invitedPolls;
    }

    public List<Poll> getMyPolls() {
        return myPolls;
    }

    public void setMyPolls(List<Poll> myPolls) {
        this.myPolls = myPolls;
    }

    public void setCurrentFragment(Fragment fragment) {
        this.currentFragment = fragment;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }
}