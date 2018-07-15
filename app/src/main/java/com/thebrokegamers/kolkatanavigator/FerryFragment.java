package com.thebrokegamers.kolkatanavigator;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FerryFragment extends Fragment {

    private ArrayList<ArrayList<String>> ferries;
    private ArrayAdapter<ArrayList<String>> ferryAdapter;
    private ListView ferryList;
    private TestAdapter ferryDbAdapter;

    public FerryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ferry, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ferryDbAdapter = new TestAdapter(getActivity());
        ferryDbAdapter.open();
        ferries = new ArrayList<ArrayList<String>>(ferryDbAdapter.ferryInfo());
        ferryAdapter = new FerryListAdapter(getActivity(), ferries);
        ferryList = (ListView) view.findViewById(R.id.ferryList);
        ferryList.setAdapter(ferryAdapter);
    }
}
