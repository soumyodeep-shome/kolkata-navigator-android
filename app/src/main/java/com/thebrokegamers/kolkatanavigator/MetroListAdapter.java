package com.thebrokegamers.kolkatanavigator;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by g0ldfighter on 11/5/2015.
 */
public class MetroListAdapter extends ArrayAdapter<ArrayList<String>> {
    private Typeface font;
    private Typeface fontBold;
    //private String currentTime;
    public MetroListAdapter(Context context, ArrayList<ArrayList<String>> metros) {
        super(context, R.layout.metro_listview, metros);
        font = Typeface.createFromAsset(context.getAssets(), "Exo2-Regular.ttf");
        fontBold = Typeface.createFromAsset(context.getAssets(), "Exo2-Bold.ttf");
        //this.currentTime = currentTime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater metroInflater = LayoutInflater.from(getContext());
        View customView = metroInflater.inflate(R.layout.metro_listview, parent, false);
        ArrayList<String> metroTemp = new ArrayList<String>(getItem(position));
        RelativeLayout metroBackground = (RelativeLayout) customView.findViewById(R.id.metroNumberHolder);
        if (position % 3 == 0) {
            metroBackground.setBackgroundResource(R.drawable.background_square_yellow);
        }
        else if (position % 3 == 1) {
            metroBackground.setBackgroundResource(R.drawable.background_square_blue);
        }
        else {
            metroBackground.setBackgroundResource(R.drawable.background_square_green);
        }
        ImageView listBackground = (ImageView) customView.findViewById(R.id.metroListViewBackgroundTint);
        listBackground.setAlpha(0f);
        String currentTime =  MetroFragment.currentTime();
        Log.d("METROTIME",currentTime);
        if (TrainListAdapter.TimeCompare(currentTime, metroTemp.get(0))) {
            listBackground.setAlpha(0.2f);
        }
        TextView metroReach = (TextView) customView.findViewById(R.id.metroReachText);
        metroReach.setTypeface(font);
        TextView metroFare = (TextView) customView.findViewById(R.id.metroFareText);
        metroFare.setTypeface(font);
        TextView metroSourceTime = (TextView) customView.findViewById(R.id.metroSourceTimeText);
        metroSourceTime.setTypeface(fontBold);
        metroSourceTime.setText(metroTemp.get(0));
        TextView metroReachTime = (TextView) customView.findViewById(R.id.metroReachTimeText);
        metroReachTime.setTypeface(font);
        metroReachTime.setText(metroTemp.get(1));
        TextView metroFareText = (TextView) customView.findViewById(R.id.metroFareValueText);
        metroFareText.setTypeface(font);
        metroFareText.append(metroTemp.get(2));
        return customView;
    }
}
