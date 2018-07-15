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
public class MetroNumberFragment extends Fragment {

    ImageView metroNumberTranslucentBackground;
    TextView metroNumberCloseButton;
    TextView metroNumberIconText;
    ListView metroStoppagesList;
    Fragment fragment = null;
    TestAdapter ta;
    Cursor metroCursor;
    String metroId;
    Bundle bundle;
    ArrayAdapter<String[]> adapter;
    ArrayList<String[]> metroListInfo;
    ArrayList<String> metroStoppages;
    Boolean metroTableReverse;
    Typeface font;
    Typeface fontBold;

    public MetroNumberFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_metro_number, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUi(view);
    }

    private void initUi(View view) {
        font = Typeface.createFromAsset(getActivity().getAssets(), "Exo2-Regular.ttf");
        fontBold = Typeface.createFromAsset(getActivity().getAssets(), "Exo2-Bold.ttf");
        metroNumberIconText = (TextView) view.findViewById(R.id.metroNumberIconText);
        metroNumberIconText.setTypeface(fontBold);
        metroNumberTranslucentBackground = (ImageView) view.findViewById(R.id.metroNumberTranslucentBackground);
        AlphaAnimation animation1 = new AlphaAnimation(0.0f, 0.6f);
        animation1.setDuration(1000);
        animation1.setStartOffset(200);
        animation1.setFillAfter(true);
        metroNumberTranslucentBackground.startAnimation(animation1);
        try {
            fragment = getActivity().getSupportFragmentManager().findFragmentByTag("FloatingFragment");
        } catch (Exception e) {
            e.printStackTrace();
        }
        metroNumberCloseButton = (TextView) view.findViewById(R.id.metroNumberCloseButton);
        metroNumberCloseButton.setTypeface(font);
        metroNumberCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.slide_out).remove(fragment).commit();
            }
        });

        bundle = this.getArguments();
        metroId = bundle.getString("metroId");
        metroTableReverse = bundle.getBoolean("metroTableReverse");
        metroStoppages = bundle.getStringArrayList("metroStoppages");


        metroStoppagesList = (ListView) view.findViewById(R.id.metroNumberList);
        metroListInfo = new ArrayList<String[]>();
        adapter = new TrainNumberListAdapter(getActivity(), metroListInfo);
        metroStoppagesList.setAdapter(adapter);
        ta = new TestAdapter(getActivity());
        ta.open();
        metroCursor = ta.metroInfo(metroId);

        populateListView(metroCursor);
    }

    private void populateListView(Cursor cursor) {
        cursor.moveToFirst();

        if (metroTableReverse) {

            for (int i = 3; i < cursor.getColumnCount(); i++) {
                if (!cursor.getString(i).equals("") && cursor.getString(i).length() > 0) {
                    String s[] = new String[2];
                    s[0] = cursor.getString(i);
                    if(s[0].length() == 4) {
                        s[0] = "0" + s[0];
                    }
                    s[1] = metroStoppages.get(i - 3).toUpperCase();
                    metroListInfo.add(s);
                }
            }
        }
        else {
            for (int i = cursor.getColumnCount() - 1; i >= 3 ; i--) {
                if (!cursor.getString(i).equals("") && cursor.getString(i).length() > 0) {
                    String s[] = new String[2];
                    s[0] = cursor.getString(i);
                    s[1] = metroStoppages.get(i - 3).toUpperCase();
                    metroListInfo.add(s);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
