package dev.jinkim.snappollandroid.model;

/**
 * Created by Jin on 1/25/15.
 */
public class Response {

    public int responseId;
    public int pollId;
    public float x;
    public float y;
    public String userId;
    public int attributeChoice;

    public Response(int pollId, float x, float y,
                    String userId, int attributeChoice) {
        this.pollId = pollId;
        this.x = x;
        this.y = y;
        this.userId = userId;
        this.attributeChoice = attributeChoice;
    }

    public int getResponseId() {
        return responseId;
    }

    public void setResponseId(int responseId) {
        this.responseId = responseId;
    }

    public int getPollId() {
        return pollId;
    }

    public void setPollId(int pollId) {
        this.pollId = pollId;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getAttributeChoice() {
        return attributeChoice;
    }

    public void setAttributeChoice(int attributeChoice) {
        this.attributeChoice = attributeChoice;
    }
}
