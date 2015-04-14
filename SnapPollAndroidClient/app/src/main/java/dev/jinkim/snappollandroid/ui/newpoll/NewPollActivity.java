package dev.jinkim.snappollandroid.ui.newpoll;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.EventListener;
import com.squareup.otto.Bus;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.event.BusProvider;
import dev.jinkim.snappollandroid.ui.activity.SnapPollBaseActivity;

/**
 * Created by Jin on 1/11/15.
 */
public class NewPollActivity extends SnapPollBaseActivity {

    public static String TAG = NewPollActivity.class.getSimpleName();
    private Bus bus;
    private NewPollController controller;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_poll);

        controller = new NewPollController(this);

        // set up toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setHomeButtonEnabled(true);

        if (findViewById(R.id.new_poll_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            NewPollSelectImageFragment newPollSelectImageFragment = new NewPollSelectImageFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            newPollSelectImageFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.new_poll_fragment_container, newPollSelectImageFragment, NewPollSelectImageFragment.TAG).commit();
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

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Determine which action menu to show (next or submit)
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.new_poll_fragment_container);
        if (f instanceof NewPollEnterDetailFragment) {
            MenuItem menuNext = menu.findItem(R.id.action_new_poll_next);
            menuNext.setVisible(false);
            MenuItem menuSubmit = menu.findItem(R.id.action_new_poll_submit);
            menuSubmit.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.new_poll_fragment_container);

        switch (item.getItemId()) {
            // Respond to the action bar's Up button
            case android.R.id.home:
                if (f instanceof NewPollSelectImageFragment) {
                    // if we are on ImageFrag, up button will take you back to the MainActivity
                    NavUtils.navigateUpFromSameTask(this);
                } else {
                    /* Clicking up button will bring you to the previous frag -- need to user test */
                    this.onBackPressed();
                }
                return true;

            case R.id.action_new_poll_next:

                if (f instanceof NewPollSelectImageFragment) {
                    if (controller.getUriSelectedImg() == null) {
                        displaySnackBar(R.string.msg_image_not_selected);
                    } else {
                        Log.d(TAG, "Navigate from Image -> Detail");
                        navigateToNewPollEnterDetail();
                    }
                } else {
                    // FriendsFragment
                    Log.d(TAG, "Showing NewPollFriendsFragment");
                }
                return true;

            case R.id.action_new_poll_submit:
                // TODO: SUBMIT LOGIC
//                controller.uploadImage();
                if (f instanceof NewPollEnterDetailFragment) {
                    if (((NewPollEnterDetailFragment) f).saveNewPollDetails()) {
                        controller.uploadImage();
                    } else {
                        Toast.makeText(this, R.string.msg_poll_question_empty, Toast.LENGTH_SHORT).show();
                    }
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

    public void navigateToNewPollEnterDetail() {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        NewPollEnterDetailFragment frag =
                (NewPollEnterDetailFragment) fm.findFragmentByTag(NewPollEnterDetailFragment.TAG);

        if (frag == null) {
            frag = new NewPollEnterDetailFragment();
        }

        ft.addToBackStack(NewPollSelectImageFragment.TAG);
        ft.setCustomAnimations(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left, R.anim.anim_enter_from_left, R.anim.anim_exit_to_right);
        ft.replace(R.id.new_poll_fragment_container, frag, NewPollEnterDetailFragment.TAG);
        ft.commit();
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void displaySnackBar(String msg) {
        Snackbar.with(this)
                .text(msg)
                .colorResource(R.color.text_primary)
                .eventListener(new EventListener() {
                                   Fragment f = getSupportFragmentManager().findFragmentById(R.id.new_poll_fragment_container);

                                   @Override
                                   public void onShow(Snackbar snackbar) {

                                       if (f instanceof NewPollSelectImageFragment) {
                                           ((NewPollSelectImageFragment) f).moveFloatButton(-snackbar.getHeight());
                                       }
                                   }

                                   @Override
                                   public void onShowByReplace(Snackbar snackbar) {

                                   }

                                   @Override
                                   public void onShown(Snackbar snackbar) {

                                   }

                                   @Override
                                   public void onDismiss(Snackbar snackbar) {
                                       Fragment f = getSupportFragmentManager().findFragmentById(R.id.new_poll_fragment_container);
                                       if (f instanceof NewPollSelectImageFragment) {
                                           ((NewPollSelectImageFragment) f).moveFloatButton(0);
                                       }
                                   }

                                   @Override
                                   public void onDismissByReplace(Snackbar snackbar) {

                                   }

                                   @Override
                                   public void onDismissed(Snackbar snackbar) {

                                   }
                               }

                )
                .show(this);
    }
}