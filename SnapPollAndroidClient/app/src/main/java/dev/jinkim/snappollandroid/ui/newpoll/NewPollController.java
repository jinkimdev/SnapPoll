package dev.jinkim.snappollandroid.ui.newpoll;

import android.net.Uri;
import android.util.Log;

import com.squareup.otto.Bus;

import java.io.File;
import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.app.App;
import dev.jinkim.snappollandroid.event.PollSubmittedEvent;
import dev.jinkim.snappollandroid.imgur.ImgurConstants;
import dev.jinkim.snappollandroid.model.ImgurResponse;
import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.model.PollAttribute;
import dev.jinkim.snappollandroid.model.RowFriend;
import dev.jinkim.snappollandroid.model.User;
import dev.jinkim.snappollandroid.util.efilechooser.FileUtils;
import dev.jinkim.snappollandroid.web.ImgurRestClient;
import dev.jinkim.snappollandroid.web.SnapPollRestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by Jin on 3/8/15.
 */
public class NewPollController {

    public static final String TAG = NewPollController.class.getSimpleName();

    public Uri uriSelectedImg;

    public int pollId = -1;
    public String question;
    public String title;
    public List<PollAttribute> attributes;
    public boolean multipleResponseAllowed;
    private List<RowFriend> friends;

    private ImgurResponse currentImgurResponse;

    private NewPollActivity mActivity;

    public NewPollController(NewPollActivity activity) {
        mActivity = activity;
    }

    public Uri getUriSelectedImg() {
        return uriSelectedImg;
    }

    public void setUriSelectedImg(Uri uriSelectedImg) {
        this.uriSelectedImg = uriSelectedImg;
    }

    public int getPollId() {
        return pollId;
    }

    public void setPollId(int pollId) {
        this.pollId = pollId;
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

    public void setFriends(List<RowFriend> friends) {
        this.friends = friends;
    }

    public List<RowFriend> getFriends() {
        return friends;
    }

    public void uploadImage() {

        // if the image is already uploaded
        if (currentImgurResponse != null) {
            submitPoll(currentImgurResponse);

        } else {
            //TODO: Check if this util works with various versions of Android
            File file = FileUtils.getFile(mActivity, uriSelectedImg);

            //TODO: Catch exception and handle it
            TypedFile tf = new TypedFile("image/*", file);

            ImgurRestClient imgurRest = new ImgurRestClient();
            imgurRest.getApiService().uploadImage("Client-ID " + ImgurConstants.MY_IMGUR_CLIENT_ID,
                    tf, title, mActivity.getString(R.string.image_description),
                    new Callback<ImgurResponse>() {
                        @Override
                        public void success(ImgurResponse imgurResponse, Response response) {
                            currentImgurResponse = imgurResponse;
                            Log.d(TAG, "Success: Image uploaded to Imgur");
                            Log.d(TAG, "Imgur link: " + currentImgurResponse.data.link);
                            Log.d(TAG, "Imgur deleteHash: " + currentImgurResponse.data.deletehash);

                            submitPoll(currentImgurResponse);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d(TAG, "Failure: Image not uploaded to Imgur");
                        }
                    });
        }
    }

    private void submitPoll(ImgurResponse resp) {

        if (!resp.success) {
            // TODO: HANDLE ERROR
        }
        User u = App.getInstance().getCurrentUser(mActivity);

        Poll currentPoll = new Poll();
        currentPoll.setCreatorId(u.getUserId());
        currentPoll.setQuestion(question);
        currentPoll.setTitle(title);
        currentPoll.setActive(true);
        currentPoll.setMultipleResponseAllowed(isMultipleResponseAllowed());
        currentPoll.setReferenceUrl(resp.data.getLink());
        currentPoll.setReferenceDeleteHash(resp.data.getDeletehash());
        currentPoll.setAttributes(attributes);

        SnapPollRestClient.ApiService rest = new SnapPollRestClient().getApiService();

        rest.createPoll(currentPoll, new Callback<Poll>() {
            @Override
            public void success(Poll poll, Response response) {
                Log.d(TAG, "Success: pollId: " + poll.getPollId() + " uploaded to SnapPoll database");
                Bus bus = mActivity.getEventBus();
                bus.post(new PollSubmittedEvent());
                // TODO: handle progress bar
//                progressBar.setVisibility(View.INVISIBLE);
                setPollId(poll.getPollId());
                // TODO: update flow so POST will happen before G+ share
//                mActivity.finish();
            }

            @Override
            public void failure(RetrofitError error) {
                // TODO: failure message for user
                Log.d(TAG, "Failure: Poll not uploaded: " + error.toString());
            }
        });
    }
}
