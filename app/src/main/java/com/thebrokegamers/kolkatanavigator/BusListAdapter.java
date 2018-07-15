package com.thebrokegamers.kolkatanavigator;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by g0ldfighter on 10/25/2015.
 */
public class BusListAdapter extends ArrayAdapter<String[]> {

    Typeface font;
    Typeface font_bold;

    public BusListAdapter(Context context, ArrayList<String[]> buses) {
        super(context, R.layout.bus_listview, buses);
        font = Typeface.createFromAsset(context.getAssets(), "Exo2-Regular.ttf");
        font_bold = Typeface.createFromAsset(context.getAssets(), "Exo2-Bold.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater busInflater = LayoutInflater.from(getContext());
        View customView = busInflater.inflate(R.layout.bus_listview, parent, false);
        RelativeLayout busNumberBackground = (RelativeLayout) customView.findViewById(R.id.busNumberHolder);
        RelativeLayout busACBackground = (RelativeLayout) customView.findViewById(R.id.busACType);
        ImageView busACIcon = (ImageView) customView.findViewById(R.id.busACIcon);
        TextView busNumberText = (TextView) customView.findViewById(R.id.busNumberText);
        TextView busSourceText = (TextView) customView.findViewById(R.id.busSourceText);
        TextView busDestinationText = (TextView) customView.findViewById(R.id.busDestinationText);
        String[] busInfo = new String[4];
        busInfo = getItem(position);
        busNumberText.setText(busInfo[0].toUpperCase());
        busSourceText.append(busInfo[2].toUpperCase());
        busDestinationText.append(busInfo[3].toUpperCase());
        busNumberText.setTypeface(font_bold);
        busSourceText.setTypeface(font);
        busDestinationText.setTypeface(font);

        if(position % 3 == 0) {
            busNumberBackground.setBackgroundResource(R.drawable.background_square_yellow);
        }
        else if(position % 3 == 1) {
            busNumberBackground.setBackgroundResource(R.drawable.background_square_blue);
        }
        else {
            busNumberBackground.setBackgroundResource(R.drawable.background_square_green);
        }
        if (busInfo[1].toUpperCase().equals("N")) {
            busACBackground.setBackgroundResource(R.drawable.non_ac_background);
            busACIcon.setImageResource(R.drawable.ic_wb_sunny_white_24dp);
        }
        else {
            busACBackground.setBackgroundResource(R.drawable.ac_background);
            busACIcon.setImageResource(R.drawable.ic_ac_unit_white_24dp);
        }
        return customView;
    }
}
