package dev.jinkim.snappollandroid.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.util.DimensionUtil;
import dev.jinkim.snappollandroid.util.image.TouchImageView;

/**
 * Created by Jin on 1/11/15.
 */
public class PollDetailFragment extends Fragment {

    public static final String TAG = "PollDetailFragment ####";

    private Poll p;
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
        p = gson.fromJson(pollJson, Poll.class);
    }

    private void initializeViews(View v) {
        tivRef = (TouchImageView) v.findViewById(R.id.tiv_ref);
        ivProfile = (ImageView) v.findViewById(R.id.detail_iv_profile_pic);
        tvQuestion = (TextView) v.findViewById(R.id.detail_tv_question);

        //TODO: CHECK IF p is null
        tvQuestion.setText(p.getQuestion());
        // load bitmap into target
        Picasso.with(mActivity).load(p.getReferenceUrl()).into(target);
        Picasso.with(mActivity).load(p.getCreatorProfilePicUrl()).into(ivProfile);
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
}