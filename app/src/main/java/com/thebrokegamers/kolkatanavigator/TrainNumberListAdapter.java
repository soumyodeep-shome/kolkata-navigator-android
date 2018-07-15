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

public class TrainNumberListAdapter extends ArrayAdapter<String[]> {
    private Typeface font;

    public TrainNumberListAdapter(Context context, ArrayList<String[]> info) {
        super(context, R.layout.train_number_listview, info);
        font = Typeface.createFromAsset(context.getAssets(), "Exo2-Regular.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater trainInflater = LayoutInflater.from(getContext());
        View customView = trainInflater.inflate(R.layout.train_number_listview, parent, false);
        ImageView trainNumberListViewIcon = (ImageView) customView.findViewById(R.id.trainNumberListViewIcon);
        TextView trainNumberListViewText = (TextView) customView.findViewById(R.id.trainNumberListViewStoppage);
        trainNumberListViewText.setTypeface(font);
        TextView trainNumberListViewTime = (TextView) customView.findViewById(R.id.trainNumberListViewTime);
        trainNumberListViewTime.setTypeface(font);
        if(position == 0) {
            trainNumberListViewIcon.setImageResource(R.drawable.transport_number_listview_icon_0);
        }
        else if (position == getCount() - 1) {
            trainNumberListViewIcon.setImageResource(R.drawable.transport_number_listview_icon_2);
        }
        else {
            trainNumberListViewIcon.setImageResource(R.drawable.transport_number_listview_icon_1);
        }
        String[] trainInfo = new String[2];
        trainInfo = getItem(position);
        trainNumberListViewText.setText(trainInfo[1]);
        trainNumberListViewTime.setText(trainInfo[0]);

        return customView;
    }
}