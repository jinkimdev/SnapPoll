package dev.jinkim.snappollandroid.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import dev.jinkim.snappollandroid.R;

/**
 * Created by Jin on 2/18/15.
 *
 * Adapter used for the image source selection (Gallery or Camera) for attaching image reference
 */
public class SelectImageSourceAdapter extends ArrayAdapter {

    private Context context;
    private String[] sources;

    public SelectImageSourceAdapter(Context context, int arrayResId) {
        super(context, 0, arrayResId);
        this.context = context;
        this.sources = context.getResources().getStringArray(arrayResId);
    }

    @Override
    public int getCount() {
        return sources.length;
    }

    @Override
    public String getItem(int position) {
        return sources[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup group) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.row_select_image_source, null);

            holder.tvSourceName = (TextView) convertView.findViewById(R.id.select_img_source_tv_name);
            holder.ivSourceIcon = (ImageView) convertView.findViewById(R.id.select_img_source_iv_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
            // Camera
            Picasso
                    .with(context)
                    .load(R.drawable.ic_dialog_select_image_source_camera)
                    .into(holder.ivSourceIcon);
            holder.tvSourceName.setText(sources[0]);
        } else {
            // Gallery
            Picasso
                    .with(context)
                    .load(R.drawable.ic_dialog_select_image_source_gallery)
                    .into(holder.ivSourceIcon);
            holder.tvSourceName.setText(sources[1]);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivSourceIcon;
        TextView tvSourceName;

    }
}