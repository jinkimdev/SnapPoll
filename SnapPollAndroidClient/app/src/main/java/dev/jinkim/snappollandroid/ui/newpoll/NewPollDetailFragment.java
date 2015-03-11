package dev.jinkim.snappollandroid.ui.newpoll;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import java.util.ArrayList;
import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.PollAttribute;
import dev.jinkim.snappollandroid.ui.adapter.ColorSpinnerAdapter;

/**
 * Created by Jin on 3/6/15.
 */
public class NewPollDetailFragment extends Fragment {

    public static String TAG = "NewPollDetailFragment";
    private NewPollActivity mActivity;

    private ImageView ivImageBackground;
    private ImageView btnAddAttribute;
    private LinearLayout llAttributes;

    /* poll data */
    private FloatLabeledEditText etQuestion;
    private FloatLabeledEditText etTitle;
    private SwitchCompat swMultiple;

    private List<NewPollImageReferenceFragment.AttributeLineItem> attributes;
    private boolean showingDialog = false;

    private List<Pair<String, String>> listColor;

    private NewPollController controller;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag_new_poll_detail, container, false);

        Log.d(TAG, "In NewPollDetailFragment");

        mActivity = (NewPollActivity) getActivity();
        controller = mActivity.getController();

        mActivity.getSupportActionBar().setTitle("Enter Detail");

        // set up spinner color picker
        listColor = new ArrayList<Pair<String, String>>();
        listColor.add(new Pair("Red", "#FF0000"));
        listColor.add(new Pair("Orange", "#FFA500"));
        listColor.add(new Pair("Green", "#00FF00"));
        listColor.add(new Pair("Cyan", "#00FFFF"));

        initializeViews(rootView);

        setHasOptionsMenu(true);


        return rootView;
    }

    private void initializeViews(View v) {
//        progressBar = (ProgressBarIndeterminate) v.findViewById(R.id.pb_create_poll_upload_progress);

        ivImageBackground = (ImageView) v.findViewById(R.id.new_poll_detail_iv_background_image);
        if (controller.getUriSelectedImg() != null) {
            Picasso.with(mActivity)
                    .load(controller.getUriSelectedImg())
                    .fit().centerCrop()
                    .into(ivImageBackground);
        }

        etQuestion = (FloatLabeledEditText) v.findViewById(R.id.et_question);
        etTitle = (FloatLabeledEditText) v.findViewById(R.id.et_title);
        swMultiple = (SwitchCompat) v.findViewById(R.id.sw_multiple);

        llAttributes = (LinearLayout) v.findViewById(R.id.container_attributes);

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
            final View content = vi.inflate(R.layout.dialog_content_new_attribute, null);

        /* display dialog with current values */
            final EditText etAttributeName = ((FloatLabeledEditText) content.findViewById(R.id.et_attribute_name)).getEditText();
            etAttributeName.setText(name);

            List<Pair<String, String>> colors = new ArrayList<Pair<String, String>>(listColor);

            // set up spinner color picker
            if (colorHex != null) {
                colors.add(0, new Pair("Unchanged", (String) colorIndicatorSelected.getTag()));
            }

            final Spinner spColorPicker = (Spinner) content.findViewById(R.id.sp_color_picker);
            spColorPicker.setAdapter(new ColorSpinnerAdapter(mActivity, R.layout.dialog_content_new_attribute, colors));

            // set up dialog
            String dialogTitle = "Edit Attribute";

            boolean wrapInScrollView = true;
            new MaterialDialog.Builder(mActivity)
                    .title(dialogTitle)
                    .customView(content, wrapInScrollView)
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
            attributes = new ArrayList<NewPollImageReferenceFragment.AttributeLineItem>();
        }

        // insert into main view
        llAttributes.addView(row, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        NewPollImageReferenceFragment.AttributeLineItem line = new NewPollImageReferenceFragment.AttributeLineItem();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

//                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_new_poll_next:

                saveNewPollDetails();

                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Grab details and save into the controller
     *
     * @return false    if question is null or empty
     * true    otherwise
     */
    public boolean saveNewPollDetails() {

        String q = etQuestion.getEditText().getText().toString();
        if (q.equals("")) {
            return false;
        }

        controller.setQuestion(etQuestion.getEditText().getText().toString());
        controller.setTitle(etTitle.getEditText().getText().toString());
        controller.setMultipleResponseAllowed(swMultiple.isChecked());
        controller.setAttributes(grabAttributes());

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        loadNewPollDetails();
    }

    /**
     * Load user input data during navigation
     */
    private void loadNewPollDetails() {
        etQuestion.getEditText().setText(controller.getQuestion());
        etTitle.getEditText().setText(controller.getTitle());
        swMultiple.setChecked(controller.isMultipleResponseAllowed());
        controller.setMultipleResponseAllowed(swMultiple.isChecked());

        List<PollAttribute> attributes = controller.getAttributes();
        if (attributes != null) {
            for (PollAttribute a : attributes) {
                addAttributeLine(a.getAttributeColorHex(), a.getAttributeName());
            }
        }
    }
}
