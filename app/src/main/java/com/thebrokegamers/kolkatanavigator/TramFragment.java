package com.thebrokegamers.kolkatanavigator;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class TramFragment extends Fragment {

    private AutoCompleteTextView actv;
    private AutoCompleteTextView actvd;
    private Typeface font;
    private ArrayList<ArrayList<String>> trams;
    private ListView tramList;
    private ArrayAdapter<ArrayList<String>> tramAdapter;
    private TestAdapter tramDbAdapter;
    private UserDatabaseAdapter userDbAdapter;
    private ImageView img;


    public TramFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tram, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUi(view);
    }

    private void initUi (View view) {
        Bundle bundle = this.getArguments();
        font = Typeface.createFromAsset(getActivity().getAssets(), "Exo2-Regular.ttf");
        TextInputLayout topText = (TextInputLayout) view.findViewById(R.id.top_text);
        topText.setTypeface(font);
        TextInputLayout bottomText = (TextInputLayout) view.findViewById(R.id.bottom_text);
        bottomText.setTypeface(font);
        TextView searchText = (TextView) view.findViewById(R.id.searchTramText);
        searchText.setTypeface(font);
        actv = (AutoCompleteTextView) view.findViewById(R.id.sourceInput);
        actv.setTypeface(font);
        actvd = (AutoCompleteTextView) view.findViewById(R.id.destinationInput);
        actvd.setTypeface(font);
        actv.setThreshold(1);//will start working from first character
        actvd.setThreshold(1);
        actv.setAdapter(new CustomArrayAdapter
                (this.getActivity(), android.R.layout.simple_list_item_1, AddWordsTram()));
        actvd.setAdapter(new CustomArrayAdapter
                (this.getActivity(), android.R.layout.simple_list_item_1, AddWordsTram()));
        img = (ImageView) view.findViewById(R.id.sourceDestinationImage);
        actv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                img.setImageResource(R.drawable.source_destination_icon);
                return false;
            }
        });

        actvd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                img.setImageResource(R.drawable.source_destination_icon_reverse);
                return false;
            }
        });
        tramDbAdapter = new TestAdapter(getActivity());
        userDbAdapter = new UserDatabaseAdapter(getActivity());
        tramDbAdapter.open();
        tramList = (ListView) view.findViewById(R.id.tramList);
        trams = new ArrayList<ArrayList<String>>();
        tramAdapter = new TramListAdapter(getActivity(), trams);
        tramList.setAdapter(tramAdapter);

        RelativeLayout searchTrams = (RelativeLayout) view.findViewById(R.id.searchTram);
        searchTrams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                initTramResults();
            }
        });

        if (bundle != null) {
            actv.setText(bundle.getString("source"));
            actvd.setText(bundle.getString("destination"));
            initTramResults();
        }
    }

    private void initTramResults () {

        if (!actv.getText().toString().equals("") && !actvd.getText().toString().equals("")
                && actv.getText().toString().length() > 0 && actvd.getText().toString().length() > 0) {
            trams.clear();
            trams.addAll(tramDbAdapter.tramInfo(actv.getText().toString(), actvd.getText().toString()));
            if (trams.size() == 0) {
                Toast.makeText(getActivity(), "No Trams Found!", Toast.LENGTH_SHORT).show();
            }
            else {
                Calendar c = Calendar.getInstance();
                String date = c.get(Calendar.YEAR) + " " + c.get(Calendar.MONTH) + " " + c.get(Calendar.DAY_OF_MONTH);
                userDbAdapter.insertRecents("TRAM", actv.getText().toString(), actvd.getText().toString(), date);
            }
            tramAdapter.notifyDataSetChanged();

        } else {
            Toast.makeText(getActivity(), "Source or Destination cannot be empty!", Toast.LENGTH_SHORT).show();
        }
    }
    
    public static ArrayList<String> AddWordsTram () {
        ArrayList<String> tramStoppages = new ArrayList<String>();
        tramStoppages.add("belgatchia");
        tramStoppages.add("shyambazar");
        tramStoppages.add("howrah bridge");
        tramStoppages.add("esplanade");
        tramStoppages.add("rajabazar");
        tramStoppages.add("bidhannagar");
        tramStoppages.add("parkcircus");
        tramStoppages.add("gariahat");
        tramStoppages.add("khidderpore");
        tramStoppages.add("tollygunge");
        tramStoppages.add("college street");
        tramStoppages.add("lenin sarani");
        tramStoppages.add("bb ganguly street");
        tramStoppages.add("bbd bag");
        tramStoppages.add("chitpur");
        tramStoppages.add("shovabazar");
        tramStoppages.add("mg road");
        tramStoppages.add("bagbazar");
        tramStoppages.add("galiff street");
        tramStoppages.add("maniktola");
        tramStoppages.add("rafi ahamed kidwai road");
        tramStoppages.add("ajc bose road");
        tramStoppages.add("sealdah");
        tramStoppages.add("kankurgachi");
        tramStoppages.add("mominpur");
        tramStoppages.add("gopalnagar");
        tramStoppages.add("hazra");
        tramStoppages.add("wattgunge");
        tramStoppages.add("maidan");
        tramStoppages.add("moulali");

        for (int i = 0; i < tramStoppages.size(); i++) {
            tramStoppages.set(i, tramStoppages.get(i).toUpperCase());
        }

        return tramStoppages;
    }


}
