package dev.jinkim.snappollandroid.ui.adapter;

import android.app.Activity;
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

/**
 * Created by Jin on 12/5/14.
 */
public class MyPollListAdapter extends BaseAdapter {
    private static final int TYPE_POLL = 0;
    private static final int TYPE_SECTION = 1;
    private Activity mActivity;
    private List<MyPollItem> list;
    private static LayoutInflater inflater;

    public MyPollListAdapter(Activity activity, List<Poll> polls) {
        mActivity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (polls == null) {
            list = new ArrayList<MyPollItem>();
        }
        setPollsWithSections(polls);
    }

    public void clear() {
        list = null;
        list = new ArrayList<MyPollItem>();
    }

    public void setPollsWithSections(List<Poll> polls) {
        if (polls == null || polls.size() < 1) {
            return;
        }

        User u = App.getInstance().getCurrentUser(mActivity);

        list = new ArrayList<MyPollItem>();
        list.add(new Section("Open"));
        for (Poll p : polls) {
            if (p.getCreatorId().equals(u.getUserId()) && p.isActive()) {
                list.add(p);
            }
        }

        //TODO: empty row for "open" section
        list.add(new Section("Closed"));
        for (Poll p : polls) {
            if (p.getCreatorId().equals(u.getUserId()) && !p.isActive()) {
                list.add(p);
            }
        }

        //TODO: empty row for "closed" section
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return position;
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


    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        View view = convertView;
        final MyPollItem item = list.get(position);

        InvitedPollListAdapter.ViewHolder pollHolder;
        SectionViewHolder sectionHolder;

        switch (type) {

            case TYPE_SECTION:
                if (view == null) {
                    sectionHolder = new SectionViewHolder();
                    view = inflater.inflate(R.layout.row_section, null);
                    view.setOnClickListener(null);
                    view.setOnLongClickListener(null);
                    view.setLongClickable(false);
                    sectionHolder.title = (TextView) view.findViewById(R.id.tv_section_title);
                    view.setTag(sectionHolder);

                } else {
                    sectionHolder = (SectionViewHolder) view.getTag();
                }

                Section section = (Section) item;
                sectionHolder.title.setText(((Section) item).getTitle());

                break;

            case TYPE_POLL:
                if (view == null) {
                    pollHolder = new InvitedPollListAdapter.ViewHolder();
                    view = inflater.inflate(R.layout.row_my_poll, null);
                    pollHolder.ivPollThumbnail = (ImageView) view.findViewById(R.id.iv_poll_thumbnail);
                    pollHolder.tvQuestion = (TextView) view.findViewById(R.id.tv_question);

                    view.setTag(pollHolder);

                } else {
                    pollHolder = (InvitedPollListAdapter.ViewHolder) view.getTag();
                }

                Poll p = (Poll) item;
                pollHolder.tvQuestion.setText(p.getQuestion());
                Picasso.with(mActivity).load(p.getReferenceUrl())
                        .into(pollHolder.ivPollThumbnail);

                break;
        }
        return view;

    }

    public static class SectionViewHolder {
        public TextView title;
    }

    public interface MyPollItem {
        public boolean isPoll();

        public boolean isSection();
    }

    public class Section implements MyPollItem {

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

