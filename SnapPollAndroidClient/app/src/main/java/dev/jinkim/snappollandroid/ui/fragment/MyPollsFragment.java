package dev.jinkim.snappollandroid.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.app.App;
import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.model.User;
import dev.jinkim.snappollandroid.ui.adapter.MyPollListAdapter;
import dev.jinkim.snappollandroid.web.SnapPollRestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jin on 11/27/14.
 */
public class MyPollsFragment extends ListFragment {

    public static final String TAG = "MyPollsFragment ####";

    MyPollListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_tab_my_polls, container, false);

        adapter = new MyPollListAdapter(getActivity(), null);
        setListAdapter(adapter);

        retrievePolls();

        return rootView;
    }

    //TODO: retrieve polls + creator first name
    private void retrievePolls() {
        User u = App.getInstance().getCurrentUser(getActivity());

        SnapPollRestClient rest = new SnapPollRestClient();
        rest.getApiService().getMyPolls(u.getEmail(), new Callback<List<Poll>>() {
            @Override
            public void success(List<Poll> polls, Response response) {
                Log.d(TAG, "GET /poll/my/:user_id success.");

                updateList(polls);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "GET /poll/my/:user_id fail.");
            }
        });

    }

    private void updateList(List<Poll> polls) {
        adapter.clear();
        adapter.setPollsWithSections(polls);
        adapter.notifyDataSetChanged();
    }

}
