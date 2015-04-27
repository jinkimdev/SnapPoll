package dev.jinkim.snappollandroid.ui.polldetail;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.PollAttribute;
import dev.jinkim.snappollandroid.ui.widget.colorpicker.ColorPickerDialogDash;

/**
 * Created by Jin on 4/22/15.
 *
 * Adapter for displaying answer choices in the sliding panel
 */
public class PollDetailAttributeAdapter extends ArrayAdapter<PollAttribute> {
    private Context context;
    private List<PollAttribute> attributes;
    private AttributeLineMode mode;

    public enum AttributeLineMode {
        SUBMIT_RESPONSE,
        VIEW_RESULT
    }

    static class ViewHolder {
        public RadioButton rbSelection;
        public ImageView ivColorIndicator;
        public TextView tvAttributeName;
    }

    public PollDetailAttributeAdapter(Context context, List<PollAttribute> attributes, AttributeLineMode lineDisplayMode) {
        super(context, R.layout.row_poll_detail_attribute_item, attributes);
        this.context = context;
        this.attributes = attributes;
        this.mode = lineDisplayMode;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_poll_detail_attribute_item, null);

            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.rbSelection = (RadioButton) rowView.findViewById(R.id.rb_poll_detail_attribute_selection);
            viewHolder.ivColorIndicator = (ImageView) rowView.findViewById(R.id.iv_poll_detail_attribute_color_indicator);

            rowView.setTag(viewHolder);
        }

        // handle visibility for different cases
        ViewHolder holder = (ViewHolder) rowView.getTag();
        PollAttribute att = attributes.get(position);

        updateIndicatorColor(holder.ivColorIndicator, att.getAttributeColorHex());

        // set up view elements according to the attribute line display mode
        switch (mode) {
            case SUBMIT_RESPONSE:
                holder.rbSelection.setChecked(false);
                break;

            case VIEW_RESULT:
                holder.rbSelection.setVisibility(View.GONE);
                break;
        }

        return rowView;
    }

    private void updateIndicatorColor(View v, String colorHex) {
        int color = Color.parseColor(colorHex);

        updateIndicatorColor(v, color);
    }

    private void updateIndicatorColor(View v, int color) {
        GradientDrawable gradDrawable = (GradientDrawable) v.getBackground();
        if (gradDrawable != null) {
            gradDrawable.setColor(color);
        }
    }
}
