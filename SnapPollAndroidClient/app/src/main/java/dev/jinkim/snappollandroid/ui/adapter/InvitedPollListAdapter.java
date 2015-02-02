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
import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.util.image.CircleTransform;

/**
 * Created by Jin on 12/5/14.
 */
public class InvitedPollListAdapter extends ArrayAdapter<Poll> {

    private Context context;
    private List<Poll> polls;

    static class ViewHolder {
        public ImageView ivPollThumbnail;
        public ImageView ivCreatorThumbnail;
        public TextView tvCreator;
        public TextView tvQuestion;
    }

    public InvitedPollListAdapter(Context context, List<Poll> polls) {
        super(context, R.layout.row_invited_poll, polls);
        this.context = context;
        this.polls = polls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_invited_poll, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.ivPollThumbnail = (ImageView) rowView
                    .findViewById(R.id.iv_poll_thumbnail);
            viewHolder.ivCreatorThumbnail = (ImageView) rowView.findViewById(R.id.iv_creator_thumbnail);
            viewHolder.tvCreator = (TextView) rowView.findViewById(R.id.tv_creator_name);
            viewHolder.tvQuestion = (TextView) rowView.findViewById(R.id.tv_question);

            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        Poll p = polls.get(position);
        String creator = p.getCreatorFirstName();
        String question = p.getQuestion();
        String referenceUrl = p.getReferenceUrl();

        holder.tvCreator.setText(creator);
        holder.tvQuestion.setText(question);
        Picasso.with(context).load(referenceUrl)
                .into(holder.ivPollThumbnail);

        String url = p.getCreatorProfilePicUrl();

        if (url != null && !url.isEmpty()) {
            Picasso.with(context).load(url)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.ic_placeholder_profile)
                    .into(holder.ivCreatorThumbnail);
        }

        return rowView;
    }
}