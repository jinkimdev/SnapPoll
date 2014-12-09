package dev.jinkim.snappollandroid.model;

import java.util.Date;

import dev.jinkim.snappollandroid.ui.adapter.MyPollAdapter;

/**
 * Created by Jin on 11/23/14.
 */
public class Poll implements MyPollAdapter.MyPollItem {
    public int pollId;
    public String creatorId = "";
    public String title = "";
    public String question = "";
    public Date pollTimestamp;
    public boolean active = true;
    public boolean multipleResponseAllowed = false;
    public String referenceUrl = "";
    public String referenceDeleteHash = "";

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

    @Override
    public boolean isPoll() {
        return true;
    }

    @Override
    public boolean isSection() {
        return false;
    }
}
