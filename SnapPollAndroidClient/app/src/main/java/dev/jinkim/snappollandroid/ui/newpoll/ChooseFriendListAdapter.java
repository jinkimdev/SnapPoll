package dev.jinkim.snappollandroid.ui.newpoll;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.plus.model.people.Person;
import com.squareup.picasso.Picasso;

import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.RowFriend;
import dev.jinkim.snappollandroid.util.image.CircleTransform;

/**
 * Created by Jin on 3/10/15.
 */
public class ChooseFriendListAdapter extends ArrayAdapter<RowFriend> {
    private LayoutInflater li;
    private Context context;

    /**
     * Constructor from a list of items
     */
    public ChooseFriendListAdapter(Context context, List<RowFriend> friends) {
        super(context, 0, friends);
        li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final RowFriend friend = getItem(position);

        ViewHolder holder;
        if (convertView == null) {
            convertView = li.inflate(R.layout.row_choose_friend, null);
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


        int color;
        if (friend.selected) {
            color = Color.parseColor("#8000FF00");
            holder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            color = Color.parseColor("#FFFFFF");
            holder.ivCheck.setVisibility(View.INVISIBLE);
        }
        convertView.setBackgroundColor(color);

//        // Restore the checked state properly
//        final ListView lv = (ListView) parent;
//        holder.layout.setChecked(lv.isItemChecked(position));

        return convertView;
    }

//    @Override
//    public long getItemId(int position) {
//        return getItem(position).getId();
//    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private static class ViewHolder {

        public ImageView ivProfile;
        public TextView tvName;
        public ImageView ivCheck;


        public ViewHolder(View root) {
            ivProfile = (ImageView) root.findViewById(R.id.choose_friend_iv_profile);
            tvName = (TextView) root.findViewById(R.id.choose_friend_tv_name);
            ivCheck = (ImageView) root.findViewById(R.id.choose_friend_iv_check);
        }
    }
}
