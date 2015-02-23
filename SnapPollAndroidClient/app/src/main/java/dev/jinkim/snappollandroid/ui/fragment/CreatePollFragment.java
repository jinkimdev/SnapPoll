package dev.jinkim.snappollandroid.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ProgressBarIndeterminate;
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
import dev.jinkim.snappollandroid.ui.adapter.ColorSpinnerAdapter;
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

    private ImageView btnSelectImage;
    private ImageView btnAddAttribute;
    private LinearLayout llAttributes;
    private ProgressBarIndeterminate progressBar;

    /* poll data */
    private FloatLabeledEditText etQuestion;
    private FloatLabeledEditText etTitle;
    private SwitchCompat swMultiple;

    private ImageView ivThumbnail;

    private Uri uriSelectedImage;

    private Resources resource;
    private CreatePollActivity mActivity;

    private Poll currentPoll;
    private ImgurResponse currentImgurResponse;
    private List<AttributeLineItem> attributes;

    private View rootView;

    private List<Pair<String, String>> listColor;

    private boolean showingDialog = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_create_poll, container, false);
        setHasOptionsMenu(true);

        mActivity = (CreatePollActivity) getActivity();
        resource = mActivity.getResources();

        initializeViews(rootView);

        // set up spinner color picker
        listColor = new ArrayList<Pair<String, String>>();
        listColor.add(new Pair("Red", "#FF0000"));
        listColor.add(new Pair("Orange", "#FFA500"));
        listColor.add(new Pair("Green", "#00FF00"));
        listColor.add(new Pair("Cyan", "#00FFFF"));

        return rootView;
    }

    private void initializeViews(View v) {
        progressBar = (ProgressBarIndeterminate) v.findViewById(R.id.pb_create_poll_upload_progress);
        etQuestion = (FloatLabeledEditText) v.findViewById(R.id.et_question);
        etTitle = (FloatLabeledEditText) v.findViewById(R.id.et_title);
        swMultiple = (SwitchCompat) v.findViewById(R.id.sw_multiple);

        llAttributes = (LinearLayout) v.findViewById(R.id.container_attributes);

        ivThumbnail = (ImageView) v.findViewById(R.id.iv_thumbnail);
//        Picasso.with(getActivity())
//                .load(R.drawable.ic_placeholder_image)
//                .placeholder(R.drawable.ic_placeholder_image)
//                .fit().centerInside()
//                .into(ivThumbnail);

        btnSelectImage = (ImageView) v.findViewById(R.id.iv_btn_select_image);
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(v);
            }
        });

        btnAddAttribute = (ImageView) v.findViewById(R.id.iv_btn_add_attribute);
        btnAddAttribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddAttributeDialog();
            }
        });
    }

    private void showEditAttributeDialog(String name, String colorHex,
                                         final TextView tvNameSelected, final View colorIndicatorSelected) {

        // prevent multiple dialogs
        if (!showingDialog) {
            showingDialog = true;

            // set up custom view for dialog - color indicator, edit text
            LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View row = vi.inflate(R.layout.dialog_content_new_attribute, null);

        /* display dialog with current values */
            final EditText etAttributeName = ((FloatLabeledEditText) row.findViewById(R.id.et_attribute_name)).getEditText();
            etAttributeName.setText(name);

            List<Pair<String, String>> colors = new ArrayList<Pair<String, String>>(listColor);

            // set up spinner color picker
            if (colorHex != null) {
                colors.add(0, new Pair("Unchanged", (String) colorIndicatorSelected.getTag()));
            }

            final Spinner spColorPicker = (Spinner) row.findViewById(R.id.sp_color_picker);
            spColorPicker.setAdapter(new ColorSpinnerAdapter(mActivity, R.layout.dialog_content_new_attribute, colors));

            // set up dialog
            String dialogTitle = "Edit Attribute";

            boolean wrapInScrollView = true;
            new MaterialDialog.Builder(mActivity)
                    .title(dialogTitle)
                    .customView(row, wrapInScrollView)
                    .positiveText("Save")
//                .positiveText(R.string.agree)
                    .negativeText("Cancel")
//                .negativeText(R.string.disagree)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                        /* reflect edit made on the attribute line */
                            Pair selected = (Pair<String, String>) spColorPicker.getSelectedItem();
                            tvNameSelected.setText(etAttributeName.getText().toString());
                            colorIndicatorSelected.setBackgroundColor(Color.parseColor((String) selected.second));
                            colorIndicatorSelected.setTag((String) selected.second);
                        }
                    })
                    .dismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            showingDialog = false;
                        }
                    })
                    .show();
        }
    }

    private void showAddAttributeDialog() {

        // prevent multiple dialogs
        if (!showingDialog) {
            showingDialog = true;

            // set up custom view for dialog - color indicator, edit text
            LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View row = vi.inflate(R.layout.dialog_content_new_attribute, null);

            final EditText etAttributeName = ((FloatLabeledEditText) row.findViewById(R.id.et_attribute_name)).getEditText();

            // set up spinner color picker
            List<Pair<String, String>> colors = new ArrayList<Pair<String, String>>(listColor);

            final Spinner spColorPicker = (Spinner) row.findViewById(R.id.sp_color_picker);
            spColorPicker.setAdapter(new ColorSpinnerAdapter(mActivity, R.layout.dialog_content_new_attribute, colors));

            // set up dialog
            String dialogTitle = "New Attribute";

            boolean wrapInScrollView = true;
            new MaterialDialog.Builder(mActivity)
                    .title(dialogTitle)
                    .customView(row, wrapInScrollView)
                    .positiveText("Save")
//                .positiveText(R.string.agree)
                    .negativeText("Cancel")
//                .negativeText(R.string.disagree)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            Pair selected = (Pair<String, String>) spColorPicker.getSelectedItem();

                            Log.d(TAG, "## Attr Dialog - " + (String) selected.second
                                    + ", " + etAttributeName.getText().toString());

                            addAttributeLine((String) selected.second, etAttributeName.getText().toString());
                        }
                    })
                    .dismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            showingDialog = false;
                        }
                    })
                    .show();
        }
    }

    private void addAttributeLine(final String colorHex, final String attributeName) {
        LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = vi.inflate(R.layout.row_poll_attribute_line_item, null);

        final View colorIndicator = row.findViewById(R.id.view_attribute_line_color_indicator);
        final TextView tvAttributeName = (TextView) row.findViewById(R.id.tv_attribute_line_attribute_name);

        colorIndicator.setBackgroundColor(Color.parseColor(colorHex));
        // save color hex into the view tag
        colorIndicator.setTag(colorHex);

        tvAttributeName.setText(attributeName);

        final ImageView btnEditAttribute = (ImageView) row.findViewById(R.id.iv_btn_attribute_line_edit);
        btnEditAttribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentName = tvAttributeName.getText().toString();
//                String currentColorHex = ColorUtil.convertToHex(colorIndicator.getSolidColor());
                showEditAttributeDialog(currentName, (String) colorIndicator.getTag(), tvAttributeName, colorIndicator);
            }
        });

        final ImageView btnRemoveAttribute = (ImageView) row.findViewById(R.id.iv_btn_attribute_line_remove);
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
        }

        // insert into main view
        llAttributes.addView(row, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        AttributeLineItem line = new AttributeLineItem();
        line.setAttributeColorHex(colorHex);
        line.setAttributeName(attributeName);

        attributes.add(line);
    }

    private List<PollAttribute> grabAttributes() {
        if (llAttributes == null) {
            return null;
        }

        List<PollAttribute> attributeList = new ArrayList<PollAttribute>();

        for (int i = 0; i < llAttributes.getChildCount(); i++) {
            View c = llAttributes.getChildAt(i);
            TextView tvAttributeName = (TextView) c.findViewById(R.id.tv_attribute_line_attribute_name);

            View v = c.findViewById(R.id.view_attribute_line_color_indicator);

            PollAttribute at = new PollAttribute();
            at.setAttributeName(tvAttributeName.getText().toString());

            String colorHex = (String) v.getTag();
            if (colorHex == null) {
                // TODO: DEFAULT COLOR VALUE
                colorHex = "#FFA500";
            }
            at.setAttributeColorHex(colorHex);
            attributeList.add(at);

            Log.d(TAG, "## Attr: " + at);
        }

        return attributeList;
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

                    // clear previously uploaded imgur response
                    if (currentImgurResponse != null)
                        currentImgurResponse = null;
                }
        }
    }

    private void updateThumbnail(Uri selectedImage) {
        Log.d(TAG, "updateThumbnail");

        ivThumbnail.setVisibility(View.VISIBLE);

        Picasso.with(getActivity())
                .load(selectedImage)
                .fit().centerCrop()
                .into(ivThumbnail);
    }

    private void uploadImage(Uri uriSelectedImage, String imageTitle) {

        // if the image is already uploaded
        if (currentImgurResponse != null) {
            submitPoll(currentImgurResponse);

        } else {
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

        User u = App.getInstance().getCurrentUser(getActivity());

        currentPoll = new Poll();
        currentPoll.setCreatorId(u.getUserId());
        currentPoll.setQuestion(etQuestion.getEditText().getText().toString());
        currentPoll.setTitle(etTitle.getEditText().getText().toString());
        currentPoll.setActive(true);
        currentPoll.setMultipleResponseAllowed(swMultiple.isChecked());
        currentPoll.setReferenceUrl(resp.data.getLink());
        currentPoll.setReferenceDeleteHash(resp.data.getDeletehash());
        currentPoll.setAttributes(grabAttributes());

        SnapPollRestClient.ApiService rest = new SnapPollRestClient().getApiService();

        rest.postPoll(currentPoll, new Callback<Poll>() {
            @Override
            public void success(Poll poll, Response response) {
                Log.d(TAG, "Success: pollId: " + poll.getPollId() + " uploaded to SnapPoll database");
                Bus bus = mActivity.getEventBus();
                bus.post(new PollSubmittedEvent());
                progressBar.setVisibility(View.INVISIBLE);
                mActivity.finish();
            }

            @Override
            public void failure(RetrofitError error) {
                // TODO: failure message for user
                Log.d(TAG, "Failure: Poll not uploaded: " + error.toString());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_create_poll, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_poll_detail_submit:

                String title = etQuestion.getEditText().getText().toString();

                // grab info on poll
                if (uriSelectedImage == null) {
                    Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    uploadImage(uriSelectedImage, title);
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class AttributeLineItem {
        private String attributeColorHex;
        private String attributeName;

        public String getAttributeColorHex() {
            return attributeColorHex;
        }

        public void setAttributeColorHex(String attributeColorHex) {
            this.attributeColorHex = attributeColorHex;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public void setAttributeName(String attributeName) {
            this.attributeName = attributeName;
        }
    }
}