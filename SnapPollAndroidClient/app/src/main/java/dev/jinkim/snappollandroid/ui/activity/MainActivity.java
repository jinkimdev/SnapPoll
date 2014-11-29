package dev.jinkim.snappollandroid.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.ui.NavigationDrawerFragment;
import dev.jinkim.snappollandroid.ui.fragment.CreatePollFragment;
import dev.jinkim.snappollandroid.ui.fragment.RespondFragment;


public class MainActivity extends Activity
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
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        // update the main content by replacing fragments
        FragmentManager fm = getFragmentManager();
        fm.findFragmentByTag("");

        switch (position) {
            case 0:
                Fragment createFrag = fm.findFragmentByTag(CreatePollFragment.TAG);
                if (createFrag == null) {
                    createFrag = new CreatePollFragment();
                }
                fm.beginTransaction().replace(R.id.container, createFrag, CreatePollFragment.TAG).commit();
                break;

            case 1:
                Fragment respondFrag = fm.findFragmentByTag(RespondFragment.TAG);
                if (respondFrag == null) {
                    respondFrag = new RespondFragment();
                }
                fm.beginTransaction().replace(R.id.container, respondFrag, RespondFragment.TAG).commit();
                break;

            case 2:
                Intent in = new Intent(this, LoginActivity.class);
                startActivity(in);
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
            case 1:
                mTitle = getString(R.string.title_create_poll);
                break;
            case 2:
                mTitle = getString(R.string.title_respond);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
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
            getMenuInflater().inflate(R.menu.main, menu);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
    }

}