package com.thebrokegamers.kolkatanavigator;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class MetroFragment extends Fragment {

    private Calendar c = Calendar.getInstance();
    private RelativeLayout metroButtonLeft;
    private RelativeLayout metroButtonCenterLeft;
    private RelativeLayout metroButtonCenterRight;
    private RelativeLayout metroButtonRight;
    private RelativeLayout searchMetro;
    private TextView metroButtonLeftText;
    private TextView metroButtonCenterLeftText;
    private TextView metroButtonCenterRightText;
    private TextView metroButtonRightText;
    private TextView searchText;
    private TestAdapter metroDbResults;
    private UserDatabaseAdapter metroRecents;
    private static int mYear, mMonth, mDay, mHour, mMinutes;
    private ArrayList<ArrayList<String>> metros;
    private ArrayAdapter<ArrayList<String>> metroAdapter;
    private ArrayAdapter<String> metroStationAdapter;
    private ListView metroList;
    private ArrayList<String> autoCompleteSourceTemp;
    private ArrayList<String> autoCompleteDestinationTemp;
    private AutoCompleteTextView actv;
    private AutoCompleteTextView actvd;
    private ImageView sourceDestinationIcon;
    private ImageView reversibleArrows;
    private boolean upDown;
    private int pos;
    private Typeface font;
    private TextInputLayout topText;
    private TextInputLayout bottomText;
    private ArrayList<String> temp = new ArrayList<String>(AddMetroStations());
    private boolean metroStation;
    private ArrayList<String> metroLatLng;


    public MetroFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_metro, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
    }

    private void initUI(View view) {
        metroStation = false;
        initialiseMetroLatLng();
        Bundle bundle = this.getArguments();
        font = Typeface.createFromAsset(getActivity().getAssets(), "Exo2-Regular.ttf");
        metroDbResults = new TestAdapter(getActivity());
        metroRecents = new UserDatabaseAdapter(getActivity());
        metros = new ArrayList<ArrayList<String>>();
        metroAdapter = new MetroListAdapter(getActivity(), metros);
        metroStationAdapter = new MetroStationListAdapter(getActivity(), new ArrayList<String>(AddMetroStations()));
        metroList = (ListView) view.findViewById(R.id.MetroList);
        metroList.setAdapter(metroAdapter);
        actv = (AutoCompleteTextView) view.findViewById(R.id.sourceInput);
        actv.setTypeface(font);
        actvd = (AutoCompleteTextView) view.findViewById(R.id.destinationInput);
        actvd.setTypeface(font);
        actv.setThreshold(1);//will start working from first character
        actvd.setThreshold(1);
        actv.setAdapter(new CustomArrayAdapter
                (this.getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>(AddMetroStations())));
        actvd.setAdapter(new CustomArrayAdapter
                (this.getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>(AddMetroStations())));
        topText = (TextInputLayout) view.findViewById(R.id.top_text);
        topText.setTypeface(font);
        bottomText = (TextInputLayout) view.findViewById(R.id.bottom_text);
        bottomText.setTypeface(font);
        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinutes = c.get(Calendar.MINUTE);
        sourceDestinationIcon = (ImageView) view.findViewById(R.id.sourceDestinationImage);
        actv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sourceDestinationIcon.setImageResource(R.drawable.source_destination_icon);
                autoCompleteSourceTemp = new ArrayList<String>(AddMetroStations());
                String ch = actvd.getText().toString();
                if (!ch.equals("") && ch.length() > 0) {
                    if (autoCompleteSourceTemp.contains(ch)) {
                        autoCompleteSourceTemp.remove(ch);
                    }
                }
                actv.setAdapter(new CustomArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, autoCompleteSourceTemp));
                return false;
            }
        });

        actvd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sourceDestinationIcon.setImageResource(R.drawable.source_destination_icon_reverse);
                autoCompleteDestinationTemp = new ArrayList<String>(AddMetroStations());
                String ch = actv.getText().toString();
                if (!ch.equals("") && ch.length() > 0) {
                    if (autoCompleteDestinationTemp.contains(ch)) {
                        autoCompleteDestinationTemp.remove(ch);
                    }
                }
                actvd.setAdapter(new CustomArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, autoCompleteDestinationTemp));
                return false;
            }
        });
        reversibleArrows = (ImageView) view.findViewById(R.id.reverseSourceDestination);
        reversibleArrows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!actv.getText().toString().equals("") && actv.getText().toString().length() > 0) || (!actvd.getText().toString().equals("")
                        && actvd.getText().toString().length() > 0)) {
                    String t = actv.getText().toString();
                    actv.setText(actvd.getText().toString());
                    actvd.setText(t);
                }
            }
        });
        metroButtonLeftText = (TextView) view.findViewById(R.id.MetroButtonLeftText);
        metroButtonLeftText.setTypeface(font);
        metroButtonLeft = (RelativeLayout) view.findViewById(R.id.MetroButtonLeft);
        metroButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metroStation = false;
                metroButtonLeft.setBackgroundResource(R.drawable.capsule_small_left_button);
                metroButtonLeftText.setTextColor(Color.parseColor("#1C3144"));
                metroButtonCenterLeft.setBackgroundColor(Color.parseColor("#1C3144"));
                metroButtonCenterLeftText.setTextColor(Color.parseColor("#FFFFFF"));
                metros.clear();
                metroList.setAdapter(metroAdapter);
                actvd.setFocusable(true);
                actvd.setFocusableInTouchMode(true);
                actvd.setEnabled(true);
                actv.setFocusable(true);
                actv.setFocusableInTouchMode(true);
                actv.setFocusable(true);
                actv.setEnabled(true);
                actvd.setEnabled(true);
            }
        });
        metroButtonCenterLeftText = (TextView) view.findViewById(R.id.MetroButtonCenterLeftText);
        metroButtonCenterLeftText.setTypeface(font);
        metroButtonCenterLeft = (RelativeLayout) view.findViewById(R.id.MetroButtonCenterLeft);
        metroButtonCenterLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metroStation = true;
                metroButtonLeft.setBackgroundResource(R.drawable.capsule_small_left_button_deact);
                metroButtonLeftText.setTextColor(Color.parseColor("#FFFFFF"));
                metroButtonCenterLeft.setBackgroundColor(Color.parseColor("#FFFFFF"));
                metroButtonCenterLeftText.setTextColor(Color.parseColor("#1C3144"));
                metros.clear();
                actv.setText("");
                actvd.setText("");
                actvd.setFocusable(false);
                actvd.setFocusableInTouchMode(false);
                actvd.setEnabled(false);
                actv.setFocusable(false);
                actv.setFocusableInTouchMode(false);
                actv.setEnabled(false);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                metroList.setAdapter(metroStationAdapter);
            }
        });
        metroButtonCenterRightText = (TextView) view.findViewById(R.id.MetroButtonCenterRightText);
        metroButtonCenterRightText.setTypeface(font);
        metroButtonCenterRight = (RelativeLayout) view.findViewById(R.id.MetroButtonCenterRight);
        metroButtonCenterRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinutes = c.get(Calendar.MINUTE);

                TimePickerDialog tpd = new TimePickerDialog(getActivity(), R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String h = hourOfDay + "";
                        if (hourOfDay < 10) {
                            h = "0" + hourOfDay;
                        }
                        String m = minute + "";
                        if (minute < 10) {
                            m = "0" + minute;
                        }
                        String f = h + ":" + m;
                        metroButtonCenterRightText.setText(f);
                        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                        mHour = c.get(Calendar.HOUR_OF_DAY);
                        mMinutes = c.get(Calendar.MINUTE);
                    }
                }, mHour, mMinutes, false);
                tpd.show();
            }
        });

        metroButtonRightText = (TextView) view.findViewById(R.id.MetroButtonRightText);
        metroButtonRightText.setTypeface(font);
        metroButtonRight = (RelativeLayout) view.findViewById(R.id.MetroButtonRight);
        metroButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(getActivity(), R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                c.set(year, monthOfYear, dayOfMonth);
                                mYear = c.get(Calendar.YEAR);
                                mMonth = c.get(Calendar.MONTH);
                                mDay = c.get(Calendar.DAY_OF_MONTH);
                                String b = dayOfMonth + " " + MonthToString(monthOfYear);
                                Date todayDate = new Date();
                                Log.d("BITCHDATE", todayDate.getYear() + "     " + year);
                                if (dayOfMonth == todayDate.getDate() && monthOfYear == todayDate.getMonth() && year == todayDate.getYear() + 1900) {
                                    b = "Today";
                                }
                                metroButtonRightText.setText(b);
                            }

                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });
        searchText = (TextView) view.findViewById(R.id.searchMetroText);
        searchText.setTypeface(font);
        searchMetro = (RelativeLayout) view.findViewById(R.id.searchMetro);
        searchMetro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!metroStation) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    ShowMetroResults();
                }
            }
        });

        metroList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!metroStation) {
                    removeFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    pos = position;
                    Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("FloatingFragment");
                    if (fragment == null) {
                        Class fragmentClass;
                        fragmentClass = MetroNumberFragment.class;
                        ArrayList<String> t = new ArrayList<String>(metros.get(pos));
                        Bundle bundle = new Bundle();
                        bundle.putString("metroId", t.get(3));
                        bundle.putBoolean("metroTableReverse", upDown);
                        bundle.putStringArrayList("metroStoppages", new ArrayList<String>(AddMetroStations()));
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                            fragment.setArguments(bundle);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations
                                (R.anim.slide_in, R.anim.slide_out).add(R.id.flContent, fragment, "FloatingFragment").commit();
                    }
                }
                else {
                    Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("FloatingFragment");
                    if (fragment == null) {
                        String[] singleStationLatLng = new String[2];
                        singleStationLatLng = metroLatLng.get(position).split(";");
                        Class fragmentClass;
                        fragmentClass = MetroMapFragment.class;
                        Bundle bundle = new Bundle();
                        bundle.putString("MetroHeader", temp.get(position));
                        bundle.putString("MetroLat", singleStationLatLng[0]);
                        bundle.putString("MetroLong", singleStationLatLng[1]);

                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                            fragment.setArguments(bundle);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.slide_out).add(R.id.flContent, fragment, "FloatingFragment").commit();
                    }
                }
            }}
            );

            if(bundle!=null)

            {
                actv.setText(bundle.getString("source"));
                actvd.setText(bundle.getString("destination"));
                ShowMetroResults();
            }

        }

    private void ShowMetroResults() {
        metroDbResults.open();
        metros.clear();
        if (!actv.getText().toString().equals("") && !actvd.getText().toString().equals("")
                && actv.getText().toString().length() > 0 && actvd.getText().toString().length() > 0
                && !actv.getText().toString().equals(actvd.getText().toString())) {
            upDown = true;
            //Log.d("METROINDEX", metroStations.indexOf(actv.getText().toString())
            //        + "   " + metroStations.indexOf(actvd.getText().toString()));

            if (temp.indexOf(actv.getText().toString()) >
                    temp.indexOf(actvd.getText().toString())) {
                upDown = false;
            }
            if (temp.contains(actv.getText().toString()) && temp.contains(actvd.getText().toString())) {
                metros.addAll(metroDbResults.getMetro(actv.getText().toString().toLowerCase(),
                        actvd.getText().toString().toLowerCase(), c.get(Calendar.DAY_OF_WEEK), upDown));
                Calendar d = Calendar.getInstance();
                String date = d.get(Calendar.YEAR) + " " + d.get(Calendar.MONTH) + " " + d.get(Calendar.DAY_OF_MONTH);
                Log.d("CURRENT_DATE", date);
                metroRecents.insertRecents("METRO", actv.getText().toString(), actvd.getText().toString(), date);
            }
            if (metros.size() == 0) {
                Toast.makeText(getActivity(), "Please choose source and destination stops from the available options!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), "Source or Destination cannot be empty!", Toast.LENGTH_SHORT).show();
        }
        metroAdapter.notifyDataSetChanged();
        ScrollToPosition(currentTime());
        removeFocus();

    }

    private void ScrollToPosition(String time) {
        if (metros.size() > 0) {
            int i;
            for (i = 0; i < metros.size(); i++) {
                if (!TrainListAdapter.TimeCompare(time, metros.get(i).get(0))) {
                    break;
                }
            }
            metroList.setSelection(i);
        }
    }


    public static String currentTime() {
        String h;
        String m;
        if (mHour < 10) {
            h = "0" + mHour;
        } else {
            h = mHour + "";
        }
        if (mMinutes < 10) {
            m = "0" + mMinutes;
        } else {
            m = mMinutes + "";
        }
        return h + ":" + m;
    }

    private void removeFocus() {
        actvd.setFocusable(false);
        actv.setFocusable(false);
        actvd.setFocusableInTouchMode(false);
        actv.setFocusableInTouchMode(false);
        actvd.setFocusable(true);
        actv.setFocusable(true);
        actvd.setFocusableInTouchMode(true);
        actv.setFocusableInTouchMode(true);
    }

    private void initialiseMetroLatLng () {
        metroLatLng = new ArrayList<String>();
        metroLatLng.add("22.471982;88.397877");
        metroLatLng.add("22.465962;88.391825");
        metroLatLng.add("22.464175;88.380607");
        metroLatLng.add("22.469467;88.369838");
        metroLatLng.add("22.473497;88.360873");
        metroLatLng.add("22.480957;88.346017");
        metroLatLng.add("22.494626;88.345031");
        metroLatLng.add("22.507986;88.345643");
        metroLatLng.add("22.516849;88.346170");
        metroLatLng.add("22.522533;88.346815");
        metroLatLng.add("22.533188;88.345949");
        metroLatLng.add("22.541221;88.347283");
        metroLatLng.add("22.549473;88.348925");
        metroLatLng.add("22.554712;88.350183");
        metroLatLng.add("22.564958;88.351691");
        metroLatLng.add("22.566808;88.354113");
        metroLatLng.add("22.572478;88.358764");
        metroLatLng.add("22.580867;88.361412");
        metroLatLng.add("22.587132;88.363025");
        metroLatLng.add("22.596040;88.365264");
        metroLatLng.add("22.601309;88.372571");
        metroLatLng.add("22.606252;88.387513");
        metroLatLng.add("22.620995;88.392838");
        metroLatLng.add("22.639835;88.394074");
    }

    public static ArrayList<String> AddMetroStations() {
        ArrayList<String> metroStations = new ArrayList<String>();
        metroStations.add("kavi subhash - garia station");
        metroStations.add("shahid khudiram - bridgy area");
        metroStations.add("kavi nazrul - garia bazar");
        metroStations.add("gitanjali - naktala");
        metroStations.add("masterda surya sen - bansdroni");
        metroStations.add("netaji - kudghat");
        metroStations.add("mahanayak uttam kumar - tollygunge");
        metroStations.add("rabindra sarobar");
        metroStations.add("kalighat");
        metroStations.add("jatin das park");
        metroStations.add("netaji bhaban");
        metroStations.add("rabindra sadan");
        metroStations.add("maidan");
        metroStations.add("park street");
        metroStations.add("esplanade");
        metroStations.add("chandni chowk");
        metroStations.add("central");
        metroStations.add("mahatma gandhi road");
        metroStations.add("girish park");
        metroStations.add("shobhabazar sutanuti");
        metroStations.add("shyambazar");
        metroStations.add("belgachia");
        metroStations.add("dumdum");
        metroStations.add("noapara");

        for (int i = 0; i < metroStations.size(); i++) {
            metroStations.set(i, metroStations.get(i).toUpperCase());
        }

        return metroStations;
    }



    public static String MonthToString(int m) {
        if (m == 0) {
            return "Jan";
        } else if (m == 1) {
            return "Feb";
        } else if (m == 2) {
            return "Mar";
        } else if (m == 3) {
            return "Apr";
        } else if (m == 4) {
            return "May";
        } else if (m == 5) {
            return "Jun";
        } else if (m == 6) {
            return "Jul";
        } else if (m == 7) {
            return "Aug";
        } else if (m == 8) {
            return "Sep";
        } else if (m == 9) {
            return "Oct";
        } else if (m == 10) {
            return "Nov";
        } else if (m == 11) {
            return "Dec";
        }
        return "";
    }
}
