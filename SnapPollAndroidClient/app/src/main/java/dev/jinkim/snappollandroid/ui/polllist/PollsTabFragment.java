package dev.jinkim.snappollandroid.ui.polllist;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.ui.activity.MainActivity;
import dev.jinkim.snappollandroid.ui.newpoll.NewPollActivity;
import dev.jinkim.snappollandroid.ui.widget.slidingtab.PollsViewPagerAdapter;
import dev.jinkim.snappollandroid.ui.widget.slidingtab.SlidingTabLayout;
import dev.jinkim.snappollandroid.web.SnapPollRestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jin on 11/27/14.
 *
 * Host two tabs with view pager in poll lists
 */
public class PollsTabFragment extends Fragment {

    public static final String TAG = PollsTabFragment.class.getSimpleName();
    private MainActivity mActivity;

    private ViewPager viewPager;
    private PollsViewPagerAdapter pollsViewPagerAdapter;
    private SlidingTabLayout slidingTabs;
    private CharSequence titles[];
    private int numTabs = 2;

//    InvitedPollListAdapter adapter;

    public PollsTabFragment() {
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        setHasOptionsMenu(true);

        mActivity.setToolbarTitle(R.string.app_name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_polls_tab_main, container, false);

        titles = new String[]{getString(R.string.tab_title_invited), getString(R.string.tab_title_my_polls)};

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        pollsViewPagerAdapter = new PollsViewPagerAdapter(getChildFragmentManager(), titles, numTabs);

        // Assigning ViewPager View and setting the adapter
        viewPager = (ViewPager) rootView.findViewById(R.id.poll_list_viewpager);
        viewPager.setAdapter(pollsViewPagerAdapter);

        // Assiging the Sliding Tab Layout View
        slidingTabs = (SlidingTabLayout) rootView.findViewById(R.id.slidingtabs);
        slidingTabs.setDistributeEvenly(false); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        slidingTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tab_scroll_color);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        slidingTabs.setViewPager(viewPager);

        setHasOptionsMenu(true);

        return rootView;
    }

    private void retrievePolls() {
        SnapPollRestClient rest = new SnapPollRestClient();
        rest.getApiService().getPolls(new Callback<List<Poll>>() {
            @Override
            public void success(List<Poll> polls, Response response) {
                Log.d(TAG, "GET /poll success.");
                updateList(polls);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "GET /poll failed.");

            }
        });

    }

    private void updateList(List<Poll> polls) {
//        adapter.clear();
//        adapter.addAll(polls);
//        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_new_poll) {
            Intent in = new Intent(getActivity(), NewPollActivity.class);
            startActivity(in);
            return true;
        }

        if (id == R.id.action_refresh_list) {
            mActivity.showProgressBar(R.string.msg_loading);

            Fragment f = mActivity.getCurrentFragment();
            if (f == null) {
                Toast.makeText(mActivity, R.string.msg_current_fragment_invalid, Toast.LENGTH_SHORT).show();
            }

            if (f instanceof InvitedPollsFragment) {
                ((InvitedPollsFragment) f).retrievePolls();
            } else if (f instanceof MyPollsFragment) {
                ((MyPollsFragment) f).retrieveMyPolls();
            }

            Toast.makeText(mActivity, R.string.msg_refreshing_list, Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}
