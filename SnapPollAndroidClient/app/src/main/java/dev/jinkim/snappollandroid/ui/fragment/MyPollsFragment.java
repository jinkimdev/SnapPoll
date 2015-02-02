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
import dev.jinkim.snappollandroid.ui.activity.PollDetailActivity;
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

    private MyPollListAdapter adapter;
    private ListView lvMyPolls;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_tab_my_polls, container, false);

        adapter = new MyPollListAdapter(getActivity(), null);
        setListAdapter(adapter);

        retrievePolls();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initializeViews();
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
                in.putExtra("Poll", pollJson);
                in.putExtra("ViewResultMode", true);

                startActivity(in);
            }
        });
    }

    //TODO: retrieve polls + creator first name
    private void retrievePolls() {
        User u = App.getInstance().getCurrentUser(getActivity());

        if (u != null) {
            SnapPollRestClient rest = new SnapPollRestClient();
            rest.getApiService().getMyPolls(u.getUserId(), new Callback<List<Poll>>() {
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
        } else {
            Toast.makeText(getActivity(), "Please sign in to see your polls", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateList(List<Poll> polls) {
        adapter.clear();
        adapter.setPollsWithSections(polls);
        adapter.notifyDataSetChanged();
    }

}
