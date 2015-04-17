package dev.jinkim.snappollandroid.model;

import java.util.Date;
import java.util.List;

import dev.jinkim.snappollandroid.ui.adapter.MyPollListAdapter;

/**
 * Created by Jin on 11/23/14.
 */
public class Poll implements MyPollListAdapter.MyPollItem {
    public int pollId;
    public String creatorId = "";
    public String creatorLastName = "";
    public String creatorFirstName = "";
    public String creatorProfilePicUrl = "";
    public String title = "";
    public String question = "";
    public Date pollTimestamp;
    public boolean active = true;
    public boolean multipleResponseAllowed = false;
    public String referenceUrl = "";
    public String referenceDeleteHash = "";
    public boolean open = true;
    public List<PollAttribute> attributes;
    public int numResponses;

    public int getPollId() {
        return pollId;
    }

    public void setPollId(int pollId) {
        this.pollId = pollId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Date getPollTimestamp() {
        return pollTimestamp;
    }

    public void setPollTimestamp(Date pollTimestamp) {
        this.pollTimestamp = pollTimestamp;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isMultipleResponseAllowed() {
        return multipleResponseAllowed;
    }

    public void setMultipleResponseAllowed(boolean multipleResponseAllowed) {
        this.multipleResponseAllowed = multipleResponseAllowed;
    }

    public String getReferenceUrl() {
        return referenceUrl;
    }

    public void setReferenceUrl(String referenceUrl) {
        this.referenceUrl = referenceUrl;
    }

    public String getReferenceDeleteHash() {
        return referenceDeleteHash;
    }

    public void setReferenceDeleteHash(String referenceDeleteHash) {
        this.referenceDeleteHash = referenceDeleteHash;
    }

    public String getCreatorLastName() {
        return creatorLastName;
    }

    public void setCreatorLastName(String creatorLastName) {
        this.creatorLastName = creatorLastName;
    }

    public String getCreatorFirstName() {
        return creatorFirstName;
    }

    public void setCreatorFirstName(String creatorFirstName) {
        this.creatorFirstName = creatorFirstName;
    }

    public String getCreatorProfilePicUrl() {
        return creatorProfilePicUrl;
    }

    public void setCreatorProfilePicUrl(String creatorProfilePicUrl) {
        this.creatorProfilePicUrl = creatorProfilePicUrl;
    }

    @Override
    public boolean isPoll() {
        return true;
    }

    @Override
    public boolean isSection() {
        return false;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public List<PollAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<PollAttribute> attributes) {
        this.attributes = attributes;
    }

    public int getNumResponses() {
        return numResponses;
    }

    public void setNumResponses(int numResponses) {
        this.numResponses = numResponses;
    }

}
