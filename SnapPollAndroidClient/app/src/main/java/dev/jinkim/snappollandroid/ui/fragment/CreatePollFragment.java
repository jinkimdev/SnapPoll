package dev.jinkim.snappollandroid.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.squareup.picasso.Picasso;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import java.io.File;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.imgur.ImgurConstants;
import dev.jinkim.snappollandroid.model.ImgurResponse;
import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.util.efilechooser.FileUtils;
import dev.jinkim.snappollandroid.web.ImgurRestClient;
import dev.jinkim.snappollandroid.web.SnapPollRestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by Jin on 11/27/14.
 */
public class CreatePollFragment extends Fragment {

    public static final String TAG = "#### CreatePollFragment";

    private static final int REQ_CODE_PICK_IMAGE = 1;
    public static final int RESULT_OK = -1;

    private Button btnTest;
    private Button btnAttachImage;
    private ActionProcessButton btnSubmit;

    /* poll data */
    private FloatLabeledEditText etQuestion;
    private FloatLabeledEditText etTitle;
    private Switch swMultiple;

    private ImageView ivThumbnail;

    private Uri uriSelectedImage;

    private Resources resource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_create_poll, container, false);

        resource = getActivity().getResources();

        initializeViews(rootView);

        return rootView;
    }

    private void initializeViews(View v) {
        etQuestion = (FloatLabeledEditText) v.findViewById(R.id.et_question);
        etTitle = (FloatLabeledEditText) v.findViewById(R.id.et_title);
        swMultiple = (Switch) v.findViewById(R.id.sw_multiple);

        ivThumbnail = (ImageView) v.findViewById(R.id.iv_thumbnail);
        Picasso.with(getActivity())
                .load(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .fit().centerInside()
                .into(ivThumbnail);

        btnSubmit = (ActionProcessButton) v.findViewById(R.id.btn_submit);
        btnSubmit.setMode(ActionProcessButton.Mode.ENDLESS);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etQuestion.getEditText().getText().toString();

                //TODO: Upload image to imgur
                //TODO when url is retrieved, make another call to POST /poll
                // grab info on poll
                if (uriSelectedImage == null) {
                    Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
                } else {
                    updateProcessButton(1, "Uploading image");
                    uploadImage(uriSelectedImage, title);
                }
            }
        });

        btnTest = (Button) v.findViewById(R.id.btn_test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                btnSubmit.setProgress(100);
//                test();

//                updateProcessButton(50, "Submitting poll");
//                ImgurResponse ir = new ImgurResponse();
//                ImgurResponseData id = new ImgurResponseData();
//                id.link = "http://i.imgur.com/Q7B42Eo.gif";
//                ir.data = id;
//                submitPoll(ir);

            }
        });

        btnAttachImage = (Button) v.findViewById(R.id.btn_attach_image);
        btnAttachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(v);
            }
        });
    }

    public void pickImage(View view) {
        Log.d(TAG, "Button clicked to pick image");
//        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent myIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        myIntent.setType("image/*");
        startActivityForResult(myIntent, REQ_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case REQ_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    uriSelectedImage = imageReturnedIntent.getData();
                    Log.d(TAG, "Image selected: " + uriSelectedImage.toString());
                    updateThumbnail(uriSelectedImage);
                }
        }
    }

    private void updateThumbnail(Uri selectedImage) {
        Log.d(TAG, "updateThumbnail");

        Picasso.with(getActivity())
                .load(selectedImage)
                .fit().centerInside()
                .into(ivThumbnail);
    }

    private void uploadImage(Uri uriSelectedImage, String imageTitle) {

        updateProcessButton(1, "Uploading image");
        //TODO: Check if this util works with various versions of Android
        File file = FileUtils.getFile(getActivity(), uriSelectedImage);

        //TODO: Catch exception and handle it
        TypedFile tf = new TypedFile("image/*", file);

        ImgurRestClient imgurRest = new ImgurRestClient();
        imgurRest.getApiService().uploadImage("Client-ID " + ImgurConstants.MY_IMGUR_CLIENT_ID,
                tf, imageTitle, resource.getString(R.string.image_description),
                new Callback<ImgurResponse>() {
                    @Override
                    public void success(ImgurResponse imgurResponse, Response response) {
                        Log.d(TAG, "Success: Image uploaded to Imgur");
                        Log.d(TAG, "Imgur link: " + imgurResponse.data.link);
                        Log.d(TAG, "Imgur deleteHash: " + imgurResponse.data.deletehash);

                        updateProcessButton(50, "Submitting poll...");
                        submitPoll(imgurResponse);
                        // TODO: EventBus event onImageUploadSuccessEvent -- pass Response object
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "Failure: Image not uploaded to Imgur");
                    }
                });
    }

    private void updateProcessButton(int prog, String msg) {
        if (prog == -1) {
            btnSubmit.setError(msg);
        } else {
            btnSubmit.setProgress(prog);
            btnSubmit.setText(msg);
        }
        if (prog == 100) {
            btnSubmit.setEnabled(false);
        }
    }

    private void submitPoll(ImgurResponse resp) {

        if (!resp.success) {
            // TODO: HANDLE ERROR
        }

        Poll p = new Poll();
        p.setCreatorId("gb");
        p.setQuestion(etQuestion.getEditText().getText().toString());
        p.setTitle(etTitle.getEditText().getText().toString());
        p.setActive(true);
        p.setMultipleResponseAllowed(swMultiple.isChecked());
        p.setReferenceUrl(resp.data.getLink());
        p.setReferenceDeleteHash(resp.data.getDeletehash());

        SnapPollRestClient rest = new SnapPollRestClient();
        rest.getApiService().postPoll(p.getCreatorId(), p.getTitle(), p.getQuestion(),
                p.isMultipleResponseAllowed(), p.getReferenceUrl(), p.getReferenceDeleteHash(),
                new Callback<Poll>() {
                    @Override
                    public void success(Poll poll, Response response) {
                        Log.d(TAG, "Success: pollId: " + poll.getPollId() + " uploaded to SnapPoll database");
                        updateProcessButton(100, "Poll submitted");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "Failure: Poll not uploaded: " + error.toString());
                        updateProcessButton(-1, "Submission failed");
                    }
                });
    }

    void test() {
        SnapPollRestClient rest = new SnapPollRestClient();
//            rest.getApiService().getPolls(new Callback<List<Poll>>() {
//                @Override
//                public void success(List<Poll> polls, Response response) {
//                    Log.d("####", "Polls: " + polls.get(0).question);
//                    Log.d("####", "Time: " + polls.get(0).pollTimestamp);
//                }
//
//                @Override
//                public void failure(RetrofitError retrofitError) {
//                    // Log error here since request failed
//                    Log.d("####", "" + retrofitError.toString());
//                }
//            });

        rest.getApiService().getPoll(6, new Callback<Poll>() {
            @Override
            public void success(Poll poll, Response response) {
                Log.d("####", "Poll by id: " + poll.question);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
