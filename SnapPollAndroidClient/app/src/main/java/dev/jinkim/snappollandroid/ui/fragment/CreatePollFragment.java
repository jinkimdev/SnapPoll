package dev.jinkim.snappollandroid.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.web.RestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jin on 11/27/14.
 */
public class CreatePollFragment extends Fragment {

    public static final String TAG = "CreatePollFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_create_poll, container, false);

        Button btnTest = (Button) rootView.findViewById(R.id.btn_test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });
        return rootView;
    }

    void test() {
        RestClient rest = new RestClient();
//            rest.getApiService().getPolls(new Callback<List<Poll>>() {
//                @Override
//                public void success(List<Poll> polls, Response response) {
//                    Log.d("####", "Polls: " + polls.get(0).question);
//                    Log.d("####", "Time: " + polls.get(0).pollTimestamp);
//                }
//
//                @Override
//                public void failure(RetrofitError retrofitError) {
//                    // Log error here since request failed
//                    Log.d("####", "" + retrofitError.toString());
//                }
//            });

        rest.getApiService().getPoll(6, new Callback<Poll>() {
            @Override
            public void success(Poll poll, Response response) {
                Log.d("####", "Poll by id: " + poll.question);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
