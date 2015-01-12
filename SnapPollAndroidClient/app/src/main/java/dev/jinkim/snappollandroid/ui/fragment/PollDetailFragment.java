package dev.jinkim.snappollandroid.ui.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.Poll;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * Created by Jin on 1/11/15.
 */
public class PollDetailFragment extends Fragment {

    public static final String TAG = "PollDetailFragment ####";

    private ImageView ivPollImage;
    private Poll p;
    private ImageViewTouch mImage;

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
        ivPollImage = (ImageView) v.findViewById(R.id.iv_poll_img);
        mImage = (ImageViewTouch) v.findViewById(R.id.image);

        //TODO: CHECK IF p is null
//        Picasso pic = Picasso.with(getActivity());
//        pic.setIndicatorsEnabled(true);
//        pic.load(p.getReferenceUrl())
//                .resize(1000, 0)
//                .placeholder(R.drawable.ic_img_placeholder)
//                .into(ivPollImage);

        // load bitmap into target
        Picasso.with(getActivity()).load(p.getReferenceUrl()).into(target);


    }

    private void loadImage(Bitmap bitmap) {

        // set the default image display type
        mImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_IF_BIGGER);
        mImage.setImageBitmap(bitmap, null, -1, 8f);
        mImage.setOnDrawableChangedListener(
                new ImageViewTouchBase.OnDrawableChangeListener() {
                    @Override
                    public void onDrawableChanged(final Drawable drawable) {
                        Log.v(TAG, "image scale: " + mImage.getScale() + "/" + mImage.getMinScale());
                        Log.v(TAG, "scale type: " + mImage.getDisplayType() + "/" + mImage.getScaleType());

                    }
                }
        );

//        mImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_IF_BIGGER);
    }


}
