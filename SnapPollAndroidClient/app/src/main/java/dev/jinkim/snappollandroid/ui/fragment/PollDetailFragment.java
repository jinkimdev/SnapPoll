package dev.jinkim.snappollandroid.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.event.ResponseSubmittedEvent;
import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.model.Response;
import dev.jinkim.snappollandroid.ui.activity.PollDetailActivity;
import dev.jinkim.snappollandroid.util.DimensionUtil;
import dev.jinkim.snappollandroid.util.UriUtil;
import dev.jinkim.snappollandroid.util.image.CircleTransform;
import dev.jinkim.snappollandroid.util.image.TouchImageView;
import dev.jinkim.snappollandroid.web.SnapPollRestClient;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by Jin on 1/11/15.
 */
public class PollDetailFragment extends Fragment {

    public static final String TAG = PollDetailFragment.class.getSimpleName();

    private Poll currentPoll;

    private TouchImageView tivRef;
    private ImageView ivProfile;
    private TextView tvCreatorName;
    private TextView tvQuestion;
    private TextView tvNumResponses;
    private TextView lblNumResponses;

    private PollDetailActivity mActivity;

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

        // load selected poll and view result mode
        loadDataFromArguments();

        initializeViewsForResponse(rootView);
//        initializeViewsForResult(rootView);

        if (viewResultMode) {
            loadResponses();
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
    public void onDestroy() {  // could be in onPause or onStop
        Picasso.with(getActivity()).cancelRequest(target);
        super.onDestroy();
    }

    private void loadDataFromArguments() {
        String pollJson = getArguments().getString(Poll.class.getName(), null);
        viewResultMode = getArguments().getBoolean(mActivity.getString(R.string.key_view_result_mode), false);
//        viewResultMode = true;

        Gson gson = new Gson();
        currentPoll = gson.fromJson(pollJson, Poll.class);
    }

    private void initializeViewsForResponse(View v) {
        tivRef = (TouchImageView) v.findViewById(R.id.tiv_ref);
        ivProfile = (ImageView) v.findViewById(R.id.detail_iv_profile_pic);
        tvQuestion = (TextView) v.findViewById(R.id.detail_tv_question);
        tvCreatorName = (TextView) v.findViewById(R.id.detail_tv_creator_name);
        tvNumResponses = (TextView) v.findViewById(R.id.detail_tv_num_responses);
        lblNumResponses = (TextView) v.findViewById(R.id.detail_lbl_num_responses);

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

        // handle when pic url is empty string
        if (currentPoll.getCreatorProfilePicUrl().equals("")) {
            Picasso.with(mActivity).load(R.drawable.ic_placeholder_profile)
                    .into(ivProfile);
        } else {
            Picasso.with(mActivity).load(currentPoll.getCreatorProfilePicUrl())
                    .transform(new CircleTransform()).into(ivProfile);
        }
    }

//    private void initializeViewsForResult(View v) {
//        tivRef = (TouchImageView) v.findViewById(R.id.tiv_ref);
//        ivProfile = (ImageView) v.findViewById(R.id.detail_iv_profile_pic);
//        tvQuestion = (TextView) v.findViewById(R.id.detail_tv_question);
//
//        //TODO: CHECK IF currentPoll is null
//        tvQuestion.setText(currentPoll.getQuestion());
//        // load bitmap into target
//        Picasso.with(mActivity).load(currentPoll.getReferenceUrl()).into(target);
//        Picasso.with(mActivity).load(currentPoll.getCreatorProfilePicUrl()).into(ivProfile);
//    }

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
//        Toast.makeText(mActivity, "Submitting Response!", Toast.LENGTH_SHORT).show();

        Poll p = currentPoll;
        PointF loc = getMarkerLocation();

        if (loc == null) {
            //TODO: error, cannot submit response!
            Log.d(TAG, "Response selection is null");
        }

        //TODO: update attribute choice
        Response currentResponse = new Response(p.pollId, loc.x, loc.y, p.getCreatorId(), -1);

        Log.d(TAG, "Submitting response API call");
        SnapPollRestClient rest = new SnapPollRestClient();
        rest.getApiService().submitResponse(currentResponse, new Callback<Response>() {
            @Override
            public void success(Response pollResponse, retrofit.client.Response response2) {
                Bus bus = mActivity.getEventBus();
                bus.post(new ResponseSubmittedEvent());
                mActivity.finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Response submission failed: " + error);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_poll_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_poll_detail_submit:
                submitResponse();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}