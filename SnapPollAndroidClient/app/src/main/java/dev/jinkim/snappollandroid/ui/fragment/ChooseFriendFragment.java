package dev.jinkim.snappollandroid.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dev.jinkim.snappollandroid.R;

/**
 * Created by Jin on 2/24/15.
 */
public class ChooseFriendFragment extends Fragment {
    public static final String TAG = "ChooseFriendFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_new_poll, container, false);
        setHasOptionsMenu(true);

        return rootView;
    }



}
