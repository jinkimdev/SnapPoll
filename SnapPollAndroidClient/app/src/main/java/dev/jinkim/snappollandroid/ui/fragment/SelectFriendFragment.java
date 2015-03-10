package dev.jinkim.snappollandroid.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.facebook.Session;

import java.util.ArrayList;
import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.FacebookFriendListItem;
import dev.jinkim.snappollandroid.ui.adapter.FacebookFriendListAdapter;

/**
 * Created by Jin on 2/24/15.
 */
public class SelectFriendFragment extends Fragment {
    public static final String TAG = "ChooseFriendFragment";

    private ListView listView;
    private List<FacebookFriendListItem> listFriends;

    private FacebookFriendListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_new_poll_friends, container, false);
        setHasOptionsMenu(true);

        listView = (ListView) rootView.findViewById(R.id.lv_friend_selection);
        listFriends = new ArrayList<FacebookFriendListItem>();
        listFriends.add(new FacebookFriendListItem(null, null, 0, 0));

        listView.setAdapter(new FacebookFriendListAdapter(getActivity(), R.id.lv_friend_selection, listFriends));

        Session session = Session.getActiveSession();
        return rootView;
    }

}
