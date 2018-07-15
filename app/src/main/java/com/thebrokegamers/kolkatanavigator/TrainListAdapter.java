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
public class TrainListAdapter extends ArrayAdapter<ArrayList<String>> {

    private Typeface font;
    private Typeface fontBold;

    public TrainListAdapter(Context context, ArrayList<ArrayList<String>> trains) {
        super(context, R.layout.train_listview, trains);
        font = Typeface.createFromAsset(context.getAssets(), "Exo2-Regular.ttf");
        fontBold = Typeface.createFromAsset(context.getAssets(), "Exo2-Bold.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater trainInflater = LayoutInflater.from(getContext());
        View customView = trainInflater.inflate(R.layout.train_listview, parent, false);
        RelativeLayout trainNumberBackground = (RelativeLayout) customView.findViewById(R.id.trainNumberHolder);
        TextView trainNumberText = (TextView) customView.findViewById(R.id.trainNumberText);
        trainNumberText.setTypeface(fontBold);
        TextView trainStartText = (TextView) customView.findViewById(R.id.trainStartTimeText);
        trainStartText.setTypeface(font);
        TextView trainEndText = (TextView) customView.findViewById(R.id.trainEndTimeText);
        trainEndText.setTypeface(font);
        TextView trainName = (TextView) customView.findViewById(R.id.trainNameText);
        trainName.setTypeface(font);
        ImageView listBackground = (ImageView) customView.findViewById(R.id.trainListViewBackgroundTint);
        ArrayList<String> trainInfo = new ArrayList<String>();
        trainInfo.addAll(getItem(position));
        trainStartText.append(trainInfo.get(0).toUpperCase());
        trainEndText.append(trainInfo.get(1).toUpperCase());
        trainNumberText.setText(trainInfo.get(2).toUpperCase());
        trainName.setText(trainInfo.get(3).toUpperCase());
        if (TimeCompare(TrainFragment.currentTime(), trainInfo.get(0))) {
            listBackground.setAlpha(0.2f);
        }
        else {
            listBackground.setAlpha(0f);
        }

        if (position % 3 == 0) {
            trainNumberBackground.setBackgroundResource(R.drawable.background_square_yellow);
        } else if (position % 3 == 1) {
            trainNumberBackground.setBackgroundResource(R.drawable.background_square_blue);
        } else {
            trainNumberBackground.setBackgroundResource(R.drawable.background_square_green);
        }

        return customView;
    }

    public static boolean TimeCompare(String currentTime, String comparisonTime) {
        int h1;
        int h2;
        int m1;
        int m2;
        h1 = Integer.parseInt(currentTime.substring(0, 2));
        m1 = Integer.parseInt(currentTime.substring(3));
        h2 = Integer.parseInt(comparisonTime.substring(0, 2));
        m2 = Integer.parseInt(comparisonTime.substring(3));
        if (h1 > h2) {
            return true;
        } else if (h1 == h2) {
            if (m1 > m2) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
