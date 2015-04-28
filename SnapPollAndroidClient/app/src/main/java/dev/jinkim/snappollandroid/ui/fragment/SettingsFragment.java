package dev.jinkim.snappollandroid.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.Switch;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.session.SessionManager;
import dev.jinkim.snappollandroid.ui.activity.MainActivity;

/**
 * Created by Jin on 4/20/15.
 * <p/>
 * SettingsFragment in Navigation Drawer - User can check and manage app settings.
 */
public class SettingsFragment extends Fragment {
    public static String TAG = SettingsFragment.class.getSimpleName();

    private MainActivity mActivity;
    private SessionManager session;

    private View rootView;

    private Switch swShowTutorial;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_settings, container, false);
        this.rootView = rootView;

        mActivity = (MainActivity) getActivity();

        session = mActivity.getAppSession();

        setHasOptionsMenu(true);

        initializeViews(rootView);

        mActivity.setToolbarTitle(R.string.title_settings);

        return rootView;
    }

    private void initializeViews(View v) {
        swShowTutorial = ((Switch) v.findViewById(R.id.sw_settings_show_tutorial));
        swShowTutorial.setOncheckListener(new Switch.OnCheckListener() {
            @Override
            public void onCheck(boolean b) {
                // if checked, clear the bit so the onboarding screen will appear next launch
                session.setOnboardingViewed(!b);
            }
        });
        swShowTutorial.setChecked(!session.isOnboardingViewed());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        // hide the main actionbar menu
        menu.setGroupVisible(R.id.main_poll_list_menu_group, false);
    }

}
