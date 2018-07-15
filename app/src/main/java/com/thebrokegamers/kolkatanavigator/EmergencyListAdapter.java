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
public class EmergencyListAdapter extends ArrayAdapter<ArrayList<String>> {

    private Typeface font;

    public EmergencyListAdapter(Context context, ArrayList<ArrayList<String>> objects) {
        super(context, R.layout.emergency_listview, objects);
        font = Typeface.createFromAsset(context.getAssets(), "Exo2-Regular.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater emergencyInflater = LayoutInflater.from(getContext());
        View customView = emergencyInflater.inflate(R.layout.emergency_listview, parent, false);
        ArrayList<String> info = new ArrayList<String>(getItem(position));
        TextView emName = (TextView) customView.findViewById(R.id.emergencyTitleText);
        emName.setTypeface(font);
        emName.setText(info.get(0));
        TextView emNum = (TextView) customView.findViewById(R.id.emergencyNumberText);
        emNum.setTypeface(font);
        emNum.setText(info.get(1));
        RelativeLayout emHolder = (RelativeLayout) customView.findViewById(R.id.emergencyHolder);
        if(position % 3 == 0) {
            emHolder.setBackgroundResource(R.drawable.background_square_yellow);
        }
        else if(position % 3 == 1) {
            emHolder.setBackgroundResource(R.drawable.background_square_blue);
        }
        else {
            emHolder.setBackgroundResource(R.drawable.background_square_green);
        }
        return customView;
    }
}
