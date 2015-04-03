package dev.jinkim.snappollandroid.ui.invite;

import android.app.Activity;
import android.content.Context;

import java.util.List;

import dev.jinkim.snappollandroid.model.RowFriend;

/**
 * Created by Jin on 4/3/15.
 */
public class InviteFriendsController {

    public static String TAG = InviteFriendsController.class.getSimpleName();

    private Context mContext;

    private int pollId = -1;

    // list of Google+ friends (retrieved from Google+ API)
    private List<RowFriend> gPlusFriends;
    // list of IDs of friends already invited to the poll (retrieved from SnapPoll API)
    private List<String> pollInviteeIds;
    // list of friends selected to invite from the dialog (selected from dialog)
    private List<RowFriend> selectedFriends;

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

    public List<RowFriend> getSelectedFriends() {
        return selectedFriends;
    }

    public void setSelectedFriends(List<RowFriend> selectedFriends) {
        this.selectedFriends = selectedFriends;
    }
}
