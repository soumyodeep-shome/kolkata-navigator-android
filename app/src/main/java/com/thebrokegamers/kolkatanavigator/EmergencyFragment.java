package com.thebrokegamers.kolkatanavigator;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmergencyFragment extends Fragment {

    private int TOTAL_BUTTONS = 7;
    private ArrayList<RelativeLayout> setTouchListeners;
    private TestAdapter emDbAdapter;
    private ArrayList<ArrayList<String>> emergencyNumbers;
    private ArrayAdapter<ArrayList<String>> emergencyAdapter;
    private ListView emergencyList;

    public EmergencyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emergency, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUi(view);
    }

    private void initUi (View view) {
        emDbAdapter = new TestAdapter(getActivity());
        emDbAdapter.open();
        emergencyNumbers = new ArrayList<ArrayList<String>>();
        emergencyAdapter = new EmergencyListAdapter(getActivity(), emergencyNumbers);
        emergencyList = (ListView) view.findViewById(R.id.emergencyList);
        emergencyList.setAdapter(emergencyAdapter);
        SetButtons(view);
        SetFont(view);
        DisplayEmInfo(setTouchListeners.get(0));
        emergencyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri number = Uri.parse("tel:" + emergencyNumbers.get(position).get(1));
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });
    }

    private void SetButtons(View view) {
        setTouchListeners = new ArrayList<RelativeLayout>();
        setTouchListeners.add((RelativeLayout) view.findViewById(R.id.emergencyPoliceContainer));
        setTouchListeners.add((RelativeLayout) view.findViewById(R.id.emergencyFireStationContainer));
        setTouchListeners.add((RelativeLayout) view.findViewById(R.id.emergencyHospitalContainer));
        setTouchListeners.add((RelativeLayout) view.findViewById(R.id.emergencyWomenContainer));
        setTouchListeners.add((RelativeLayout) view.findViewById(R.id.emergencyBloodContainer));
        setTouchListeners.add((RelativeLayout) view.findViewById(R.id.emergencyTouristContainer));
        setTouchListeners.add((RelativeLayout) view.findViewById(R.id.emergencyMiscContainer));
        int i = 0;

        for (final RelativeLayout rLayout : setTouchListeners) {
            rLayout.setTag(SetEmTag(i));
            rLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            return true; // if you want to handle the touch event
                        case MotionEvent.ACTION_UP:
                            DisplayEmInfo(rLayout);
                            return true; // if you want to handle the touch event
                        case MotionEvent.ACTION_CANCEL:
                            return true; // if you want to handle the touch event

                    }
                    return false;
                }
            });
            i++;
        }
    }

    private void DisplayEmInfo(RelativeLayout rl) {
        for (int b = 0; b < TOTAL_BUTTONS; b++) {
            setTouchListeners.get(b).setBackgroundColor(Color.parseColor("#42A5F5"));
        }
        rl.setBackgroundColor(Color.parseColor("#1E88E5"));
        emergencyNumbers.clear();
        emergencyNumbers.addAll(emDbAdapter.emergencyInfo(rl.getTag().toString()));
        emergencyAdapter.notifyDataSetChanged();
    }

    private String SetEmTag (int a) {
        if (a == 0) {
            return "police";
        }
        else if (a == 1) {
            return "firestation";
        }
        else if (a == 2) {
            return "hospital";
        }
        else if (a == 3) {
            return "wsafety";
        }
        else if (a == 4) {
            return "bloodbank";
        }
        else if (a == 5) {
            return "tourist";
        }
        else {
            return "misc";
        }
    }

    private void SetFont(View view) {
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Exo2-Regular.ttf");
        ArrayList<TextView> changeFonts = new ArrayList<TextView>();
        changeFonts.add((TextView) view.findViewById(R.id.emergencyPoliceText));
        changeFonts.add((TextView) view.findViewById(R.id.emergencyFireStationText));
        changeFonts.add((TextView) view.findViewById(R.id.emergencyHospitalText));
        changeFonts.add((TextView) view.findViewById(R.id.emergencyWomenText));
        changeFonts.add((TextView) view.findViewById(R.id.emergencyBloodText));
        changeFonts.add((TextView) view.findViewById(R.id.emergencyTouristText));
        changeFonts.add((TextView) view.findViewById(R.id.emergencyMiscText));

        for (int i = 0; i < changeFonts.size(); i++) {
            changeFonts.get(i).setTypeface(font);
        }
    }


}
