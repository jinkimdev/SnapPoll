package dev.jinkim.snappollandroid.ui.fragment;

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

import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.app.App;
import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.model.User;
import dev.jinkim.snappollandroid.ui.activity.MainActivity;
import dev.jinkim.snappollandroid.ui.polldetail.PollDetailActivity;
import dev.jinkim.snappollandroid.ui.adapter.MyPollListAdapter;
import dev.jinkim.snappollandroid.web.SnapPollRestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jin on 11/27/14.
 */
public class MyPollsFragment extends ListFragment {

    public static final String TAG = MyPollsFragment.class.getSimpleName();

    private MyPollListAdapter adapter;
    private ListView lvMyPolls;
    private MainActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_tab_my_polls, container, false);
        mActivity = (MainActivity) getActivity();
        mActivity.showProgressBar(R.string.msg_loading);

        adapter = new MyPollListAdapter(getActivity(), null);
        setListAdapter(adapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initializeViews();

        // get cached poll list
        List<Poll> myPolls = mActivity.getMyPolls();
        if (myPolls == null || myPolls.size() < 1) {
            retrieveMyPolls();
        } else {
            // if poll list has been already retrieved, update the list with them
            updateList(myPolls);
            mActivity.hideProgressBar();
        }
    }

    private void initializeViews() {
        lvMyPolls = getListView();
        lvMyPolls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Poll p = adapter.getItem(position);
                Log.d(TAG, "Poll selected: " + p.getCreatorId() + " - " + p.getQuestion());

                Intent in = new Intent(getActivity(), PollDetailActivity.class);
                Gson gson = new Gson();
                String pollJson = gson.toJson(p);
                in.putExtra(getString(R.string.key_poll), pollJson);
                in.putExtra(getString(R.string.key_view_result_mode), true);

                startActivity(in);
            }
        });
    }

    //TODO: retrieve polls + creator first name
    private void retrieveMyPolls() {
        User u = App.getInstance().getCurrentUser(getActivity());

        if (u != null) {
            SnapPollRestClient rest = new SnapPollRestClient();
            rest.getApiService().getMyPolls(u.getUserId(), new Callback<List<Poll>>() {
                @Override
                public void success(List<Poll> polls, Response response) {
                    Log.d(TAG, "GET /poll/my/:user_id success.");
                    updateList(polls);
                    mActivity.setMyPolls(polls);
                    mActivity.hideProgressBar();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(TAG, "GET /poll/my/:user_id fail.");
                    mActivity.hideProgressBar();
                }
            });
        } else {
            Toast.makeText(getActivity(), R.string.msg_please_sign_in_to_see_polls, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateList(List<Poll> polls) {
        adapter.clear();
        adapter.setPollsWithSections(polls);
        adapter.notifyDataSetChanged();
    }

}
