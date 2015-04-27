package dev.jinkim.snappollandroid.model;

import java.util.List;

/**
 * Created by Jin on 4/1/15.
 *
 * PollInvitedFriendsResponse from SnapPoll REST API
 */
public class PollInvitedFriendsResponse {
    public int pollId;
    public List<String> friends;

    public int getPollId() {
        return pollId;
    }

    public void setPollId(int pollId) {
        this.pollId = pollId;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }
}
