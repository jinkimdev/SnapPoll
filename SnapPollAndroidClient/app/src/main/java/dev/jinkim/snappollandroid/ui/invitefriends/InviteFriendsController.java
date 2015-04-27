package dev.jinkim.snappollandroid.ui.invitefriends;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dev.jinkim.snappollandroid.web.SnapPollRestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jin on 4/3/15.
 *
 * Keep up with the list of friends (Google+) invited to the poll
 */
public class InviteFriendsController {

    public static String TAG = InviteFriendsController.class.getSimpleName();

    private Context mContext;

    private int pollId = -1;

    // list of Google+ friends (retrieved from Google+ API)
    private List<RowFriend> gPlusFriends;
    // list of IDs of friends already invited to the poll (retrieved from SnapPoll API)
    private List<String> pollInviteeIds;

    public InviteFriendsController(Activity activity) {
        mContext = activity;
    }

    public int getPollId() {
        return pollId;
    }

    public void setPollId(int pollId) {
        this.pollId = pollId;
    }

    public List<RowFriend> getgPlusFriends() {
        return gPlusFriends;
    }

    public void setgPlusFriends(List<RowFriend> gPlusFriends) {
        this.gPlusFriends = gPlusFriends;
    }

    public List<String> getPollInviteeIds() {
        return pollInviteeIds;
    }

    public void setPollInviteeIds(List<String> pollInviteeIds) {
        this.pollInviteeIds = pollInviteeIds;
    }

    public void inviteSelectedFriends(List<RowFriend> selectedFriends) {

        List<String> listIds = new ArrayList<String>();
//        String ids = "";
        // convert list of selectedFriends to comma-separated string
        for (RowFriend r : selectedFriends) {
            listIds.add(r.person.getId());
        }
        String out = TextUtils.join(",", listIds);

        SnapPollRestClient.ApiService rest = new SnapPollRestClient().getApiService();
        rest.inviteFriends(pollId, out, new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {
                Log.d(TAG, "## Success: invite friends to poll: " + String.valueOf(pollId));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
