package dev.jinkim.snappollandroid.ui.newpoll;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.facebook.Session;

import dev.jinkim.snappollandroid.R;

/**
 * Created by Jin on 3/6/15.
 */
public class NewPollFriendsFragment extends Fragment {

    public static String TAG = "NewPollFriendsFragment";

    private ListView listView;

    private NewPollActivity mActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_new_poll_friends, container, false);
        setHasOptionsMenu(true);
        mActivity = (NewPollActivity) getActivity();

        listView = (ListView) rootView.findViewById(R.id.lv_friend_selection);

        return rootView;
    }

}
