package dev.jinkim.snappollandroid.ui.newpoll;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.gc.materialdesign.views.ButtonRectangle;

import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.PollAttribute;
import dev.jinkim.snappollandroid.ui.widget.colorpicker.ColorPickerDialogDash;
import dev.jinkim.snappollandroid.util.ColorUtil;

/**
 * Created by Jin on 4/22/15.
 */
public class NewPollAttributeAdapter extends ArrayAdapter<PollAttribute> {
    private Context context;
    private List<PollAttribute> attributes;
    private int[] mMarkerColors;
    private FragmentManager fm;

    static class ViewHolder {
        public ButtonRectangle btnColor;
        public ImageView ivRemoveButton;
        public EditText etAttributeName;
    }

    public NewPollAttributeAdapter(Context context, List<PollAttribute> attributes, FragmentManager fm) {
        super(context, R.layout.row_poll_detail_attribute, attributes);
        this.context = context;
        this.attributes = attributes;
        this.fm = fm;
        this.mMarkerColors = ColorUtil.colorChoice(context, R.array.snappoll_response_colors);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_new_poll_attribute_item, null);

            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.btnColor = (ButtonRectangle) rowView.findViewById(R.id.btn_new_poll_attribute_color);
            viewHolder.ivRemoveButton = (ImageView) rowView.findViewById(R.id.iv_new_poll_attribute_remove_button);
            viewHolder.etAttributeName = (EditText) rowView.findViewById(R.id.et_new_poll_attribute_name);
            rowView.setTag(viewHolder);
        }

        /* populate fields */

        // handle visibility for different cases
        ViewHolder holder = (ViewHolder) rowView.getTag();
        PollAttribute att = attributes.get(position);

        // edit text
        String name = attributes.get(position).getAttributeName();
        if (name == null || name.length() < 1) {
            name = String.format(
                    context.getString(R.string.lbl_default_numbered_attribute_name),
                    position + 1);
            holder.etAttributeName.setHint(name);
        } else {
            holder.etAttributeName.setText(name);
        }

        holder.etAttributeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                attributes.get(position).setAttributeName(s.toString());
            }
        });

        // color indicator
        holder.btnColor.setBackgroundColor(Color.parseColor(att.getAttributeColorHex()));
        holder.btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPicker(position, v);
            }
        });

        // hide remove button for the default choice
        if (position == 0) {
            holder.ivRemoveButton.setVisibility(View.INVISIBLE);
        } else {
            holder.ivRemoveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attributes.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
        return rowView;
    }

    private void showColorPicker(final int position, final View colorIndicatorView) {

        ColorPickerDialogDash colordashfragment = ColorPickerDialogDash.newInstance(
                R.string.color_picker_default_title,
                mMarkerColors, 0, 4);

        //Implement listener to get color value
        colordashfragment.setOnColorSelectedListener(new ColorPickerDialogDash.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                // reflect the selected color on the button
                colorIndicatorView.setBackgroundColor(color);
                // save the hex value into attribute object
                attributes.get(position).setAttributeColorHex(ColorUtil.convertToHex(color));
            }
        });
        colordashfragment.show(fm, "ColorPicker");
    }

    // if there is any attribute with empty name, use hint text as name and return
    public List<PollAttribute> grabAttributes() {

        for (int i = 0; i < attributes.size(); i++) {
            PollAttribute att = attributes.get(i);
            if (att.getAttributeName().length() < 1) {
                att.setAttributeName(String.format(context.getString(R.string.lbl_default_numbered_attribute_name), i + 1));
            }
        }
        return attributes;
    }
}