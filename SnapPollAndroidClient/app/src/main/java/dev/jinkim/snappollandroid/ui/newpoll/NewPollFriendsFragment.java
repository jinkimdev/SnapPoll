package dev.jinkim.snappollandroid.ui.newpoll;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.util.ArrayList;
import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.RowFriend;

/**
 * Created by Jin on 3/6/15.
 */
public class NewPollFriendsFragment extends Fragment {

    public static String TAG = "NewPollFriendsFragment";

    private ListView listView;

    private NewPollActivity mActivity;
    private NewPollController controller;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_new_poll_friends, container, false);
        setHasOptionsMenu(true);
        mActivity = (NewPollActivity) getActivity();
        controller = mActivity.getController();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initViews(view);
        retrieveFriends();
    }

    private void showChooseFriendsDialog(List<RowFriend> friends) {
        // set up custom view for dialog - color indicator, edit text
        LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View content = vi.inflate(R.layout.dialog_content_choose_friends, null);

        final ListView listView = (ListView) content.findViewById(R.id.friends_dialog_lv_friends);
        final SelectFriendListAdapter adapter = new SelectFriendListAdapter(mActivity, friends);

        // sparse boolean array to keep up with selected friend list
        final SparseBooleanArray selected = new SparseBooleanArray(friends.size());

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemsCanFocus(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItem(position).selected) {
                    adapter.getItem(position).selected = false;
                    selected.delete(position);
                } else {
                    adapter.getItem(position).selected = true;
                    selected.append(position, true);
                }
                Log.d(TAG, "Selected friend position: " + String.valueOf(position));
                adapter.notifyDataSetChanged();
            }
        });
        listView.setAdapter(adapter);

        // set up dialog
        String dialogTitle = "G+ Choose friends";

        boolean wrapInScrollView = false;
        MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                .title(dialogTitle)
                .customView(content, wrapInScrollView)
                .positiveText("Save")
//                .positiveText(R.string.agree)
                .negativeText("Cancel")
//                .negativeText(R.string.disagree)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        /* update selected friends */
                        Log.d(TAG, selected.toString());

                        // TODO: grab the keys (positions) that are true in value
                        List<RowFriend> selectedFriends = new ArrayList<RowFriend>();
                        for (int i = 0; i < selected.size(); i++) {
                            int position = selected.keyAt(i);
                            if (selected.valueAt(i)) {
                                selectedFriends.add(adapter.getItem(position));
                            }
                        }

                        updateSelectedFriendList(selectedFriends);
                        Log.d(TAG, "Selected friends: " + String.valueOf(selectedFriends.size()));
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
//                        showingDialog = false;
                    }
                })
                .show();
        dialog.show();

    }

    private void updateSelectedFriendList(List<RowFriend> selectedFriends) {
        //TODO: update list view

        // clear adapter
        // set adapter
    }

    private void initViews(View v) {
        listView = (ListView) v.findViewById(R.id.lv_friend_selection);


    }

    /**
     * If GoogleApiClient is connected, make a call to get friends list
     * <p/>
     * List of Google+ Person model
     * <p/>
     * https://developer.android.com/reference/com/google/android/gms/plus/model/people/Person.html
     */
    private void retrieveFriends() {

        GoogleApiClient mGoogleApiClient = mActivity.getGoogleApiClient();
        if (mGoogleApiClient.isConnected()) {

            Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
                    .setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
                        @Override
                        public void onResult(People.LoadPeopleResult loadPeopleResult) {
                            if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
                                PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
                                List<RowFriend> friends = new ArrayList<RowFriend>();
                                try {
                                    int count = personBuffer.getCount();
                                    for (int i = 0; i < count; i++) {
                                        if (personBuffer.get(i) != null) {
                                            Log.d(TAG, "Display name: " + personBuffer.get(i).getDisplayName());
                                            RowFriend friend = new RowFriend(personBuffer.get(i).freeze());
                                            friends.add(friend);
                                        }
                                    }
                                } finally {
                                    personBuffer.close();
                                    if (friends.size() > 0) {
                                        controller.setFriends(friends);
//                                        Log.d(TAG, "# Friends: " + friends.size());
                                        showChooseFriendsDialog(friends);
                                    }
                                }
                            } else {
                                Log.e(TAG, "Error requesting visible circles: " + loadPeopleResult.getStatus());
                            }
                        }
                    });
        }
    }

}
