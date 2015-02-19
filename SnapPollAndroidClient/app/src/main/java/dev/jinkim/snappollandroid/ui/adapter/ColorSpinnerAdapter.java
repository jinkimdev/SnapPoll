package dev.jinkim.snappollandroid.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import dev.jinkim.snappollandroid.R;

/**
 * Created by Jin on 2/18/15.
 */
public class ColorSpinnerAdapter extends ArrayAdapter<Pair<String, String>> {

    private Context context;
    private List<Pair<String, String>> colorPairs;

    public ColorSpinnerAdapter(Context context, int txtViewResourceId, List<Pair<String, String>> colorPairs) {
        super(context, txtViewResourceId, colorPairs);
        this.context = context;
        this.colorPairs = colorPairs;
    }

    @Override
    public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override
    public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView(pos, cnvtView, prnt);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View spinnerRowView = inflater.inflate(R.layout.row_spinner_color_picker, parent, false);

        TextView tvColorText = (TextView) spinnerRowView.findViewById(R.id.tv_spinner_color_text);
        String colorName = (colorPairs.get(position)).first;
        tvColorText.setText(colorName);

        View colorIndicator = spinnerRowView.findViewById(R.id.view_spinner_color_indicator);
        String colorHex = (colorPairs.get(position)).second;
        colorIndicator.setBackgroundColor(Color.parseColor(colorHex));

        return spinnerRowView;
    }
}

