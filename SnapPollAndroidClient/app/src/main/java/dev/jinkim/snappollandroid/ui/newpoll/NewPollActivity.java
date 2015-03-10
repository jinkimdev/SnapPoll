package dev.jinkim.snappollandroid.ui.newpoll;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.squareup.otto.Bus;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.event.BusProvider;
import dev.jinkim.snappollandroid.ui.activity.SnapPollBaseActivity;

/**
 * Created by Jin on 1/11/15.
 */
public class NewPollActivity extends SnapPollBaseActivity {

    public static String TAG = "NewPollActivity";
    private Bus bus;
    private NewPollController controller;

    /* FACEBOOK */
    private static final String USER_SKIPPED_LOGIN_KEY = "user_skipped_login";
    private boolean isResumed = false;
    private boolean userSkippedLogin = false;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
//            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_poll);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        controller = new NewPollController();

        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle("New Poll");
////
//
        actionBar.setLogo(R.drawable.ic);
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setHomeButtonEnabled(true);

////        actionBar.setDisplayOptions(ActionBar.DISPLAY_USE_LOGO);
//        actionBar.setDisplayUseLogoEnabled(true);
//
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_USE_LOGO);
//        actionBar.setIcon(R.drawable.ic);

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
        uiHelper.onDestroy();
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

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.new_poll_fragment_container);

        switch (item.getItemId()) {
            // Respond to the action bar's Up button
            case android.R.id.home:
                if (f instanceof NewPollImageFragment) {
                    // if we are on ImageFrag, up button will take you back to the MainActivity
                    NavUtils.navigateUpFromSameTask(this);
                } else {
                    /* Clicking up button will bring you to the previous frag -- need to user test */
                    this.onBackPressed();
                }
                return true;

            case R.id.action_new_poll_next:

                if (f instanceof NewPollImageFragment) {
                    if (controller.getUriSelectedImg() == null) {
                        displaySnackBar("Image reference is not selected");
                    } else {
                        Log.d(TAG, "Navigate from Image -> Detail");
                        navigateToNewPollDetail();
                    }

                } else if (f instanceof NewPollDetailFragment) {
                    if (((NewPollDetailFragment) f).saveNewPollDetails()) {
                        Log.d(TAG, "Navigate from Detail -> Friends");
                        navigateToNewPollFriends();
                    } else {
                        displaySnackBar("Poll question is empty");
                    }

                } else {
                    // FriendsFragment
                    Log.d(TAG, "Showing NewPollFriendsFragment");

                }


                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Bus getEventBus() {
        return bus;
    }

    public NewPollController getController() {
        return controller;
    }

    public void navigateToNewPollDetail() {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        NewPollDetailFragment frag =
                (NewPollDetailFragment) fm.findFragmentByTag(NewPollDetailFragment.TAG);

        if (frag == null) {
            frag = new NewPollDetailFragment();
        }

        ft.addToBackStack(NewPollImageFragment.TAG);
        ft.setCustomAnimations(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left, R.anim.anim_enter_from_left, R.anim.anim_exit_to_right);
        ft.replace(R.id.new_poll_fragment_container, frag, NewPollDetailFragment.TAG);
        ft.commit();
    }

    public void navigateToNewPollFriends() {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        NewPollFriendsFragment frag =
                (NewPollFriendsFragment) fm.findFragmentByTag(NewPollFriendsFragment.TAG);

        if (frag == null) {
            frag = new NewPollFriendsFragment();
        }

        ft.addToBackStack(NewPollFriendsFragment.TAG);
        ft.setCustomAnimations(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left, R.anim.anim_enter_from_left, R.anim.anim_exit_to_right);
//        ft.setCustomAnimations(R.anim.anim_enter_from_right, R.anim.anim_empty);

        ft.replace(R.id.new_poll_fragment_container, frag, NewPollFriendsFragment.TAG);
        ft.commit();
    }


    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        isResumed = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        isResumed = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);

        outState.putBoolean(USER_SKIPPED_LOGIN_KEY, userSkippedLogin);
    }


//    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
//        if (isResumed) {
//            FragmentManager manager = getSupportFragmentManager();
//            int backStackSize = manager.getBackStackEntryCount();
//            for (int i = 0; i < backStackSize; i++) {
//                manager.popBackStack();
//        }
//        // check for the OPENED state instead of session.isOpened() since for the
//        // OPENED_TOKEN_UPDATED state, the selection fragment should already be showing.
//        if (state.equals(SessionState.OPENED)) {
//            showFragment(SELECTION, false);
//        } else if (state.isClosed()) {
//            showFragment(SPLASH, false);
//        }
//        }
//    }


}