package dev.jinkim.snappollandroid.ui.newpoll;

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
import android.widget.Toast;

import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.PollAttribute;
import dev.jinkim.snappollandroid.ui.widget.colorpicker.ColorPickerDialogDash;
import dev.jinkim.snappollandroid.util.ColorUtil;

/**
 * Created by Jin on 4/22/15.
 */
public class AttributeLineAdapter extends ArrayAdapter<PollAttribute> {
    private Context context;
    private List<PollAttribute> attributes;
    private int[] mMarkerColors;
    private FragmentManager fm;
    private AttributeLineMode mode;

    public enum AttributeLineMode {
        NEW_POLL,
        SUBMIT_RESPONSE,
        VIEW_RESULT
    }

    static class ViewHolder {
        public RadioButton rbSelection;
        public ImageView ivColorIndicator;
        public ImageView ivRemoveButton;
        public TextView tvAttributeName;
    }

    public AttributeLineAdapter(Context context, List<PollAttribute> attributes, FragmentManager fm, AttributeLineMode lineDisplayMode) {
        super(context, R.layout.row_poll_attribute_line_item, attributes);
        this.context = context;
        this.attributes = attributes;
        this.mode = lineDisplayMode;
        this.fm = fm;
        this.mMarkerColors = ColorUtil.colorChoice(context, R.array.snappoll_response_colors);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_poll_attribute_line_item, null);

            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.rbSelection = (RadioButton) rowView.findViewById(R.id.rb_attribute_line_selection);
            viewHolder.ivColorIndicator = (ImageView) rowView.findViewById(R.id.iv_attribute_line_color_indicator);

            viewHolder.ivRemoveButton = (ImageView) rowView.findViewById(R.id.iv_attribute_line_remove_button);

            rowView.setTag(viewHolder);
        }

        // handle visibility for different cases
        ViewHolder holder = (ViewHolder) rowView.getTag();
        PollAttribute att = attributes.get(position);

        updateIndicatorColor(holder.ivColorIndicator, att.getAttributeColorHex());

        // set up view elements according to the attribute line display mode
        switch (mode) {
            case NEW_POLL:
                holder.rbSelection.setVisibility(View.GONE);

                holder.ivColorIndicator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                if (att.getAttributeName().equals(context.getString(R.string.lbl_default_attribute_name))) {
                    // if this is the default attribute, do not show remove button
                    holder.ivRemoveButton.setVisibility(View.GONE);
                } else {
                    holder.ivRemoveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            attributes.remove(position);
                        }
                    });
                }

                break;

            case SUBMIT_RESPONSE:
                holder.ivRemoveButton.setVisibility(View.GONE);

                holder.rbSelection.setChecked(false);

            case VIEW_RESULT:
                holder.rbSelection.setVisibility(View.GONE);
                holder.ivRemoveButton.setVisibility(View.GONE);

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


    private void showColorPicker(final ImageView ivColorIndicator) {

        ColorPickerDialogDash colordashfragment = ColorPickerDialogDash.newInstance(
                R.string.color_picker_default_title,
                mMarkerColors, 0, 4);

        //Implement listener to get color value
        colordashfragment.setOnColorSelectedListener(new ColorPickerDialogDash.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                updateIndicatorColor(ivColorIndicator, color);
            }
        });

        colordashfragment.show(fm, "dash");

    }
}
