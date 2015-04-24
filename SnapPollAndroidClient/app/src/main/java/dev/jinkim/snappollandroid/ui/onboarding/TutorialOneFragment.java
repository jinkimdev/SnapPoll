package dev.jinkim.snappollandroid.ui.onboarding;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dev.jinkim.snappollandroid.R;

/**
 * Created by Jin on 4/20/15.
 */
public class TutorialOneFragment extends Fragment {
    public static String TAG = TutorialOneFragment.class.getSimpleName();
    private OnboardingActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_tutorial_one, container, false);
        mActivity = (OnboardingActivity) getActivity();

        setHasOptionsMenu(true);

        return rootView;
    }

}
