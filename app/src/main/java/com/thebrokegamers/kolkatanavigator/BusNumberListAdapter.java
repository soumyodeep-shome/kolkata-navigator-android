package com.thebrokegamers.kolkatanavigator;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by g0ldfighter on 10/27/2015.
 */
public class BusNumberListAdapter extends ArrayAdapter {

    private Typeface font;
    public BusNumberListAdapter(Context context, ArrayList<String> stoppages) {
        super(context, R.layout.bus_number_listview, stoppages);
        font = Typeface.createFromAsset(context.getAssets(), "Exo2-Regular.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater busInflater = LayoutInflater.from(getContext());
        View customView = busInflater.inflate(R.layout.bus_number_listview, parent, false);
        ImageView busNumberListViewIcon = (ImageView) customView.findViewById(R.id.busNumberListViewIcon);
        TextView busNumberListViewText = (TextView) customView.findViewById(R.id.busNumberListViewText);
        busNumberListViewText.setTypeface(font);
        if(position == 0) {
            busNumberListViewIcon.setImageResource(R.drawable.transport_number_listview_icon_0);
        }
        else if (position == getCount() - 1) {
            busNumberListViewIcon.setImageResource(R.drawable.transport_number_listview_icon_2);
        }
        else {
            busNumberListViewIcon.setImageResource(R.drawable.transport_number_listview_icon_1);
        }
        busNumberListViewText.setText(getItem(position).toString().toUpperCase());

        return customView;
    }
}
