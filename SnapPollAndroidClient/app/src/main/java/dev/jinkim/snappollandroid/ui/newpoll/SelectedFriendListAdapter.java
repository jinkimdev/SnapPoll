package dev.jinkim.snappollandroid.ui.newpoll;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.RowFriend;
import dev.jinkim.snappollandroid.util.image.CircleTransform;

/**
 * Created by Jin on 3/10/15.
 */
public class SelectedFriendListAdapter extends ArrayAdapter<RowFriend> {
    private LayoutInflater li;
    private Context context;
    private List<RowFriend> friends;
    private NewPollActivity mActivity;

    public static final String TAG = SelectedFriendListAdapter.class.getSimpleName();

    /**
     * Constructor from a list of items
     */
    public SelectedFriendListAdapter(Context context, List<RowFriend> friends) {
        super(context, 0, friends);
        li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.friends = friends;
        if (context instanceof NewPollActivity) {
            mActivity = (NewPollActivity) context;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final RowFriend friend = getItem(position);

        ViewHolder holder;
        if (convertView == null) {
            convertView = li.inflate(R.layout.row_selected_friend, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Set some view properties
        holder.tvName.setText(friend.person.getDisplayName());
        Picasso.with(context)
                .load(friend.person.getImage().getUrl())
                .transform(new CircleTransform())
                .fit().centerInside()
                .into(holder.ivProfile);
        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friends.remove(position);
                notifyDataSetChanged();
                //TODO: SNACKBAR MESSAGE?
//                for (RowFriend r : mActivity.getController().getFriends()) {
//                    Log.d(TAG, r.person.getDisplayName());
//                }
            }
        });

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private static class ViewHolder {

        public ImageView ivProfile;
        public TextView tvName;
        public ImageView ivRemove;


        public ViewHolder(View root) {
            ivProfile = (ImageView) root.findViewById(R.id.selected_friend_iv_profile);
            tvName = (TextView) root.findViewById(R.id.selected_friend_tv_name);
            ivRemove = (ImageView) root.findViewById(R.id.selected_friend_iv_remove);
        }
    }
}
