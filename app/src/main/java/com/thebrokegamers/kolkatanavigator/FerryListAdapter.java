package com.thebrokegamers.kolkatanavigator;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by g0ldfighter on 11/19/2015.
 */
public class FerryListAdapter extends ArrayAdapter<ArrayList<String>> {
    private Typeface font;
    private ArrayList<String> temp;
    public FerryListAdapter(Context context, ArrayList<ArrayList<String>> info) {
        super(context, R.layout.ferry_listview_normal, info);
        font = Typeface.createFromAsset(context.getAssets(), "Exo2-Regular.ttf");
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).size() == 5) {
            return 0;
        }
        else {
            return 1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater ferryInflater = LayoutInflater.from(getContext());
        View customView;
        temp = new ArrayList<String>(getItem(position));
        int type = getItemViewType(position);
        if (type == 0) {
            customView = ferryInflater.inflate(R.layout.ferry_listview_normal, parent, false);
        }
        else {
            customView = ferryInflater.inflate(R.layout.ferry_listview_other, parent, false);
        }
        SetTextView(customView, type);
        RelativeLayout ferryHolder = (RelativeLayout) customView.findViewById(R.id.ferryHolder);
        if(position % 3 == 0) {
            ferryHolder.setBackgroundResource(R.drawable.background_square_yellow);
        }
        else if(position % 3 == 1) {
            ferryHolder.setBackgroundResource(R.drawable.background_square_blue);
        }
        else {
            ferryHolder.setBackgroundResource(R.drawable.background_square_green);
        }
        return customView;
    }

    private void SetTextView(View view, int type) {
        ArrayList<TextView> changeFonts = new ArrayList<TextView>();
        if (type == 0) {
            changeFonts.add((TextView) view.findViewById(R.id.ferrySourceText));
            changeFonts.add((TextView) view.findViewById(R.id.ferryDestinationText));
            changeFonts.add((TextView) view.findViewById(R.id.ferryStartTimeText));
            changeFonts.add((TextView) view.findViewById(R.id.ferryEndTimeText));
            changeFonts.add((TextView) view.findViewById(R.id.ferryTimeIntervalText));
        }
        else {
            changeFonts.add((TextView) view.findViewById(R.id.ferrySourceText));
            changeFonts.add((TextView) view.findViewById(R.id.ferryDestinationText));
            changeFonts.add((TextView) view.findViewById(R.id.ferryVia1Text));
            changeFonts.add((TextView) view.findViewById(R.id.ferryVia2Text));
            changeFonts.add((TextView) view.findViewById(R.id.ferryStartTimeText));
            changeFonts.add((TextView) view.findViewById(R.id.ferryEndTimeText));
            changeFonts.add((TextView) view.findViewById(R.id.ferryTimeIntervalText));
        }
        for (int i = 0; i < changeFonts.size(); i++) {
            changeFonts.get(i).setTypeface(font);
            changeFonts.get(i).append(temp.get(i));
        }

        if (type == 1) {
            changeFonts.add((TextView) view.findViewById(R.id.ferryViaTitle));
            changeFonts.get(7).setTypeface(font);
        }
    }
}
