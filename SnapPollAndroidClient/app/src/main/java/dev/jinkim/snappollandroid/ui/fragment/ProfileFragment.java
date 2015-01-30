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

import java.util.zip.Inflater;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.app.App;
import dev.jinkim.snappollandroid.event.RevokeGplusAccessEvent;
import dev.jinkim.snappollandroid.model.User;
import dev.jinkim.snappollandroid.ui.activity.MainActivity;
import dev.jinkim.snappollandroid.util.image.CircleTransform;

/**
 * Created by Jin on 11/27/14.
 */
public class ProfileFragment extends Fragment {

    public static final String TAG = "UserProfileFragment ####";

    private ImageView ivProfilePic;
    private TextView tvName;
    private TextView tvEmail;

    private Button btnSignOut;
    private Button btnRevokeAccess;

    private MainActivity mActivity;

//    private GoogleApiClient mGoogleApiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_profile, container, false);
        mActivity = (MainActivity) getActivity();
//        mGoogleApiClient = mActivity.getGoogleApiClient();

        initializeViews(rootView);

        displayUserInfo();

        setHasOptionsMenu(true);

        return rootView;
    }

    private void initializeViews(View v) {
        ivProfilePic = (ImageView) v.findViewById(R.id.iv_profile_pic);
        tvName = (TextView) v.findViewById(R.id.tv_name);
        tvEmail = (TextView) v.findViewById(R.id.tv_email);
        btnSignOut = (Button) v.findViewById(R.id.profile_btn_google_sign_out);
        btnRevokeAccess = (Button) v.findViewById(R.id.profile_btn_google_revoke_access);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.signOutFromGplus();
            }
        });

        btnRevokeAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.revokeGoogleAccess();
            }
        });
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
                .into(ivProfilePic);
    }

    @Subscribe
    public void onRevokeGoogleAccess(RevokeGplusAccessEvent event) {
        Log.d(TAG, "Received RevokeGplusAccessEvent");
        Toast.makeText(mActivity, "Revoked Google+ access.", Toast.LENGTH_SHORT).show();

//        updateUI(false);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        // hide the main actionbar menu
        menu.setGroupVisible(R.id.main_menu_group, false);
    }

}
