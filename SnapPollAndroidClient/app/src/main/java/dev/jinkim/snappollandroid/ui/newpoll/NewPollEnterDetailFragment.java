package dev.jinkim.snappollandroid.ui.newpoll;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import java.util.ArrayList;
import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.PollAttribute;
import dev.jinkim.snappollandroid.ui.newpoll.NewPollController.AttributeLineItem;
import dev.jinkim.snappollandroid.util.ColorUtil;

/**
 * Created by Jin on 3/6/15.
 *
 * Fragment for entering poll details including question, title, and answer choices
 */
public class NewPollEnterDetailFragment extends Fragment {

    public static String TAG = NewPollEnterDetailFragment.class.getSimpleName();
    private NewPollActivity mActivity;

    private ImageView ivImageBackground;
    private ImageView btnAddAttribute;
    private LinearLayout llAttributesContainer;
    private ListView lvNewPollAttributes;
    private NewPollAttributeAdapter adapter;

    /* poll data */
    private FloatLabeledEditText etQuestion;
    private FloatLabeledEditText etTitle;
    private SwitchCompat swMultiple;

    private List<AttributeLineItem> attributes;
    private boolean showingDialog = false;

    private NewPollController controller;
    private int[] mMarkerColors;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag_new_poll_enter_detail, container, false);

        mActivity = (NewPollActivity) getActivity();
        controller = mActivity.getController();

        mActivity.getSupportActionBar().setTitle(R.string.title_poll_detail);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initializeViews(view);
    }

    private void initializeViews(View v) {
//        progressBar = (ProgressBarIndeterminate) v.findViewById(R.id.pb_create_poll_upload_progress);
        mMarkerColors = ColorUtil.colorChoice(mActivity, R.array.snappoll_response_colors);

        ivImageBackground = (ImageView) v.findViewById(R.id.new_poll_detail_iv_background_image);

        // load selected image from saved URI
        if (mActivity.getSelectedImageUri() != null) {
            Picasso.with(mActivity)
                    .load(mActivity.getSelectedImageUri())
                    .fit().centerCrop()
                    .into(ivImageBackground);
        } else if (mActivity.getCapturedPhotoPath() != null) {
            Picasso.with(mActivity)
                    .load(mActivity.getSelectedImageUri())
                    .fit().centerCrop()
                    .into(ivImageBackground);
        }

        etQuestion = (FloatLabeledEditText) v.findViewById(R.id.et_question);
        etTitle = (FloatLabeledEditText) v.findViewById(R.id.et_title);
        swMultiple = (SwitchCompat) v.findViewById(R.id.sw_multiple);

        adapter = new NewPollAttributeAdapter(mActivity, new ArrayList<PollAttribute>(), getChildFragmentManager());

        adapter.add(new PollAttribute(getString(R.string.lbl_default_attribute_name), getResources().getColor(R.color.attribute_default_marker_color)));

        lvNewPollAttributes = (ListView) v.findViewById(R.id.lv_new_poll_attributes);
        lvNewPollAttributes.setAdapter(adapter);

        btnAddAttribute = (ImageView) v.findViewById(R.id.iv_btn_add_attribute);
        btnAddAttribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
////                showAddAttributeDialog();
//                showColorPicker();

                adapter.add(new PollAttribute("", getResources().getColor(R.color.attribute_default_marker_color)));
                setListViewHeightBasedOnChildren(lvNewPollAttributes);
            }
        });
    }

    private void addEmptyAttributeLine() {
        AttributeLineItem attr = new AttributeLineItem();

        LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = vi.inflate(R.layout.row_poll_detail_attribute_item, null);

        final View colorIndicator = row.findViewById(R.id.iv_poll_detail_attribute_color_indicator);
        final TextView tvAttributeName = (TextView) row.findViewById(R.id.tv_poll_detail_attribute_name);


    }

    /**
     * Populate indicator color in the new attribute line
     *
     * @param colorHex
     * @param attributeName
     */
    private void populateAttributeLine(final String colorHex, final String attributeName) {
        LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = vi.inflate(R.layout.row_poll_detail_attribute_item, null);

        final View colorIndicator = row.findViewById(R.id.iv_poll_detail_attribute_color_indicator);
        final TextView tvAttributeName = (TextView) row.findViewById(R.id.tv_poll_detail_attribute_name);

        updateIndicatorColor(colorIndicator, colorHex);
        tvAttributeName.setText(attributeName);

        final ImageView btnRemoveAttribute = (ImageView) row.findViewById(R.id.iv_new_poll_attribute_remove_button);
        btnRemoveAttribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llAttributesContainer.removeView(row);
                Log.d(TAG, "Attributes: " + llAttributesContainer.getChildCount());
            }
        });

        // add objects to the list
        if (attributes == null) {
            attributes = new ArrayList<>();
        }

        // insert into main view
        llAttributesContainer.addView(row, llAttributesContainer.getChildCount(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        AttributeLineItem line = new AttributeLineItem();
        line.setAttributeColorHex(colorHex);
        line.setAttributeName(attributeName);

        attributes.add(line);
    }

    private void updateIndicatorColor(View v, String colorHex) {
        int color = Color.parseColor(colorHex);

        GradientDrawable gradDrawable = (GradientDrawable) v.getBackground();
        if (gradDrawable != null) {
            gradDrawable.setColor(color);
        }
    }

    public List<PollAttribute> grabAttributes() {
        return adapter.grabAttributes();
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
                populateAttributeLine(a.getAttributeColorHex(), a.getAttributeName());
            }
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {

//        ListAdapter listAdapter = listView.getAdapter();
        if (adapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = adapter.getCount(); i < len; i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
