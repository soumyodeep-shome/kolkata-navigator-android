package com.thebrokegamers.kolkatanavigator;


import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class BusNumberFragment extends Fragment {

    ImageView busNumberTranslucentBackground;
    TextView busNumberIconText;
    TextView busNumberCloseButton;
    ListView busStoppagesList;
    Fragment fragment = null;
    TestAdapter ta;
    Cursor busCursor;
    String [] stoppages = new String[100];
    String topText;
    String busNumber;
    String busOperator;
    Bundle bundle;
    ArrayAdapter<String> adapter;
    ArrayList<String> busStoppages;

    public BusNumberFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bus_number, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUi(view);
    }

    private void initUi (View view) {
        bundle = this.getArguments();
        busNumber = bundle.getString("busNumber");
        busNumberTranslucentBackground = (ImageView) view.findViewById(R.id.busNumberTranslucentBackground);
        busNumberIconText = (TextView) view.findViewById(R.id.busNumberIconText);
        busNumberIconText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "Exo2-Bold.ttf"));
        busStoppagesList = (ListView) view.findViewById(R.id.busNumberList);
        busStoppages = new ArrayList<String>();
        adapter = new BusNumberListAdapter(getActivity(), busStoppages);
        busStoppagesList.setAdapter(adapter);
        AlphaAnimation animation1 = new AlphaAnimation(0.0f, 0.6f);
        animation1.setDuration(1000);
        animation1.setStartOffset(200);
        animation1.setFillAfter(true);
        busNumberTranslucentBackground.startAnimation(animation1);

        try {
            fragment = getActivity().getSupportFragmentManager().findFragmentByTag("FloatingFragment");
        } catch (Exception e) {
            e.printStackTrace();
        }
        busNumberCloseButton = (TextView) view.findViewById(R.id.busNumberCloseButton);
        busNumberCloseButton.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "Exo2-Regular.ttf"));
        busNumberCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.slide_out).remove(fragment).commit();
            }
        });
        ta = new TestAdapter(getActivity());
        ta.open();
        busCursor = ta.getBusInfo(busNumber.toLowerCase());
        busCursor.moveToFirst();
        stoppages = busCursor.getString(1).split(";");
        busOperator = busCursor.getString(0).toUpperCase();
        topText = busNumber + " (" + busOperator + ")";
        busNumberIconText.setText(topText);
        populateListView(stoppages);
    }

    private void populateListView (String [] b) {
        for (int i = 0; i < b.length; i++) {
            if (!b[i].equals("") && b[i].length() > 0) {
                busStoppages.add(b[i].toUpperCase());
            }
        }
        adapter.notifyDataSetChanged();
    }
}
