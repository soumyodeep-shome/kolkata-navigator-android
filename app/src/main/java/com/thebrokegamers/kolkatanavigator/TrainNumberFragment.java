package com.thebrokegamers.kolkatanavigator;


import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainNumberFragment extends Fragment {

    ImageView trainNumberTranslucentBackground;
    TextView trainNumberIconText;
    TextView trainNumberCloseButton;
    ListView trainStoppagesList;
    Fragment fragment = null;
    TestAdapter ta;
    Cursor trainCursor;
    String topText;
    String trainNumber;
    String trainLine;
    Bundle bundle;
    ArrayAdapter<String[]> adapter;
    ArrayList<String[]> trainListInfo;
    ArrayList<String> trainStoppages;
    Boolean trainTableReverse;
    Typeface font;
    Typeface fontBold;

    public TrainNumberFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_train_number, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUi(view);
    }

    private void initUi (View view) {
        font = Typeface.createFromAsset(getActivity().getAssets(), "Exo2-Regular.ttf");
        fontBold = Typeface.createFromAsset(getActivity().getAssets(), "Exo2-Bold.ttf");
        trainNumberTranslucentBackground = (ImageView) view.findViewById(R.id.trainNumberTranslucentBackground);
        AlphaAnimation animation1 = new AlphaAnimation(0.0f, 0.6f);
        animation1.setDuration(1000);
        animation1.setStartOffset(200);
        animation1.setFillAfter(true);
        trainNumberTranslucentBackground.startAnimation(animation1);
        try {
            fragment = getActivity().getSupportFragmentManager().findFragmentByTag("FloatingFragment");
        } catch (Exception e) {
            e.printStackTrace();
        }
        trainNumberCloseButton = (TextView) view.findViewById(R.id.trainNumberCloseButton);
        trainNumberCloseButton.setTypeface(font);
        trainNumberCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.slide_out).remove(fragment).commit();
            }
        });

        bundle = this.getArguments();
        trainNumber = bundle.getString("trainNumber");
        trainLine = bundle.getString("trainLine");
        trainTableReverse = bundle.getBoolean("trainTableReverse");
        trainStoppages = bundle.getStringArrayList("trainList");
        if (!trainTableReverse) {
            Collections.reverse(trainStoppages);
        }
        Log.d("TRAINBITCH", trainNumber + "   " + trainNumber);

        trainNumberIconText = (TextView) view.findViewById(R.id.trainNumberIconText);
        trainNumberIconText.setTypeface(fontBold);
        trainStoppagesList = (ListView) view.findViewById(R.id.trainNumberList);
        trainListInfo = new ArrayList<String[]>();
        adapter = new TrainNumberListAdapter(getActivity(), trainListInfo);
        trainStoppagesList.setAdapter(adapter);
        ta = new TestAdapter(getActivity());
        ta.open();
        trainCursor = ta.trainNumberInfo(trainNumber, trainLine);
        topText = trainNumber;
        trainNumberIconText.setText(topText);
        populateListView(trainCursor);
    }

    private void populateListView (Cursor cursor) {
        cursor.moveToFirst();
        //trainStoppages = new ArrayList<String>(trainFragment.trainList.get(trainTableIndex));
        //if (!trainTableReverse) {
        //    Collections.reverse(trainStoppages);
        //
        //}

        for (int i = 4; i < cursor.getColumnCount(); i++) {
            if (!cursor.getString(i).equals("") && cursor.getString(i).length() > 0) {
                //trainListInfo.add(cursor.getString(i).replace('"', ':') + "   " + trainStoppages.get(i));
                String s[] = new String[2];
                s[0] = cursor.getString(i).replace('"', ':');
                s[1] = trainStoppages.get(i-4).toUpperCase().replace("-", " - ");
                trainListInfo.add(s);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
