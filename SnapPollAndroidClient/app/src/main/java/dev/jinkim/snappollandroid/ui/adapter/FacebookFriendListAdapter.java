package dev.jinkim.snappollandroid.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.FacebookFriendListItem;

/**
 * Created by Jin on 3/9/15.
 */
public class FacebookFriendListAdapter extends ArrayAdapter<FacebookFriendListItem> {

    private List<FacebookFriendListItem> list;
    private Context context;

    public FacebookFriendListAdapter(Context context, int resourceId, List<FacebookFriendListItem> listItems) {
        super(context, resourceId, listItems);
        this.list = listItems;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_facebook_friend, null);
        }

        FacebookFriendListItem friend = list.get(position);
        if (list != null) {
            ImageView ivProfile = (ImageView) view.findViewById(R.id.facebook_friend_iv_profile);
            TextView tvName = (TextView) view.findViewById(R.id.facebook_friend_tv_name);

            ivProfile.setImageDrawable(friend.getPic());

            tvName.setText(friend.getName());
        }

        return view;
    }
}
