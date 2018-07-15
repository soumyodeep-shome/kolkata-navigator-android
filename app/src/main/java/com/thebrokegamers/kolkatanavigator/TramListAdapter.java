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
 * Created by g0ldfighter on 11/20/2015.
 */
public class TramListAdapter extends ArrayAdapter<ArrayList<String>> {
    private Typeface font;
    private Typeface fontBold;

    public TramListAdapter(Context context, ArrayList<ArrayList<String>> trams) {
        super(context, R.layout.tram_listview, trams);
        font = Typeface.createFromAsset(context.getAssets(), "Exo2-Regular.ttf");
        fontBold = Typeface.createFromAsset(context.getAssets(), "Exo2-Bold.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater tramInflater = LayoutInflater.from(getContext());
        View customView = tramInflater.inflate(R.layout.tram_listview, parent, false);
        ArrayList<String> info = getItem(position);
        ArrayList<TextView> tramTexts = new ArrayList<TextView>();
        tramTexts.add((TextView) customView.findViewById(R.id.tramNumberText));
        tramTexts.add((TextView) customView.findViewById(R.id.tramDepotText));
        tramTexts.add((TextView) customView.findViewById(R.id.tramWayText));
        tramTexts.add((TextView) customView.findViewById(R.id.tramFirstText));
        tramTexts.add((TextView) customView.findViewById(R.id.tramLastText));

        tramTexts.get(0).setTypeface(fontBold);
        tramTexts.get(0).setText(info.get(0));

        for (int i = 1; i < 5; i++) {
            tramTexts.get(i).setTypeface(font);
            tramTexts.get(i).append(info.get(i));
        }

        RelativeLayout tramHolder = (RelativeLayout) customView.findViewById(R.id.tramNumberHolder);

        if(position % 3 == 0) {
            tramHolder.setBackgroundResource(R.drawable.background_square_yellow);
        }
        else if(position % 3 == 1) {
            tramHolder.setBackgroundResource(R.drawable.background_square_blue);
        }
        else {
            tramHolder.setBackgroundResource(R.drawable.background_square_green);
        }

        return customView;
    }
}
