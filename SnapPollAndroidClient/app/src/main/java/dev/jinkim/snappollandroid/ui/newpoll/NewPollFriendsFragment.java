package dev.jinkim.snappollandroid.ui.newpoll;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dev.jinkim.snappollandroid.R;

/**
 * Created by Jin on 3/6/15.
 */
public class NewPollFriendsFragment extends Fragment {

    public static String TAG = "NewPollFriendsFragment";
    private NewPollActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag_new_poll_friends, container, false);

        mActivity = (NewPollActivity) getActivity();

        mActivity.getSupportActionBar().setTitle("Invite to Poll");

        Log.d(TAG, "In NewPollFriendsFragment");

        return rootView;
    }
}