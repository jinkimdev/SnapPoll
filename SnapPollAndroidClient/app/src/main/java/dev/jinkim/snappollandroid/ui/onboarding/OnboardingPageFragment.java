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
public class OnboardingPageFragment extends Fragment {
    public static String TAG = OnboardingPageFragment.class.getSimpleName();

    private int page = 0;
    private static final String KEY_PAGE = "onboarding_page";

    public OnboardingPageFragment() {
    }

    public static OnboardingPageFragment newInstance(int page) {

        OnboardingPageFragment fragment = new OnboardingPageFragment();

        Bundle args = new Bundle();
        args.putInt(KEY_PAGE, page);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        page = getArguments().getInt(KEY_PAGE);
        int tutorialPageResId;

        switch (page) {

            case 0:
                tutorialPageResId = R.layout.frag_onboarding_page0;
                break;

            case 1:
                tutorialPageResId = R.layout.frag_onboarding_page1;
                break;

            case 2:
                tutorialPageResId = R.layout.frag_onboarding_page2;
                break;

            case 3:
                tutorialPageResId = R.layout.frag_onboarding_page3;
                break;

            case 4:
                tutorialPageResId = R.layout.frag_onboarding_page4;
                break;

            case 5:
                tutorialPageResId = R.layout.frag_onboarding_page5;
                break;

            default:
                tutorialPageResId = R.layout.frag_onboarding_page1;
                break;
        }

        View rootView = inflater.inflate(tutorialPageResId, container, false);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            page = getArguments().getInt(getString(R.string.key_onboarding_page));
        }
    }

}
