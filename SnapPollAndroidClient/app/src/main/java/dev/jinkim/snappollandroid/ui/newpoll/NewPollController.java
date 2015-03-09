package dev.jinkim.snappollandroid.ui.newpoll;

import android.net.Uri;

import java.util.List;

import dev.jinkim.snappollandroid.model.PollAttribute;

/**
 * Created by Jin on 3/8/15.
 */
public class NewPollController {

    public static final String TAG = NewPollController.class.getSimpleName();

    public Uri uriSelectedImg;

    public String question;
    public String title;
    public List<PollAttribute> attributes;
    public boolean multipleResponseAllowed;

    public NewPollController() {
    }

    public Uri getUriSelectedImg() {
        return uriSelectedImg;
    }

    public void setUriSelectedImg(Uri uriSelectedImg) {
        this.uriSelectedImg = uriSelectedImg;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PollAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<PollAttribute> attributes) {
        this.attributes = attributes;
    }

    public boolean isMultipleResponseAllowed() {
        return multipleResponseAllowed;
    }

    public void setMultipleResponseAllowed(boolean multipleResponseAllowed) {
        this.multipleResponseAllowed = multipleResponseAllowed;
    }
}
