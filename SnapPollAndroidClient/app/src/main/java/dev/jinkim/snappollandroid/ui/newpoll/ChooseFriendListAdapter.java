package dev.jinkim.snappollandroid.ui.newpoll;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.RowFriend;
import dev.jinkim.snappollandroid.util.image.CircleTransform;

/**
 * Created by Jin on 3/10/15.
 */
public class ChooseFriendListAdapter extends ArrayAdapter<RowFriend> implements Filterable {
    private LayoutInflater li;
    private Context context;

    private List<RowFriend> friends;
    private List<RowFriend> filteredList;

    private FriendFilter friendFilter;

    /**
     * Constructor from a list of items
     */
    public ChooseFriendListAdapter(Context context, List<RowFriend> friends) {
        super(context, 0, friends);
        li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;

        // initialize variables to the original lists
        this.friends = friends;
        filteredList = friends;

        getFilter();
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
            color = context.getResources().getColor(R.color.select_highlight);
            holder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            color = context.getResources().getColor(R.color.unselect_highlight);
            holder.ivCheck.setVisibility(View.INVISIBLE);
        }
        convertView.setBackgroundColor(color);

//        // Restore the checked state properly
//        final ListView lv = (ListView) parent;
//        holder.layout.setChecked(lv.isItemChecked(position));

        return convertView;
    }


    /**
     * Get size of filtered list
     *
     * @return number of friends in filteredList
     */
    @Override
    public int getCount() {
        return filteredList.size();
    }

    /**
     * Get a RowFriend from the filteredList
     *
     * @param i item index
     * @return list item
     */
    @Override
    public RowFriend getItem(int i) {
        return filteredList.get(i);
    }

    /**
     * Get a RowFriend from the original list
     *
     * @param i item index (index from the original list)
     * @return list item
     */
    public RowFriend getItemFromOriginalList(int i) {
        return friends.get(i);
    }


    /**
     * Get current row's Google+ id
     *
     * @param i item index from filteredList
     * @return current Person's Google+ id
     */
    @Override
    public long getItemId(int i) {
//        long id = Long.parseLong(filteredList.get(i).person.getId());
        //TODO: Exception on error?
//        return id;
        return i;
    }

    /**
     * @param i item index from the filteredList
     * @return index from the original list
     */
    public int getOriginalIndex(int i) {
        RowFriend r = filteredList.get(i);
        for (int k = 0; k < friends.size(); k++) {
            // check if Google+ id matches
            if (r.person.getId().equals(friends.get(k).person.getId())) {
                return k;
            }
        }

        // error, RowFriend not found -- should not happen
        return -1;
    }

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

    @Override
    public Filter getFilter() {
        if (friendFilter == null) {
            friendFilter = new FriendFilter();
        }

        return friendFilter;
    }

    /**
     * Custom filter for friend list
     * Filter content in friend list according to the search text
     */
    private class FriendFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                List<RowFriend> tempList = new ArrayList<RowFriend>();

                // search content in friend list
                for (RowFriend r : friends) {
                    if (r.person.getDisplayName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(r);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = friends.size();
                filterResults.values = friends;
            }

            return filterResults;
        }

        /**
         * Notify about filtered list to ui
         *
         * @param constraint text
         * @param results    filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<RowFriend>) results.values;
            notifyDataSetChanged();
        }
    }

    public List<RowFriend> getAllFriends() {
        return friends;
    }

}
