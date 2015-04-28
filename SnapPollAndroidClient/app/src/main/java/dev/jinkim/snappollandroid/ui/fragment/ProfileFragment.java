package dev.jinkim.snappollandroid.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.app.App;
import dev.jinkim.snappollandroid.event.RevokeGplusAccessEvent;
import dev.jinkim.snappollandroid.model.User;
import dev.jinkim.snappollandroid.session.SessionManager;
import dev.jinkim.snappollandroid.ui.activity.MainActivity;
import dev.jinkim.snappollandroid.util.image.CircleTransform;

/**
 * Created by Jin on 11/27/14.
 *
 * Profile Fragment in Navigation Drawer - User can see the status of app login with Google + and Facebook
 */
public class ProfileFragment extends Fragment {

    public static final String TAG = ProfileFragment.class.getSimpleName();

    private ImageView ivProfilePic;
    private TextView tvName, tvEmail;
    private TextView tvStatusGPlus, tvStatusFacebook;

    private Button btnSignOutGoogle;
    private Button btnSignOutFacebook;

    private Button btnRevokeAccess;

    private MainActivity mActivity;
    private SessionManager session;

//    private GoogleApiClient mGoogleApiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_profile, container, false);
        mActivity = (MainActivity) getActivity();
//        mGoogleApiClient = mActivity.getGoogleApiClient();
        session = mActivity.getAppSession();
        mActivity.setToolbarTitle(R.string.title_profile);

        initializeViews(rootView);

        displayUserInfo();

        setHasOptionsMenu(true);

        return rootView;
    }

    private void initializeViews(View v) {
        ivProfilePic = (ImageView) v.findViewById(R.id.iv_profile_pic);
        tvName = (TextView) v.findViewById(R.id.tv_name);
        tvEmail = (TextView) v.findViewById(R.id.tv_email);
        btnSignOutGoogle = (Button) v.findViewById(R.id.profile_btn_signout_google);
        btnSignOutGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.signOutFromGplus();
            }
        });

        btnSignOutFacebook = (Button) v.findViewById(R.id.profile_btn_signout_facebook);
        btnSignOutFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.signOutFromFacebook();
            }
        });

        btnRevokeAccess = (Button) v.findViewById(R.id.profile_btn_google_revoke_access);
        btnRevokeAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.revokeGoogleAccess();
            }
        });

        /* sign in status */
        tvStatusGPlus = (TextView) v.findViewById(R.id.profile_tv_status_googleplus);
        if (!session.isSignedIntoGPlus()) {
            tvStatusGPlus.setText(mActivity.getString(R.string.status_not_signed_in));
//            tvStatusGPlus.setTextColor(Color.parseColor("#555555"));
            tvStatusGPlus.setTextColor(getResources().getColor(R.color.text_primary));

            //TODO: SHOW G+ SIGN IN BUTTON

            btnSignOutGoogle.setEnabled(false);
            btnRevokeAccess.setEnabled(false);
        }

        tvStatusFacebook = (TextView) v.findViewById(R.id.profile_tv_status_facebook);
        if (!session.isSignedIntoFacebook()) {
            tvStatusFacebook.setText(mActivity.getString(R.string.status_not_signed_in));
            tvStatusFacebook.setTextColor(getResources().getColor(R.color.text_primary));

            //TODO: SHOW FB SIGN IN BUTTON

            btnSignOutFacebook.setEnabled(false);
        }
    }

    private void displayUserInfo() {
        User user = App.getInstance().getCurrentUser(getActivity());
        if (user != null) {
            tvName.setText(user.getFullName());
            tvEmail.setText(user.getUserId());
            updateProfilePic(user.getProfilePicUrl());
        }
    }

    private void updateProfilePic(String photoUrl) {
        Picasso.with(getActivity())
                .load(photoUrl)
                .transform(new CircleTransform())
                .placeholder(R.drawable.ic_placeholder_profile)
                .into(ivProfilePic);
    }

    @Subscribe
    public void onRevokeGoogleAccess(RevokeGplusAccessEvent event) {
        Log.d(TAG, "Received RevokeGplusAccessEvent");
        Toast.makeText(mActivity, R.string.msg_revoked_gplus_access, Toast.LENGTH_SHORT).show();

//        updateUI(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        // hide the main actionbar menu
        menu.setGroupVisible(R.id.main_poll_list_menu_group, false);
    }

}
