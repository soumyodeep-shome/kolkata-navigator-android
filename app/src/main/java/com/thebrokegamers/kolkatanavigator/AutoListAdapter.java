package com.thebrokegamers.kolkatanavigator;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by soumyodeep on 10/27/2015.
 */
public class AutoListAdapter extends ArrayAdapter<ArrayList<String>> {

    private Typeface font;
    private Typeface fontBold;
    private Context mContext;

    public AutoListAdapter(Context context, ArrayList<ArrayList<String>> items) {
        super(context, R.layout.auto_listview, items);
        font = Typeface.createFromAsset(context.getAssets(), "Exo2-Regular.ttf");
        fontBold = Typeface.createFromAsset(context.getAssets(), "Exo2-Bold.ttf");
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater myInflater = LayoutInflater.from((getContext()));
        View customView = myInflater.inflate(R.layout.auto_listview, parent, false);

        final ArrayList<String> singleItem = new ArrayList<String>(getItem(position));
        TextView autoHolderText = (TextView) customView.findViewById(R.id.autoHolderText);
        autoHolderText.setTypeface(fontBold);

        RelativeLayout autoHolder = (RelativeLayout) customView.findViewById(R.id.autoNumberHolder);
        if ((position % 3) == 0) {
            autoHolder.setBackgroundResource(R.drawable.background_square_yellow);
        } else if ((position % 3) == 1) {
            autoHolder.setBackgroundResource(R.drawable.background_square_green);
        } else if ((position % 3) == 2) {
            autoHolder.setBackgroundResource(R.drawable.background_square_blue);
        }

        TextView autoSourceText = (TextView) customView.findViewById(R.id.autoSourceText);
        autoSourceText.setText(singleItem.get(0).toUpperCase());
        autoSourceText.setTypeface(font);
        TextView autoDestinationText = (TextView) customView.findViewById(R.id.autoDestinationText);
        autoDestinationText.setText((singleItem.get(1)).toUpperCase());
        autoDestinationText.setTypeface(font);


        final FloatingActionButton sourceButton = (FloatingActionButton) customView.findViewById(R.id.sourceFab);
        sourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, sourceButton.getText(), Toast.LENGTH_SHORT).show();
                Fragment fragment = ((FragmentActivity) mContext).getSupportFragmentManager().findFragmentByTag("FloatingFragment");
                if (fragment == null) {
                    Log.d("JONSNOWWILLRISE", singleItem.get(2) + "   " + singleItem.get(3));
                    Class fragmentClass;
                    fragmentClass = AutoMapFragment.class;
                    Bundle bundle = new Bundle();
                    bundle.putString("AutoHeader", singleItem.get(0));
                    bundle.putString("AutoLat", singleItem.get(2));
                    bundle.putString("AutoLong", singleItem.get(3));

                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                        fragment.setArguments(bundle);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.slide_out).add(R.id.flContent, fragment, "FloatingFragment").commit();
                }
            }
        });

        final FloatingActionButton destinationButton = (FloatingActionButton) customView.findViewById(R.id.destinationFab);
        destinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = ((FragmentActivity)mContext).getSupportFragmentManager().findFragmentByTag("FloatingFragment");
                if (fragment == null) {
                    Log.d("JONSNOWWILLRISEAGAIN", singleItem.get(4) + "   " + singleItem.get(5));
                    Class fragmentClass;
                    fragmentClass = AutoMapFragment.class;
                    Bundle bundle = new Bundle();
                    bundle.putString("AutoHeader", singleItem.get(1));
                    bundle.putString("AutoLat", singleItem.get(4));
                    bundle.putString("AutoLong", singleItem.get(5));

                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                        fragment.setArguments(bundle);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.slide_out).add(R.id.flContent, fragment, "FloatingFragment").commit();
                }
            }
        });

        return customView;
    }
}
