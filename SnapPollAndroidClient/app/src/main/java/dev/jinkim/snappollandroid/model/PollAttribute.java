package dev.jinkim.snappollandroid.model;

import com.google.gson.annotations.SerializedName;

import dev.jinkim.snappollandroid.util.ColorUtil;

/**
 * Created by Jin on 2/9/15.
 */
public class PollAttribute {
    @SerializedName("attribute_id")
    public int attributeId;
    @SerializedName("poll_id")
    public int pollId;
    @SerializedName("attribute_name")
    public String attributeName;
    @SerializedName("attribute_color_hex")
    public String attributeColorHex;

    public PollAttribute() {
    }

    public PollAttribute(String attributeName, String attributeColorHex) {
        this.attributeName = attributeName;
        this.attributeColorHex = attributeColorHex;
    }

    public PollAttribute(String attributeName, int attributeColor) {
        this.attributeName = attributeName;
        this.attributeColorHex = ColorUtil.convertToHex(attributeColor);
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
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
}