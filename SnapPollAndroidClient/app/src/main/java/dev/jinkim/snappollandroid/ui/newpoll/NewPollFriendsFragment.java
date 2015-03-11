package dev.jinkim.snappollandroid.ui.newpoll;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.util.ArrayList;
import java.util.List;

import dev.jinkim.snappollandroid.R;

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

    private void showChooseFriendsDialog(List<Person> friends) {
        // set up custom view for dialog - color indicator, edit text
        LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View content = vi.inflate(R.layout.dialog_content_choose_friends, null);

        ListView listView = (ListView) content.findViewById(R.id.friends_dialog_lv_friends);
        SelectFriendListAdapter adapter = new SelectFriendListAdapter(mActivity, friends);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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
                                List<Person> friends = new ArrayList<Person>();
                                try {
                                    int count = personBuffer.getCount();
                                    for (int i = 0; i < count; i++) {
                                        if (personBuffer.get(i) != null) {
                                            Log.d(TAG, "Display name: " + personBuffer.get(i).getDisplayName());
                                            Person p = personBuffer.get(i).freeze();
                                            friends.add(p);
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
