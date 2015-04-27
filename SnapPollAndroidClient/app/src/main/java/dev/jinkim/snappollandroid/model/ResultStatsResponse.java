package dev.jinkim.snappollandroid.model;

import android.content.Context;

import dev.jinkim.snappollandroid.R;

/**
 * Created by Jin on 4/22/15.
 *
 * ResultStatsResponse model object - include info like num reponses per answer choice in this poll
 */
public class ResultStatsResponse {

    public int attributeId;
    public int count;
    public int pollId;
    public String attributeName;
    public String attributeColorHex;

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPollId() {
        return pollId;
    }

    public void setPollId(int pollId) {
        this.pollId = pollId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeColorHex() {
        return attributeColorHex;
    }

    public void setAttributeColorHex(String attributeColorHex) {
        this.attributeColorHex = attributeColorHex;
    }

    public String formatText(Context context) {
        return String.format(context.getString(R.string.response_text), attributeName, count);
    }
}
