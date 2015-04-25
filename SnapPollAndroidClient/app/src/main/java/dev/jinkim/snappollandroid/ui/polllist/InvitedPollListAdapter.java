package dev.jinkim.snappollandroid.ui.polllist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.app.App;
import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.model.User;
import dev.jinkim.snappollandroid.util.UriUtil;
import dev.jinkim.snappollandroid.util.image.CircleTransform;

/**
 * Created by Jin on 12/5/14.
 */
public class InvitedPollListAdapter extends BaseAdapter {
    private static final int TYPE_POLL = 0;
    private static final int TYPE_SECTION = 1;
    private Context context;
    private LayoutInflater inflater;
    private List<PollListItemInterface> list;

    static class InvitedPollViewHolder {
        public ImageView ivPollThumbnail;
        public ImageView ivCreatorThumbnail;
        public TextView tvCreator;
        public TextView tvQuestion;
        public TextView tvNumResponses;
    }

    public InvitedPollListAdapter(Context context, List<Poll> polls) {
        this.context = context;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (polls == null || polls.size() < 1) {
            list = new ArrayList<PollListItemInterface>();
        } else {
            setPollsWithSections(polls);
        }
    }

    public void clear() {
        list = null;
        list = new ArrayList<PollListItemInterface>();
    }

    public int getCount() {
        return list.size();
    }

    public Poll getItem(int position) {
        PollListItemInterface pollItem = list.get(position);
        if (pollItem.isPoll()) {
            return (Poll) pollItem;
        }

        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).isSection() ? TYPE_SECTION : TYPE_POLL;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);
        View view = convertView;
        final PollListItemInterface item = list.get(position);

        InvitedPollViewHolder pollHolder;
        SectionViewHolder sectionHolder;

        switch (type) {
            case TYPE_SECTION:
                if (view == null) {
                    sectionHolder = new SectionViewHolder();
                    view = inflater.inflate(R.layout.row_section, null);
                    view.setOnClickListener(null);
                    view.setOnClickListener(null);
                    view.setLongClickable(false);
                    sectionHolder.title = (TextView) view.findViewById(R.id.tv_section_title);
                    view.setTag(sectionHolder);

                } else {
                    sectionHolder = (SectionViewHolder) view.getTag();
                }

                sectionHolder.title.setText(((Section) item).getTitle());
                break;

            case TYPE_POLL:
                if (view == null) {
                    pollHolder = new InvitedPollViewHolder();
                    view = inflater.inflate(R.layout.row_invited_poll, null);
                    pollHolder.ivPollThumbnail = (ImageView) view
                            .findViewById(R.id.invited_poll_iv_poll_thumbnail);
                    pollHolder.ivCreatorThumbnail = (ImageView) view.findViewById(R.id.iv_creator_thumbnail);
                    pollHolder.tvCreator = (TextView) view.findViewById(R.id.tv_creator_name);
                    pollHolder.tvQuestion = (TextView) view.findViewById(R.id.tv_question);
                    view.setTag(pollHolder);

                } else {
                    pollHolder = (InvitedPollViewHolder) view.getTag();
                }

                Poll p = (Poll) item;

                // fill data
                String creator = p.getCreatorFirstName();
                String question = p.getQuestion();

                // append 'l' for large-sized thumbnail from Imgur
                UriUtil util = new UriUtil();
                // TODO: Find optimal thumbnail type based on device screen
                String referenceUrl = util.convertImgurThumbnail(p.getReferenceUrl(), 'l');

                pollHolder.tvCreator.setText(creator);
                pollHolder.tvQuestion.setText(question);

                Picasso.with(context).load(referenceUrl)
                        .into(pollHolder.ivPollThumbnail);


                String url = p.getCreatorProfilePicUrl();

                if (url != null && !url.isEmpty()) {
                    Picasso.with(context).load(url)
                            .transform(new CircleTransform())
                            .placeholder(R.drawable.ic_placeholder_profile)
                            .into(pollHolder.ivCreatorThumbnail);
                }

        }

        return view;
    }


    public void setPollsWithSections(List<Poll> polls) {

        User u = App.getInstance().getCurrentUser(context);

        list = new ArrayList<PollListItemInterface>();
        list.add(new Section(context.getResources().getString(R.string.lbl_section_not_responded)));
        for (Poll p : polls) {
            if (!p.isAnsweredByMe()) {
                list.add(p);
            }
        }

        //TODO: empty row for "open" section

        list.add(new Section(context.getResources().getString(R.string.lbl_section_responded)));
        for (Poll p : polls) {
            if (p.isAnsweredByMe()) {
                list.add(p);
            }
        }

        //TODO: empty row for "closed" section
    }


    public static class SectionViewHolder {
        public TextView title;
    }

    public class Section implements PollListItemInterface {

        private String title;

        public Section(String title) {
            this.title = title;
        }

        @Override
        public boolean isPoll() {
            return false;
        }

        @Override
        public boolean isSection() {
            return true;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}