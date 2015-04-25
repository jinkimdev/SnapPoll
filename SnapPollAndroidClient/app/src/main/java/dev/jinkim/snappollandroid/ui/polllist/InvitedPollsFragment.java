package dev.jinkim.snappollandroid.ui.polllist;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.app.App;
import dev.jinkim.snappollandroid.event.BusProvider;
import dev.jinkim.snappollandroid.event.ResponseSubmittedEvent;
import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.model.User;
import dev.jinkim.snappollandroid.ui.activity.MainActivity;
import dev.jinkim.snappollandroid.ui.polldetail.PollDetailActivity;
import dev.jinkim.snappollandroid.web.SnapPollRestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jin on 11/27/14.
 */
public class InvitedPollsFragment extends ListFragment {

    public static final String TAG = InvitedPollsFragment.class.getSimpleName();
    private InvitedPollListAdapter adapter;
    private ListView lvPolls;
    private MainActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_tab_invited_polls, container, false);

        mActivity = (MainActivity) getActivity();
        mActivity.showProgressBar(R.string.msg_loading);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        initializeListView();
    }

    private void initializeListView() {
        adapter = new InvitedPollListAdapter(mActivity, new ArrayList<Poll>());
        setListAdapter(adapter);
        lvPolls = getListView();
        lvPolls.setDivider(null);
        lvPolls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Poll p = (Poll) parent.getAdapter().getItem(position);
                Log.d(TAG, "Poll selected: " + p.getCreatorId() + " - " + p.getQuestion());

                Intent in = new Intent(mActivity, PollDetailActivity.class);
                Gson gson = new Gson();
                String pollJson = gson.toJson(p);
                in.putExtra(getString(R.string.key_poll), pollJson);
                in.putExtra(getString(R.string.key_view_result_mode), false);

                startActivity(in);
            }
        });
    }


    public void retrievePolls() {
        User u = App.getInstance().getCurrentUser(mActivity);

        if (u != null) {
            SnapPollRestClient rest = new SnapPollRestClient();
            rest.getApiService().getInvitedPolls(u.getUserId(), new Callback<List<Poll>>() {
                @Override
                public void success(List<Poll> polls, Response response) {
                    Log.d(TAG, "GET /poll/invited success.");
                    updateList(polls);
                    mActivity.setInvitedPolls(polls);
                    mActivity.hideProgressBar();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(TAG, "GET /poll/invited failed.");
                    mActivity.hideProgressBar();
                }
            });
        } else {
            Toast.makeText(mActivity, R.string.msg_sign_in_to_see_polls, Toast.LENGTH_SHORT).show();
        }

    }

    private void updateList(List<Poll> polls) {
        adapter.clear();
        adapter.setPollsWithSections(polls);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }

        mActivity.setCurrentFragment(this);
    }


    public void onStart() {
        super.onStart();
        mActivity.showProgressBar(R.string.msg_loading);
        retrievePolls();

    }

}
