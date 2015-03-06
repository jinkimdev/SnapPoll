package dev.jinkim.snappollandroid.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.ui.activity.NewPollActivity;

/**
 * Created by Jin on 3/6/15.
 */
public class NewPollImageFragment extends Fragment {
    public static final String TAG = "CreatePollFragment";

    private View rootView;
    private NewPollActivity mActivity;

    private ImageView ivImage;
    private Uri selectedImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_new_poll_image, container, false);
        setHasOptionsMenu(true);

        mActivity = (NewPollActivity) getActivity();

        initializeViews(rootView);
//        mActivity.invalidateOptionsMenu();

        return rootView;
    }

    private void initializeViews(View view) {
        ivImage = (ImageView) view.findViewById(R.id.new_poll_image_iv_thumbnail);

        if (selectedImageUri != null) {

            Picasso.with(getActivity())
                    .load(selectedImageUri)
                    .fit().centerCrop()
                    .into(ivImage);
        } else {
            Picasso.with(getActivity())
                    .load(R.drawable.ic_placeholder_image)
                    .fit().centerCrop()
                    .into(ivImage);
        }
    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//
//        menu.clear();
//        inflater.inflate(R.menu.menu_new_poll, menu);
//
//        MenuItem item = menu.findItem(R.id.action_poll_detail_submit);
//        item.setVisible(false);
//    }
}