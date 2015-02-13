package dev.jinkim.snappollandroid.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.app.App;
import dev.jinkim.snappollandroid.event.PollSubmittedEvent;
import dev.jinkim.snappollandroid.imgur.ImgurConstants;
import dev.jinkim.snappollandroid.model.ImgurResponse;
import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.model.PollAttribute;
import dev.jinkim.snappollandroid.model.User;
import dev.jinkim.snappollandroid.ui.activity.CreatePollActivity;
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

    public static final String TAG = "CreatePollFragment";

    private static final int REQ_CODE_PICK_IMAGE = 1;
    public static final int RESULT_OK = -1;

    private Button btnAttachImage, btnAddAttribute, btnGrab;
    private ActionProcessButton btnSubmit;
    private LinearLayout llAttributes;

    /* poll data */
    private FloatLabeledEditText etQuestion;
    private FloatLabeledEditText etTitle;
    private SwitchCompat swMultiple;

    private ImageView ivThumbnail;

    private Uri uriSelectedImage;

    private Resources resource;
    private CreatePollActivity mActivity;

    private List<AttributeLineItem> attributes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_create_poll, container, false);

        mActivity = (CreatePollActivity) getActivity();
        resource = mActivity.getResources();

        initializeViews(rootView);

        return rootView;
    }

    private void initializeViews(View v) {
        etQuestion = (FloatLabeledEditText) v.findViewById(R.id.et_question);
        etTitle = (FloatLabeledEditText) v.findViewById(R.id.et_title);
        swMultiple = (SwitchCompat) v.findViewById(R.id.sw_multiple);

        llAttributes = (LinearLayout) v.findViewById(R.id.container_attributes);

        ivThumbnail = (ImageView) v.findViewById(R.id.iv_thumbnail);
        Picasso.with(getActivity())
                .load(R.drawable.ic_placeholder_image)
                .placeholder(R.drawable.ic_placeholder_image)
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

        btnAttachImage = (Button) v.findViewById(R.id.btn_attach_image);
        btnAttachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(v);
            }
        });

        btnAddAttribute = (Button) v.findViewById(R.id.btn_add_attribute);
        btnAddAttribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAttributeLine(v);
            }
        });

        btnGrab = (Button) v.findViewById(R.id.btn_grab_attributes);
        btnGrab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grabAttributes();
            }
        });
    }

    private List<PollAttribute> grabAttributes() {
        if (llAttributes == null) {
            return null;
        }

        List<PollAttribute> attributeList = new ArrayList<PollAttribute>();

        for (int i = 0; i < llAttributes.getChildCount(); i++) {
            View c = llAttributes.getChildAt(i);
            FloatLabeledEditText etAttributeName = (FloatLabeledEditText) c.findViewById(R.id.et_attribute_name);

            PollAttribute at = new PollAttribute();
            at.setAttributeName(etAttributeName.getEditText().getText().toString());
            // TODO: Grab color from picker or set default value
            at.setAttributeColorHex("#5555FF");
            attributeList.add(at);

            Log.d(TAG, "## Attr: " + at);
        }

        return attributeList;
    }

    private void addAttributeLine(View rootView) {
        LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = vi.inflate(R.layout.row_poll_attribute, null);

        // fill in any details dynamically here
        FloatLabeledEditText etAttributeName = (FloatLabeledEditText) row.findViewById(R.id.et_attribute_name);
//        etAttributeName.getEditText().setText("My Attribute");
        final Button btnRemoveAttribute = (Button) row.findViewById(R.id.btn_attribute_remove);
        btnRemoveAttribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llAttributes.removeView(row);
                Log.d(TAG, "Attributes: " + llAttributes.getChildCount());
            }
        });

        // add objects to the list
        if (attributes == null) {
            attributes = new ArrayList<AttributeLineItem>();
//            row.setTag(0);

        }

        // insert into main view
        llAttributes.addView(row, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        AttributeLineItem line = new AttributeLineItem();
        line.setEditText(etAttributeName.getEditText());
        line.setAttributeColor("#FCC");

        attributes.add(line);
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

        User u = App.getInstance().getCurrentUser(getActivity());

        Poll p = new Poll();
        p.setCreatorId(u.getUserId());
        p.setQuestion(etQuestion.getEditText().getText().toString());
        p.setTitle(etTitle.getEditText().getText().toString());
        p.setActive(true);
        p.setMultipleResponseAllowed(swMultiple.isChecked());
        p.setReferenceUrl(resp.data.getLink());
        p.setReferenceDeleteHash(resp.data.getDeletehash());
        p.setAttributes(grabAttributes());

        SnapPollRestClient.ApiService rest = new SnapPollRestClient().getApiService();


        rest.postPoll(p, new Callback<Poll>() {
            @Override
            public void success(Poll poll, Response response) {
                Log.d(TAG, "Success: pollId: " + poll.getPollId() + " uploaded to SnapPoll database");
                updateProcessButton(100, "Poll submitted");
                Bus bus = mActivity.getEventBus();
                bus.post(new PollSubmittedEvent());
                mActivity.finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failure: Poll not uploaded: " + error.toString());
                updateProcessButton(-1, "Submission failed");
            }
        });
    }

    public static class AttributeLineItem {
        private String attributeColor;
        private EditText editText;

        public String getAttributeColor() {
            return attributeColor;
        }

        public void setAttributeColor(String attributeColor) {
            this.attributeColor = attributeColor;
        }

        public EditText getEditText() {
            return editText;
        }

        public void setEditText(EditText editText) {
            this.editText = editText;
        }
    }
}
