package dev.jinkim.snappollandroid.ui.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.ui.adapter.PollListAdapter;
import dev.jinkim.snappollandroid.web.SnapPollRestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jin on 11/27/14.
 */
public class RespondFragment extends ListFragment {

    public static final String TAG = "RespondFragment ####";
    PollListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_respond, container, false);

        adapter = new PollListAdapter(getActivity(), new ArrayList<Poll>());
        setListAdapter(adapter);

        Log.d(TAG, "onCreateView, adapter set.");

        retrievePolls();

        return rootView;
    }

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
        adapter.addAll(polls);
        adapter.notifyDataSetChanged();
    }

}
