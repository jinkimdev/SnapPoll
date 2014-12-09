package dev.jinkim.snappollandroid.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.app.App;
import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.model.User;
import dev.jinkim.snappollandroid.session.SessionManager;
import dev.jinkim.snappollandroid.ui.adapter.MyPollAdapter;
import dev.jinkim.snappollandroid.util.image.CircleTransform;
import dev.jinkim.snappollandroid.web.SnapPollRestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jin on 11/27/14.
 */
public class UserProfileFragment extends ListFragment {

    public static final String TAG = "UserProfileFragment ####";

    private SessionManager session;

    private ImageView ivProfilePic;
    private TextView tvName;
    private TextView tvEmail;

    MyPollAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_user_profile, container, false);
        session = new SessionManager(getActivity());

        adapter = new MyPollAdapter(getActivity(), null);
        setListAdapter(adapter);

        initializeViews(rootView);

        displayUserInfo();
        retrievePolls();

        return rootView;
    }

    //TODO: retrieve polls + creator first name
    private void retrievePolls() {
        SnapPollRestClient rest = new SnapPollRestClient();
        rest.getApiService().getPolls(new Callback<List<Poll>>() {
            @Override
            public void success(List<Poll> polls, Response response) {
                Log.d(TAG, "GET /poll success.");
                updateList(polls);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "GET /poll failed.");

            }
        });

    }

    private void updateList(List<Poll> polls) {
        adapter.clear();
        adapter.setPollsWithSections(polls);
        adapter.notifyDataSetChanged();
    }

    private void displayUserInfo() {
        User user = App.getInstance().getCurrentUser(getActivity());
        if (user != null) {
            updateProfilePic(user.getPhotoUrl());
            tvName.setText(user.getFullName());
            tvEmail.setText(user.getEmail());
        }
    }

    private void initializeViews(View v) {
        ivProfilePic = (ImageView) v.findViewById(R.id.iv_profile_pic);
        tvName = (TextView) v.findViewById(R.id.tv_name);
        tvEmail = (TextView) v.findViewById(R.id.tv_email);
    }


    private void updateProfilePic(String photoUrl) {
        Picasso.with(getActivity())
                .load(photoUrl)
                .transform(new CircleTransform())
                .into(ivProfilePic);
    }

}
