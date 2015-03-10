package dev.jinkim.snappollandroid.ui.newpoll;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.facebook.Session;

import java.util.ArrayList;
import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.FacebookFriendListItem;
import dev.jinkim.snappollandroid.ui.adapter.FacebookFriendListAdapter;

/**
 * Created by Jin on 3/6/15.
 */
public class NewPollFriendsFragment extends Fragment {

    public static String TAG = "NewPollFriendsFragment";

    private ListView listView;
    private List<FacebookFriendListItem> listFriends;

    private NewPollActivity mActivity;

    private FacebookFriendListAdapter adapter;
    private static final int REAUTH_ACTIVITY_CODE = 100;
    private static final int FRIEND_PICKER_REQUEST_CODE = 2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_new_poll_friends, container, false);
        setHasOptionsMenu(true);
        mActivity = (NewPollActivity) getActivity();

        listView = (ListView) rootView.findViewById(R.id.lv_friend_selection);
        listFriends = new ArrayList<FacebookFriendListItem>();
        listFriends.add(new FacebookFriendListItem(null, null, 0, 0));

        listView.setAdapter(new FacebookFriendListAdapter(getActivity(), R.id.lv_friend_selection, listFriends));

        Session session = Session.getActiveSession();

        startPickerActivity(FacebookFriendPickerActivity.FRIEND_PICKER, FRIEND_PICKER_REQUEST_CODE);

        return rootView;
    }

    private void startPickerActivity(Uri data, int requestCode) {
        Intent intent = new Intent();
        intent.setData(data);
        intent.setClass(getActivity(), FacebookFriendPickerActivity.class);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REAUTH_ACTIVITY_CODE) {
//            uiHelper.onActivityResult(requestCode, resultCode, data);
            Log.d(TAG, "Reauth activity in FriendsFrag");
        } else if (resultCode == Activity.RESULT_OK) {
            // Do nothing for now
            Log.d(TAG, "Result OK in FriendsFrag");
        }
    }
}
