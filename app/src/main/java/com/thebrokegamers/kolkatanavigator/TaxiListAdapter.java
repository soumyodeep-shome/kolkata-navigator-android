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
public class TaxiListAdapter extends ArrayAdapter<ArrayList<String>> {

    Typeface font;
    Typeface font_bold;

    public TaxiListAdapter(Context context, ArrayList<ArrayList<String>> taxies) {
        super(context, R.layout.taxi_listview, taxies);
        font = Typeface.createFromAsset(context.getAssets(), "Exo2-Regular.ttf");
        font_bold = Typeface.createFromAsset(context.getAssets(), "Exo2-Bold.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater taxiInflater = LayoutInflater.from(getContext());
        View customView = taxiInflater.inflate(R.layout.taxi_listview, parent, false);
        RelativeLayout taxiNumberBackground = (RelativeLayout) customView.findViewById(R.id.taxiNumberHolder);
        TextView taxiNumberText = (TextView) customView.findViewById(R.id.taxiNumberText);
        TextView taxiSourceText = (TextView) customView.findViewById(R.id.taxiSourceText);
        TextView taxiDestinationText = (TextView) customView.findViewById(R.id.taxiDestinationText);
        ArrayList<String> taxiInfo = new ArrayList<String>();
        taxiInfo = getItem(position);
        taxiSourceText.append(taxiInfo.get(0));
        taxiDestinationText.append(taxiInfo.get(1));
        taxiNumberText.setTypeface(font_bold);
        taxiSourceText.setTypeface(font);
        taxiDestinationText.setTypeface(font);

        if(position % 3 == 0) {
            taxiNumberBackground.setBackgroundResource(R.drawable.background_square_yellow);
        }
        else if(position % 3 == 1) {
            taxiNumberBackground.setBackgroundResource(R.drawable.background_square_blue);
        }
        else {
            taxiNumberBackground.setBackgroundResource(R.drawable.background_square_green);
        }
        return customView;
    }
}
