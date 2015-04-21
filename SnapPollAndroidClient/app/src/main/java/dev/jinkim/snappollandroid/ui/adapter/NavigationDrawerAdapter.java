package dev.jinkim.snappollandroid.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import dev.jinkim.snappollandroid.R;

/**
 * Created by Jin on 4/20/15.
 */
public class NavigationDrawerAdapter extends ArrayAdapter<DrawerItem> {
    private Context context;
    private List<DrawerItem> drawerItems;
    private int layoutResId;
    private int mCurrentSelectedPosition;

    public NavigationDrawerAdapter(Context context, int layoutResourceID,
                                   List<DrawerItem> listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.drawerItems = listItems;
        this.layoutResId = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        DrawerItemHolder itemHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            itemHolder = new DrawerItemHolder();

            view = inflater.inflate(layoutResId, parent, false);
            itemHolder.container = (LinearLayout) view.findViewById(R.id.nav_drawer_ll_container);
            itemHolder.title = (TextView) view
                    .findViewById(R.id.nav_drawer_tv_title);
            itemHolder.icon = (ImageView) view.findViewById(R.id.nav_drawer_iv_icon);

            view.setTag(itemHolder);

        } else {
            itemHolder = (DrawerItemHolder) view.getTag();
        }

        // check selected position
        if (mCurrentSelectedPosition == position) {
            itemHolder.container.setBackgroundColor(context.getResources().getColor(R.color.drawer_selected));
        } else {
            itemHolder.container.setBackgroundColor(context.getResources().getColor(R.color.drawer_not_selected));
        }

        DrawerItem item = drawerItems.get(position);

        itemHolder.icon.setImageDrawable(view.getResources().getDrawable(
                item.getIconResId()));
        itemHolder.title.setText(item.getTitle());

        return view;
    }

    private static class DrawerItemHolder {
        LinearLayout container;
        TextView title;
        ImageView icon;
    }

    public void setSelectedPosition(int position) {
        mCurrentSelectedPosition = position;
    }
}
