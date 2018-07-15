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
 * Created by g0ldfighter on 11/10/2015.
 */
public class HomeFavouritesListAdapter extends ArrayAdapter<ArrayList<String>> {

    private Typeface font;
    private Typeface fontBold;
    private ArrayList<ArrayList<String>> info;

    public HomeFavouritesListAdapter(Context context, ArrayList<ArrayList<String>> objects) {
        super(context, R.layout.home_favourites_listview, objects);
        font = Typeface.createFromAsset(context.getAssets(), "Exo2-Regular.ttf");
        fontBold = Typeface.createFromAsset(context.getAssets(), "Exo2-Bold.ttf");
        info = new ArrayList<ArrayList<String>>(objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater homeListInflater = LayoutInflater.from(getContext());
        View customView = homeListInflater.inflate(R.layout.home_favourites_listview, parent, false);
        RelativeLayout homeTransportBackground = (RelativeLayout) customView.findViewById(R.id.homeTransportNumberHolder);
        TextView transportName = (TextView) customView.findViewById(R.id.homeTransportNameText);
        transportName.setTypeface(fontBold);
        transportName.setText(info.get(position).get(1));
        setTransportImage(info.get(position).get(1), customView);
        TextView sourceText = (TextView) customView.findViewById(R.id.homeSourceText);
        sourceText.setTypeface(font);
        sourceText.setText(info.get(position).get(2));
        TextView destinationText = (TextView) customView.findViewById(R.id.homeDestinationText);
        destinationText.setTypeface(font);
        destinationText.setText(info.get(position).get(3));

        if (position % 3 == 0) {
            homeTransportBackground.setBackgroundResource(R.drawable.background_square_yellow);
        } else if (position % 3 == 1) {
            homeTransportBackground.setBackgroundResource(R.drawable.background_square_blue);
        } else {
            homeTransportBackground.setBackgroundResource(R.drawable.background_square_green);
        }

        return customView;
    }

    private void setTransportImage (String s, View view) {
        ImageView sd = (ImageView) view.findViewById(R.id.homeTransportImage);
        s = s.toLowerCase();
        if (s.equals("bus")) {
            sd.setImageResource(R.drawable.ic_directions_bus_black_24dp);
        }
        else if (s.equals("metro")) {
            sd.setImageResource(R.drawable.ic_directions_subway_black_24dp);
        }
        else if (s.equals("auto")) {
            sd.setImageResource(R.drawable.auto_icon);
        }
        else if (s.equals("train")) {
            sd.setImageResource(R.drawable.ic_directions_railway_black_24dp);
        }
        else if (s.equals("taxi")) {
            sd.setImageResource(R.drawable.ic_local_taxi_black_24dp);
        }
    }
}
