package dev.jinkim.snappollandroid.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.model.Response;
import dev.jinkim.snappollandroid.util.DimensionUtil;
import dev.jinkim.snappollandroid.util.image.TouchImageView;

/**
 * Created by Jin on 1/11/15.
 */
public class PollDetailFragment extends Fragment {

    public static final String TAG = "PollDetailFragment ####";

    private Poll currentPoll;

    private TouchImageView tivRef;
    private ImageView ivProfile;
    private TextView tvQuestion;

    private Activity mActivity;

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

        mActivity = getActivity();

        loadPollFromArguments();

        initializeViews(rootView);

        return rootView;
    }


    @Override
    public void onDestroy() {  // could be in onPause or onStop
        Picasso.with(getActivity()).cancelRequest(target);
        super.onDestroy();
    }

    private void loadPollFromArguments() {
        String pollJson = getArguments().getString("Poll", null);
        Gson gson = new Gson();
        currentPoll = gson.fromJson(pollJson, Poll.class);
    }

    private void initializeViews(View v) {
        tivRef = (TouchImageView) v.findViewById(R.id.tiv_ref);
        ivProfile = (ImageView) v.findViewById(R.id.detail_iv_profile_pic);
        tvQuestion = (TextView) v.findViewById(R.id.detail_tv_question);

        //TODO: CHECK IF currentPoll is null
        tvQuestion.setText(currentPoll.getQuestion());
        // load bitmap into target
        Picasso.with(mActivity).load(currentPoll.getReferenceUrl()).into(target);
        Picasso.with(mActivity).load(currentPoll.getCreatorProfilePicUrl()).into(ivProfile);
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

    private Response makeResponse() {
        Poll p = currentPoll;
        PointF loc = getMarkerLocation();

        if (loc == null) {
            //TODO: error, cannot submit response!
            Log.d(TAG, "Response selection is null");
        }

        //TODO: update attribute choice
        Response currentResponse = new Response(p.pollId, loc.x, loc.y, p.getCreatorId(), -1);
        return currentResponse;
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
            case R.id.action_submit:

                Toast.makeText(mActivity, "Submit Response!", Toast.LENGTH_SHORT).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}