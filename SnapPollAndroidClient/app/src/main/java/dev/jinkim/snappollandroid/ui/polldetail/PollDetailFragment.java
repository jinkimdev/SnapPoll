package dev.jinkim.snappollandroid.ui.polldetail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.app.App;
import dev.jinkim.snappollandroid.event.ResponseSubmittedEvent;
import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.model.PollAttribute;
import dev.jinkim.snappollandroid.model.PollInvitedFriendsResponse;
import dev.jinkim.snappollandroid.model.Response;
import dev.jinkim.snappollandroid.model.RowFriend;
import dev.jinkim.snappollandroid.model.User;
import dev.jinkim.snappollandroid.ui.invite.InviteFriendsController;
import dev.jinkim.snappollandroid.ui.invite.InviteFriendsDialog;
import dev.jinkim.snappollandroid.ui.widget.TouchImageView;
import dev.jinkim.snappollandroid.util.DimensionUtil;
import dev.jinkim.snappollandroid.util.UriUtil;
import dev.jinkim.snappollandroid.util.image.CircleTransform;
import dev.jinkim.snappollandroid.web.SnapPollRestClient;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by Jin on 1/11/15.
 */
public class PollDetailFragment extends Fragment {

    public static final String TAG = PollDetailFragment.class.getSimpleName();

    private PollDetailActivity mActivity;
    private InviteFriendsController inviteController;
    private GoogleApiClient mGoogleApiClient;

    private InviteFriendsDialog dialog;

    private Poll currentPoll;
    private PollAttribute selectedAttr;

    private TouchImageView tivRef;
    private ImageView ivProfile;
    private TextView tvCreatorName;
    private TextView tvQuestion;
    private TextView tvNumResponses;
    private LinearLayout llAttributeContainer;
    private List<RadioButton> radioButtons;

    private TextView lblNumResponses;
    private SlidingUpPanelLayout slidingUpPanel;

    private ImageView ivExpand;

    // true for viewing the poll result, false for responding to a poll
    private boolean viewResultMode = false;

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            loadImage(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_poll_detail, container, false);
        setHasOptionsMenu(true);

        mActivity = (PollDetailActivity) getActivity();

        inviteController = new InviteFriendsController(mActivity);
        mGoogleApiClient = mActivity.getGoogleApiClient();

        // load selected poll and view result mode
        loadDataFromArguments();

        inviteController.setPollId(currentPoll.getPollId());

        initializeViews(rootView);

        // determine whether viewing invited poll to respond or the result of my poll
        if (viewResultMode) {
            loadResponses();
            mActivity.getSupportActionBar().setTitle(R.string.title_view_my_poll_result);
        } else {
            mActivity.getSupportActionBar().setTitle(R.string.title_submit_response);
        }

        return rootView;
    }

    private void loadResponses() {
        SnapPollRestClient.ApiService rest = new SnapPollRestClient().getApiService();
        rest.getPollResponses(currentPoll.getPollId(), new Callback<List<Response>>() {
            @Override
            public void success(List<Response> responses, retrofit.client.Response response) {
                tvNumResponses.setText(String.valueOf(responses.size()));
                tivRef.setResponses(responses);
                tivRef.invalidate();

                Log.d(TAG, "Responses retrieved: " + responses.size());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // expanded as default when view is created
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                slidingUpPanel.expandPanel();
                ivExpand.setImageResource(R.drawable.ic_arrow_down);
            }
        }, 700);
    }

    @Override
    public void onDestroy() {  // could be in onPause or onStop
        Picasso.with(getActivity()).cancelRequest(target);
        super.onDestroy();
    }

    private void loadDataFromArguments() {
        Bundle bundle = getArguments();
        String pollJson = bundle.getString(getString(R.string.key_poll), null);
        viewResultMode = bundle.getBoolean(getString(R.string.key_view_result_mode), false);
        Boolean showUploadedMessage = bundle.getBoolean(getString(R.string.key_show_poll_created_msg), false);

        if (showUploadedMessage) {
            mActivity.displaySnackBar(getString(R.string.msg_poll_created));
        }

        if (pollJson == null || pollJson.equals("")) {
            // Error
            Toast.makeText(mActivity, R.string.msg_poll_invalid, Toast.LENGTH_SHORT).show();
        }
        Gson gson = new Gson();
        currentPoll = gson.fromJson(pollJson, Poll.class);
    }

    private void initializeViews(View v) {
        tivRef = (TouchImageView) v.findViewById(R.id.tiv_ref);
        ivProfile = (ImageView) v.findViewById(R.id.detail_iv_profile_pic);
        tvQuestion = (TextView) v.findViewById(R.id.detail_tv_question);
        tvCreatorName = (TextView) v.findViewById(R.id.detail_tv_creator_name);
        tvNumResponses = (TextView) v.findViewById(R.id.detail_tv_num_responses);
        lblNumResponses = (TextView) v.findViewById(R.id.detail_lbl_num_responses);
        slidingUpPanel = (SlidingUpPanelLayout) v.findViewById(R.id.sliding_layout);
        ivExpand = (ImageView) v.findViewById(R.id.detail_iv_panel_expand_indicator);
        llAttributeContainer = (LinearLayout) v.findViewById(R.id.ll_container_attributes);

        if (viewResultMode) {
            tivRef.setSelectorEnabled(false);
            tvNumResponses.setVisibility(View.VISIBLE);
            lblNumResponses.setVisibility(View.VISIBLE);
            tvNumResponses.setText("1");
            tvCreatorName.setVisibility(View.INVISIBLE);
        } else {
            tivRef.setSelectorEnabled(true);
            tvCreatorName.setVisibility(View.VISIBLE);
            tvCreatorName.setText(currentPoll.getCreatorFirstName() + " " + currentPoll.getCreatorLastName());
            tvNumResponses.setVisibility(View.INVISIBLE);
        }

        //TODO: CHECK IF currentPoll is null

        tvQuestion.setText(currentPoll.getQuestion());
        // load bitmap into target
        UriUtil util = new UriUtil();
        String imgUrl = util.convertImgurThumbnail(currentPoll.getReferenceUrl(), 'h');
        Picasso.with(mActivity).load(imgUrl).into(target);

        if (viewResultMode) {
            // if my poll, display my profile pic
            User user = App.getInstance().getCurrentUser(getActivity());
            Picasso.with(mActivity).load(user.getProfilePicUrl())
                    .transform(new CircleTransform()).into(ivProfile);

        } else { // else display poll creator's profile pic
            // handle when pic url is empty string
            if (currentPoll.getCreatorProfilePicUrl().equals("")) {
                Picasso.with(mActivity).load(R.drawable.ic_placeholder_profile)
                        .into(ivProfile);
            } else {
                Picasso.with(mActivity).load(currentPoll.getCreatorProfilePicUrl())
                        .transform(new CircleTransform()).into(ivProfile);
            }
        }


        // attach listener to sliding up panel
        slidingUpPanel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
            }

            @Override
            public void onPanelCollapsed(View view) {
                ivExpand.setImageResource(R.drawable.ic_arrow_up);
            }

            @Override
            public void onPanelExpanded(View view) {
                ivExpand.setImageResource(R.drawable.ic_arrow_down);

            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        });

        if (currentPoll.getAttributes() != null) {
            displayAttributes(llAttributeContainer, currentPoll.getAttributes());
            RelativeLayout rlAttributeDefault = (RelativeLayout) v.findViewById(R.id.detail_attribute_default);
            rlAttributeDefault.setVisibility(View.GONE);
        }

        // if view result mode, hide all action items in attribute line item view
    }

    private void displayAttributes(LinearLayout attributeContainer, List<PollAttribute> attributes) {

        radioButtons = new ArrayList<>();

        for (PollAttribute attr : attributes) {
            LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = vi.inflate(R.layout.row_poll_attribute_line_item, null);

            final RadioButton rbAttributeSelect = (RadioButton) row.findViewById(R.id.rb_attribute);
            final View colorIndicator = row.findViewById(R.id.view_attribute_line_color_indicator);
            final TextView tvAttributeName = (TextView) row.findViewById(R.id.tv_attribute_line_attribute_name);

            try {
                colorIndicator.setBackgroundColor(Color.parseColor(attr.getAttributeColorHex()));
            } catch (NullPointerException e) {
                colorIndicator.setBackgroundColor(mActivity.getResources().getColor(R.color.app_primary));
            }
            tvAttributeName.setText(attr.getAttributeName());

            // save the attribute object as radio button view's tag so we can retrieve color to update
            // when this radio button is selected
            rbAttributeSelect.setTag(attr);

            radioButtons.add(rbAttributeSelect);

            // if respond mode, display radio button selection for attributes
            if (!viewResultMode) {
                rbAttributeSelect.setVisibility(View.VISIBLE);
                rbAttributeSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //TODO: Update existing response marker

                        if (isChecked) {
                            for (RadioButton btn : radioButtons) {
                                btn.setChecked(false);
                            }
                            buttonView.setChecked(true);

                            // load attribute associated with this radio button
                            PollAttribute attr = (PollAttribute) buttonView.getTag();
                            String colorHex;
                            if (attr == null) {
                                colorHex = getString(R.string.color_default_marker);
                            } else {
                                colorHex = attr.getAttributeColorHex();
                            }

                            // check if valid color hex string
                            try {
                                Color.parseColor(colorHex);
                            } catch (NullPointerException e) {
                                colorHex = getString(R.string.color_default_marker);
                            }
                            tivRef.updateResponseMarkerColor(colorHex);
                            selectedAttr = attr;
                        }
                    }
                });
            }
            attributeContainer.addView(row, attributeContainer.getChildCount(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private void loadImage(Bitmap bitmap) {

        final int imgWidth = bitmap.getWidth();
        final int imgHeight = bitmap.getHeight();

        tivRef.setImageBitmap(bitmap);

        ViewTreeObserver viewTreeObserver = tivRef.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        tivRef.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        tivRef.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                    // image view width and height within the content container (below action bar)
                    float contentWid = tivRef.getWidth();
                    float contentHt = tivRef.getHeight();

                    DimensionUtil screen = new DimensionUtil(getActivity());

                    float centerZoomRatio = screen.getCenterZoomRatio(contentWid, contentHt, imgWidth, imgHeight);

                    tivRef.setZoom(centerZoomRatio);
                }
            });
        }
    }

    private PointF getMarkerLocation() {
        return tivRef.getMarkerLocation();
    }

    private void submitResponse() {
        mActivity.showProgressBar(R.string.msg_submitting);

        PointF loc = getMarkerLocation();
        if (loc == null) {
            //TODO: error, cannot submit response!
            Log.d(TAG, "Response selection is null");
            Toast.makeText(mActivity,
                    R.string.msg_response_not_indicated, Toast.LENGTH_LONG).show();
            return;
        }

        //TODO: update attribute choice

        // grab selected attribute id, -1 if null
        int attrId = (selectedAttr != null) ? selectedAttr.getAttributeId() : -1;

        Response currentResponse = new Response(currentPoll.getPollId(),
                loc.x, loc.y, currentPoll.getCreatorId(), attrId);

        Log.d(TAG, "Submitting response API call");
        SnapPollRestClient rest = new SnapPollRestClient();
        rest.getApiService().submitResponse(currentResponse, new Callback<Response>() {
            @Override
            public void success(Response pollResponse, retrofit.client.Response response2) {
                Bus bus = mActivity.getEventBus();
                bus.post(new ResponseSubmittedEvent());

                mActivity.hideProgressBar();
                mActivity.finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Response submission failed: " + error);
                mActivity.hideProgressBar();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.clear();
        inflater.inflate(R.menu.menu_poll_detail, menu);

        MenuItem menuInvite = menu.findItem(R.id.action_poll_detail_invite);
        MenuItem menuSubmit = menu.findItem(R.id.action_poll_detail_submit);

        if (viewResultMode) {
            menuInvite.setVisible(true);
            menuSubmit.setVisible(false);
        } else {
            menuInvite.setVisible(false);
            menuSubmit.setVisible(true);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_poll_detail_submit:
                submitResponse();
                return true;

            case R.id.action_poll_detail_invite:
                retrievePollInvitees();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void retrievePollInvitees() {
        List<String> inviteeIds = inviteController.getPollInviteeIds();
        if (inviteeIds == null || inviteeIds.size() < 1) {
            SnapPollRestClient.ApiService rest = new SnapPollRestClient().getApiService();
            rest.getPollInvitedFriends(inviteController.getPollId(), new Callback<PollInvitedFriendsResponse>() {
                @Override
                public void success(PollInvitedFriendsResponse pollInvitedFriendsResponse, retrofit.client.Response response) {

                    List<String> invitees;

                    if (pollInvitedFriendsResponse == null || pollInvitedFriendsResponse.friends == null) {
                        invitees = new ArrayList<>();
                    } else {
                        invitees = pollInvitedFriendsResponse.friends;
                    }
                    inviteController.setPollInviteeIds(invitees);
                    retrieveFriendsFromGPlus();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(TAG, "Failed in retrieving existing invitees.");
                    retrieveFriendsFromGPlus();
                }
            });
        } else {
            showInviteFriendsDialog(inviteController.getgPlusFriends());
        }
        return;
    }

    private void updateExistingInvitees(List<String> inviteeIds) {
        if (dialog == null) {
            return;
        }

        dialog.updateExistingInvitees(inviteeIds);
    }

    /**
     * If GoogleApiClient is connected, make a call to get friends list
     * <p/>
     * List of Google+ Person model
     * <p/>
     * https://developer.android.com/reference/com/google/android/gms/plus/model/people/Person.html
     */
    private void retrieveFriendsFromGPlus() {

        if (mGoogleApiClient.isConnected()) {

            Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
                    .setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
                        @Override
                        public void onResult(People.LoadPeopleResult loadPeopleResult) {
                            if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
                                PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();

                                // add the result into list of RowFriend
                                List<RowFriend> gPlusFriends = new ArrayList<RowFriend>();
                                try {
                                    int count = personBuffer.getCount();
                                    for (int i = 0; i < count; i++) {
                                        if (personBuffer.get(i) != null) {
                                            Log.d(TAG, "Display name: " + personBuffer.get(i).getDisplayName());
                                            RowFriend friend = new RowFriend(personBuffer.get(i).freeze());
                                            gPlusFriends.add(friend);
                                        }
                                    }
                                } finally {
                                    personBuffer.close();

                                    gPlusFriends = updateFriendsWithInvitees(gPlusFriends);
                                    inviteController.setgPlusFriends(gPlusFriends);
                                    showInviteFriendsDialog(gPlusFriends);
                                }
                            } else {
                                Log.e(TAG, "Error requesting visible circles: " + loadPeopleResult.getStatus());
                            }
                        }
                    });
        }
    }

    /**
     * With the retrieved invitee Ids, check the selected flag in G+ friends list
     * to display in the dialog
     *
     * @param gPlusFriends
     * @return
     */
    private List<RowFriend> updateFriendsWithInvitees(List<RowFriend> gPlusFriends) {
        for (String id : inviteController.getPollInviteeIds()) {
            for (RowFriend r : gPlusFriends) {
                if (id.equals(r.person.getId())) {
                    r.selected = true;
                }
            }
        }
        return gPlusFriends;
    }

    private void showInviteFriendsDialog(List<RowFriend> retrievedFriends) {
        dialog = new InviteFriendsDialog(mActivity, inviteController, retrievedFriends);
        dialog.showDialog();
    }
}