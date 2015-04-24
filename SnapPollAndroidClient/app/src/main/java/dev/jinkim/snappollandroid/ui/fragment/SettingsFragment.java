package dev.jinkim.snappollandroid.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.ui.activity.MainActivity;

/**
 * Created by Jin on 4/20/15.
 */
public class SettingsFragment extends Fragment {
    public static String TAG = SettingsFragment.class.getSimpleName();

    private MainActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_settings, container, false);
        mActivity = (MainActivity) getActivity();

        setHasOptionsMenu(true);

        return rootView;
    }

}
