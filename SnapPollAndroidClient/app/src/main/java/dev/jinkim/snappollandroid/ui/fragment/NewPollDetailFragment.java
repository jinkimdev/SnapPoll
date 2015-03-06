package dev.jinkim.snappollandroid.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dev.jinkim.snappollandroid.R;

/**
 * Created by Jin on 3/6/15.
 */
public class NewPollDetailFragment extends Fragment {

    public static String TAG = "NewPollDetailFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag_new_poll_detail, container, false);

        Log.d(TAG, "In NewPollDetailFragment");

        return rootView;
    }
}
