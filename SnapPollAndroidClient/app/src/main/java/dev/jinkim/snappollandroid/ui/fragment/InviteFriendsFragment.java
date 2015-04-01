package dev.jinkim.snappollandroid.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ButtonFloat;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.util.ArrayList;
import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.PollInvitedFriends;
import dev.jinkim.snappollandroid.model.RowFriend;
import dev.jinkim.snappollandroid.ui.activity.InviteFriendsActivity;
import dev.jinkim.snappollandroid.ui.newpoll.ChooseFriendListAdapter;
import dev.jinkim.snappollandroid.ui.newpoll.SelectedFriendListAdapter;
import dev.jinkim.snappollandroid.web.SnapPollRestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jin on 3/6/15.
 */
public class InviteFriendsFragment extends Fragment {

    public static String TAG = InviteFriendsFragment.class.getSimpleName();

    private InviteFriendsActivity mActivity;
    private int pollId;

//    private NewPollController controller;

    private ListView listView;
    private SelectedFriendListAdapter adapter;
    // list of Google+ friends (retrieved from Google+ API)
    private List<RowFriend> retrievedFriends;
    // list of IDs of friends already invited to the poll (retrieved from SnapPoll API)
    private List<String> invitedFriendIds;
    // list of friends selected to invite from the dialog (selected from dialog)
    private List<RowFriend> selectedFriends;

    private ButtonFloat fabSelectFriends;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_new_poll_friends, container, false);
        setHasOptionsMenu(true);
        mActivity = (InviteFriendsActivity) getActivity();
        pollId = getArguments().getInt(getString(R.string.key_poll_id));
//        controller = mActivity.getController();

        mActivity.getSupportActionBar().setTitle(R.string.title_invite_friends);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initViews(view);

        // TODO: RETRIEVE INVITED FRIENDS
        SnapPollRestClient.ApiService rest = new SnapPollRestClient().getApiService();
        rest.getPollInvitedFriends(pollId, new Callback<PollInvitedFriends>() {
            @Override
            public void success(PollInvitedFriends pollInvitedFriends, Response response) {
                // TODO: PARSE LIST OF FRIENDS
                invitedFriendIds = pollInvitedFriends.friends;
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void showChooseFriendsDialog(List<RowFriend> friends) {
        // set up custom view for dialog - color indicator, edit text
        LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View content = vi.inflate(R.layout.dialog_content_choose_friends, null);


        final ChooseFriendListAdapter adapter = new ChooseFriendListAdapter(mActivity, friends);

        // sparse boolean array to keep up with selected friend list
        final SparseBooleanArray selected = new SparseBooleanArray(friends.size());

        for (int i = 0; i < friends.size(); i++) {
            // if preselected from existing list, then update the sparse array
            if (friends.get(i).selected) {
                selected.append(i, true);
            }
        }

        /* SET UP SELECT FRIENDS LIST VIEW */
        final ListView listView = (ListView) content.findViewById(R.id.friends_dialog_lv_friends);
        listView.setTextFilterEnabled(true);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemsCanFocus(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItem(position).selected) {
                    adapter.getItem(position).selected = false;
                    adapter.getItemFromOriginalList(adapter.getOriginalIndex(position)).selected = false;
                    selected.delete(position);
                } else {
                    adapter.getItem(position).selected = true;
                    adapter.getItemFromOriginalList(adapter.getOriginalIndex(position)).selected = true;
                    selected.append(position, true);
                }
                Log.d(TAG, "Selected friend position: " + String.valueOf(position));
                Log.d(TAG, "Selected friend id: " + String.valueOf(id));
                adapter.notifyDataSetChanged();
            }
        });
        listView.setAdapter(adapter);

        SearchView svSearch = (SearchView) content.findViewById(R.id.friends_dialog_sv_search);
        svSearch.setQueryHint(mActivity.getString(R.string.query_hint_search_from_gplus));
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return true;
            }
        });

        /* SET UP SELECT FRIENDS DIALOG */
        String dialogTitle = mActivity.getResources().getString(R.string.dialog_title_choose_frineds_gplus);

        boolean wrapInScrollView = false;
        MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                .title(dialogTitle)
                .customView(content, wrapInScrollView)
                .positiveText(R.string.action_save)
//                .positiveText(R.string.agree)
                .negativeText(R.string.action_cancel)
//                .negativeText(R.string.disagree)
                .callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        /* update selected friends */
                        Log.d(TAG, selected.toString());

                        // TODO: grab the keys (positions) that are true in value
                        selectedFriends = new ArrayList<RowFriend>();
                        for (int i = 0; i < selected.size(); i++) {
                            int position = selected.keyAt(i);
                            if (selected.valueAt(i)) {
                                // grab the selected friends from the original list (inst of filteredList)
                                RowFriend item = adapter.getItemFromOriginalList(adapter.getOriginalIndex(position));
                                selectedFriends.add(item);
                            }
                        }

                        updateSelectedFriends(selectedFriends);

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

    private void initViews(View v) {
        listView = (ListView) v.findViewById(R.id.lv_selected_friends);
        fabSelectFriends = (ButtonFloat) v.findViewById(R.id.fab_select_friends);
        fabSelectFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (retrievedFriends != null && retrievedFriends.size() > 0) {
                    // if friend list from G+ is available display in the dialog
                    showChooseFriendsDialog(retrievedFriends);
                } else {
                    // if not available, fetch friend list from G+
                    retrieveFriendsFromGPlus();
                }
            }
        });

        adapter = new SelectedFriendListAdapter(mActivity, new ArrayList<RowFriend>());
        listView.setAdapter(adapter);
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
    }

    /**
     * Update the fragment listview with the friends selected from the dialog
     * Also update the controller with the current list
     *
     * @param selectedFriends
     */
    private void updateSelectedFriends(List<RowFriend> selectedFriends) {
        adapter = new SelectedFriendListAdapter(mActivity, selectedFriends);
        listView.setAdapter(null);
        listView.setAdapter(adapter);

//        controller.setFriends(selectedFriends);
    }

    /**
     * If GoogleApiClient is connected, make a call to get friends list
     * <p/>
     * List of Google+ Person model
     * <p/>
     * https://developer.android.com/reference/com/google/android/gms/plus/model/people/Person.html
     */
    private void retrieveFriendsFromGPlus() {

        GoogleApiClient mGoogleApiClient = mActivity.getGoogleApiClient();
        if (mGoogleApiClient.isConnected()) {

            Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
                    .setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
                        @Override
                        public void onResult(People.LoadPeopleResult loadPeopleResult) {
                            if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
                                PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
                                retrievedFriends = new ArrayList<RowFriend>();
                                try {
                                    int count = personBuffer.getCount();
                                    for (int i = 0; i < count; i++) {
                                        if (personBuffer.get(i) != null) {
                                            Log.d(TAG, "Display name: " + personBuffer.get(i).getDisplayName());
                                            RowFriend friend = new RowFriend(personBuffer.get(i).freeze());
                                            retrievedFriends.add(friend);
                                        }
                                    }
                                } finally {
                                    personBuffer.close();
                                    if (retrievedFriends.size() > 0) {
//                                        controller.setFriends(retrievedFriends);
//                                        Log.d(TAG, "# Friends: " + friends.size());
                                        showChooseFriendsDialog(retrievedFriends);
                                    }
                                }
                            } else {
                                Log.e(TAG, "Error requesting visible circles: " + loadPeopleResult.getStatus());
                            }
                        }
                    });
        }
    }

    /**
     * takes in relative position to be animated to
     *
     * @param position
     */
    public void moveFloatButton(float position) {
        fabSelectFriends.animate().translationY(position);
    }

    public void inviteFriends() {
//        int pollId = controller.getPollId();

        if (pollId == -1) {
            mActivity.displaySnackBar(getString(R.string.msg_poll_id_invalid));
            return;
        }

        List<String> listIds = new ArrayList<String>();
        for (RowFriend r : selectedFriends) {
            listIds.add(r.person.getId());
        }

        String friends = TextUtils.join(",", listIds);

        // check selected list
        if (listIds.size() < 1) {
            mActivity.displaySnackBar(getString(R.string.msg_no_friend_selected));
            return;
        }

        SnapPollRestClient.ApiService rest = new SnapPollRestClient().getApiService();
        rest.inviteFriends(pollId, friends, new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {
                Log.d(TAG, "Invited friends to poll: " + String.valueOf(pollId));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    /* share this poll on Google+ with selected friends */
    public void shareOnGplus() {
//        PlusShare.Builder builder = new PlusShare.Builder(mActivity);

        List<Person> recipients = new ArrayList<Person>();
        for (RowFriend r : selectedFriends) {
            if (r.selected) {
                recipients.add(r.person);
            }
        }

        String pollUrl = "http://snappoll.herokuapp.com/poll/" + String.valueOf(pollId);
//        String deepLinkString = "snappoll://view_poll/" + "poll_id=" + String.valueOf(controller.getPollId());
        String deepLinkString = "vnd.google.deeplink://view_poll/" + "poll_id=" + String.valueOf(pollId);
//        String deepLinkString = "intent://view_poll/#Intent;package=dev.jinkim.snappollandroid.ui.activity;"
//                + "scheme=snappoll;end;";
//                + String.valueOf(controller.getPollId());
        // host view_poll
        // scheme snappoll

        Log.d(TAG, "Deep Link ID: " + deepLinkString);
        PlusShare.Builder builder = new PlusShare.Builder(mActivity);

        // Set call-to-action metadata.
        builder.addCallToAction(
                "VIEW", /** call-to-action button label */
                Uri.parse(deepLinkString), /** call-to-action url (for desktop use) */
                deepLinkString /** call to action deep-link ID (for mobile use), 512 characters or fewer */);

        // Set the content url (for desktop use).
        builder.setContentUrl(Uri.parse(deepLinkString));

        // Set the target deep-link ID (for mobile use).
        builder.setContentDeepLinkId(deepLinkString,
                null, null, null);

        builder.setRecipients(recipients);
        // Set the share text.
        builder.setText("View Poll: " + String.valueOf(pollId));

        startActivityForResult(builder.getIntent(), 0);


    }

}
