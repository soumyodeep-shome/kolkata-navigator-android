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
 * Created by g0ldfighter on 11/5/2015.
 */
public class MetroStationListAdapter extends ArrayAdapter<String> {
    private Typeface font;
    private Typeface fontBold;

    public MetroStationListAdapter(Context context, ArrayList<String> stations) {
        super(context, R.layout.metro_station_listview, stations);
        font = Typeface.createFromAsset(context.getAssets(), "Exo2-Regular.ttf");
        fontBold = Typeface.createFromAsset(context.getAssets(), "Exo2-Bold.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater metroInflater = LayoutInflater.from(getContext());
        View customView = metroInflater.inflate(R.layout.metro_station_listview, parent, false);
        String metroStation = getItem(position);
        RelativeLayout metroBackground = (RelativeLayout) customView.findViewById(R.id.metroStationHolder);
        if (position % 3 == 0) {
            metroBackground.setBackgroundResource(R.drawable.background_square_yellow);
        }
        else if (position % 3 == 1) {
            metroBackground.setBackgroundResource(R.drawable.background_square_blue);
        }
        else {
            metroBackground.setBackgroundResource(R.drawable.background_square_green);
        }

        TextView metroHeader = (TextView) customView.findViewById(R.id.metroHeader);
        metroHeader.setTypeface(fontBold);
        TextView metroStationName = (TextView) customView.findViewById(R.id.metroStationName);
        metroStationName.setTypeface(font);
        metroStationName.setText(metroStation);
        return customView;
    }
}
