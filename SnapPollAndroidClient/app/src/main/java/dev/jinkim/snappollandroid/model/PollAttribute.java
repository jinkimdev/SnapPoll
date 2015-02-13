package dev.jinkim.snappollandroid.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jin on 2/9/15.
 */
public class PollAttribute {
    public int attributeId;
    @SerializedName("attribute_name")
    public String attributeName;
    @SerializedName("attribute_color_hex")
    public String attributeColorHex;

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
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