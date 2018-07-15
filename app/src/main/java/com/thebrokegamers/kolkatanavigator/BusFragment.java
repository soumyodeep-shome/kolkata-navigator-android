package com.thebrokegamers.kolkatanavigator;


import android.content.Context;
import android.database.Cursor;
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

public class BusFragment extends Fragment {

    private TestAdapter busDbResults;
    private UserDatabaseAdapter busRecents;
    private Typeface font;
    private ArrayList<String> busNumberAll;
    private ArrayList<String> busStoppagesAll;
    private AutoCompleteTextView actv;
    private AutoCompleteTextView actvd;
    private ImageView img;
    private RelativeLayout searchBus;
    private TextView searchBusText;
    private RelativeLayout busButtonLeft;
    private TextView busButtonLeftText;
    private RelativeLayout busButtonCenterLeft;
    private TextView busButtonCenterLeftText;
    private RelativeLayout busButtonCenterRight;
    private TextView busButtonCenterRightText;
    private RelativeLayout busButtonRight;
    private TextView busButtonRightText;
    private ListView busAToBList;
    private ArrayAdapter<String[]> busAdapter;
    private ArrayList<String[]> busResults;
    private boolean busAToB;
    private boolean busTypeAC;
    private boolean busTypeNonAC;
    private TextInputLayout topText;
    private TextInputLayout bottomText;
    private int pos;


    public BusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_bus, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
    }

    private void initUI(View view) {
        Bundle bundle = this.getArguments();
        AddWordsBusNumber();
        busStoppagesAll = new ArrayList<String>(AddWordsBusStoppages());
        font = Typeface.createFromAsset(getActivity().getAssets(), "Exo2-Regular.ttf");
        topText = (TextInputLayout) view.findViewById(R.id.top_text);
        topText.setTypeface(font);
        bottomText = (TextInputLayout) view.findViewById(R.id.bottom_text);
        bottomText.setTypeface(font);
        searchBusText = (TextView) view.findViewById(R.id.searchBusText);
        searchBusText.setTypeface(font);
        actv = (AutoCompleteTextView) view.findViewById(R.id.sourceInput);
        actv.setTypeface(font);
        actvd = (AutoCompleteTextView) view.findViewById(R.id.destinationInput);
        actvd.setTypeface(font);
        actv.setThreshold(1);//will start working from first character
        actvd.setThreshold(1);
        actv.setAdapter(new CustomArrayAdapter
                (this.getActivity(), android.R.layout.simple_list_item_1, busStoppagesAll));
        actvd.setAdapter(new CustomArrayAdapter
                (this.getActivity(), android.R.layout.simple_list_item_1, busStoppagesAll));


        img = (ImageView) view.findViewById(R.id.sourceDestinationImage);
        busDbResults = new TestAdapter(getActivity());
        busRecents = new UserDatabaseAdapter(getActivity());
        busResults = new ArrayList<String[]>();
        busAdapter = new BusListAdapter(getActivity(), busResults);
        busAToBList = (ListView) view.findViewById(R.id.busList);
        busAToBList.setAdapter(busAdapter);
        busAToBList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RemoveFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                pos = position;
                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("FloatingFragment");
                if (fragment == null) {
                    Class fragmentClass;
                    fragmentClass = BusNumberFragment.class;
                    Bundle bundle = new Bundle();
                    bundle.putString("busNumber", busResults.get(pos)[0].toUpperCase());
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                        fragment.setArguments(bundle);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations
                            (R.anim.slide_in, R.anim.slide_out).add(R.id.flContent, fragment, "FloatingFragment").commit();
                }
            }
        });
        busButtonLeftText = (TextView) view.findViewById(R.id.busButtonLeftText);
        busButtonLeftText.setTypeface(font);
        busButtonCenterLeftText = (TextView) view.findViewById(R.id.busButtonCenterLeftText);
        busButtonCenterLeftText.setTypeface(font);
        busButtonCenterRightText = (TextView) view.findViewById(R.id.busButtonCenterRightText);
        busButtonCenterRightText.setTypeface(font);
        busButtonRightText = (TextView) view.findViewById(R.id.busButtonRightText);
        busButtonRightText.setTypeface(font);
        busAToB = true;
        busTypeAC = false;
        busTypeNonAC = false;


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

        searchBus = (RelativeLayout) view.findViewById(R.id.searchBus);
        searchBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (busAToB) {
                    if (!actv.getText().toString().equals("") && !actvd.getText().toString().equals("")
                            && actv.getText().toString().length() > 0 && actvd.getText().toString().length() > 0) {
                        initBusResults(actv.getText().toString(), actvd.getText().toString(), busTypeAC, busTypeNonAC);

                        //busDbResults.getTestData();
                    } else {
                        Toast.makeText(getActivity(), "Source or Destination cannot be empty!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!actv.getText().toString().equals("")) {
                        busDbResults.open();

                        Cursor cursor = busDbResults.getBusInfo(actv.getText().toString().toLowerCase());
                        if (cursor.getCount() > 0) {
                            Fragment fragment = null;
                            Class fragmentClass;
                            fragmentClass = BusNumberFragment.class;
                            Bundle bundle = new Bundle();
                            bundle.putString("busNumber", actv.getText().toString().toUpperCase());
                            try {
                                fragment = (Fragment) fragmentClass.newInstance();
                                fragment.setArguments(bundle);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations
                                    (R.anim.slide_in, R.anim.slide_out).add(R.id.flContent, fragment, "FloatingFragment").commit();
                        } else {
                            Toast.makeText(getActivity(), "This bus number doesn't exist!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        busButtonLeft = (RelativeLayout) view.findViewById(R.id.busButtonLeft);
        busButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!busAToB) {
                    busResults.clear();
                    busAdapter.notifyDataSetChanged();
                    busAToB = true;
                    busButtonLeft.setBackgroundResource(R.drawable.capsule_small_left_button);
                    busButtonLeftText.setTextColor(Color.parseColor("#1C3144"));
                    busButtonCenterLeft.setBackgroundColor(Color.parseColor("#1C3144"));
                    busButtonCenterLeftText.setTextColor(Color.parseColor("#FFFFFF"));
                    topText.setHint("Enter Source...");
                    actv.setAdapter(new CustomArrayAdapter
                            (getActivity(), android.R.layout.simple_list_item_1, busStoppagesAll));
                    actvd.setAdapter(new CustomArrayAdapter
                            (getActivity(), android.R.layout.simple_list_item_1, busStoppagesAll));
                    actvd.setText("");
                    actv.setText("");
                    actvd.setEnabled(true);
                    RemoveFocus();
                }
            }
        });

        busButtonCenterLeft = (RelativeLayout) view.findViewById(R.id.busButtonCenterLeft);
        busButtonCenterLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (busAToB) {
                    busResults.clear();
                    busAdapter.notifyDataSetChanged();
                    busAToB = false;
                    busButtonLeft.setBackgroundResource(R.drawable.capsule_small_left_button_deact);
                    busButtonLeftText.setTextColor(Color.parseColor("#FFFFFF"));
                    busButtonCenterLeft.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    busButtonCenterLeftText.setTextColor(Color.parseColor("#1C3144"));
                    actv.setText("");
                    actvd.setText("");
                    topText.setHint("Enter Bus Number...");
                    actv.setAdapter(new CustomArrayAdapter
                            (getActivity(), android.R.layout.simple_list_item_1, busNumberAll));
                    actvd.setFocusable(false);
                    actvd.setFocusableInTouchMode(false);
                    actvd.setEnabled(false);
                    actv.setFocusable(false);
                    actv.setFocusableInTouchMode(false);
                    actv.setFocusable(true);
                    actv.setFocusableInTouchMode(true);
                    img.setImageResource(R.drawable.source_destination_icon);

                }
            }
        });

        busButtonCenterRight = (RelativeLayout) view.findViewById(R.id.busButtonCenterRight);
        busButtonCenterRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                busTypeAC = !busTypeAC;
                if (busTypeAC) {
                    busButtonCenterRight.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    busButtonCenterRightText.setTextColor(Color.parseColor("#1C3144"));
                } else {
                    busButtonCenterRight.setBackgroundColor(Color.parseColor("#1C3144"));
                    busButtonCenterRightText.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if (!actv.getText().toString().equals("") && !actvd.getText().toString().equals("")
                        && actv.getText().toString().length() > 0 && actvd.getText().toString().length() > 0) {
                    initBusResults(actv.getText().toString(), actvd.getText().toString(), busTypeAC, busTypeNonAC);
                }
            }
        });

        busButtonRight = (RelativeLayout) view.findViewById(R.id.busButtonRight);
        busButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                busTypeNonAC = !busTypeNonAC;
                if (busTypeNonAC) {
                    busButtonRight.setBackgroundResource(R.drawable.capsule_small_right_button);
                    busButtonRightText.setTextColor(Color.parseColor("#1C3144"));
                } else {
                    busButtonRight.setBackgroundResource(R.drawable.capsule_small_right_button_deact);
                    busButtonRightText.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if (!actv.getText().toString().equals("") && !actvd.getText().toString().equals("")
                        && actv.getText().toString().length() > 0 && actvd.getText().toString().length() > 0) {
                    initBusResults(actv.getText().toString(), actvd.getText().toString(), busTypeAC, busTypeNonAC);
                }
            }
        });

        if (bundle != null) {
            actv.setText(bundle.getString("source"));
            actvd.setText(bundle.getString("destination"));
            initBusResults(actv.getText().toString(), actvd.getText().toString(), busTypeAC, busTypeNonAC);
        }
    }


    private void initBusResults(String A, String B, Boolean bTypeAC, Boolean bTypeNonAC) {
        busDbResults.open();
        busResults.clear();
        Cursor busCursor = busDbResults.getAToBBuses(A, B, bTypeAC, bTypeNonAC);
        if (busCursor.getCount() > 0) {
            Calendar c = Calendar.getInstance();
            String date = c.get(Calendar.YEAR) + " " + c.get(Calendar.MONTH) + " " + c.get(Calendar.DAY_OF_MONTH);
            Log.d("CURRENT_DATE", date);
            busRecents.insertRecents("BUS", A, B, date);
            while (busCursor.moveToNext()) {
                String[] temp = new String[4];
                temp[0] = busCursor.getString(0);
                temp[1] = busCursor.getString(1);
                temp[2] = busCursor.getString(2);
                temp[3] = busCursor.getString(3);
                busResults.add(temp);
            }
            busAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getActivity(), "No Buses Found!", Toast.LENGTH_SHORT).show();
        }
        busDbResults.close();
    }

    private void RemoveFocus() {
        actvd.setFocusable(false);
        actv.setFocusable(false);
        actvd.setFocusableInTouchMode(false);
        actv.setFocusableInTouchMode(false);
        actvd.setFocusable(true);
        actv.setFocusable(true);
        actvd.setFocusableInTouchMode(true);
        actv.setFocusableInTouchMode(true);
    }

    private void AddWordsBusNumber() {
        busNumberAll = new ArrayList<String>();
        busNumberAll.add("007");
        busNumberAll.add("10");
        busNumberAll.add("11");
        busNumberAll.add("11a");
        busNumberAll.add("12n");
        busNumberAll.add("13");
        busNumberAll.add("14");
        busNumberAll.add("14a");
        busNumberAll.add("15");
        busNumberAll.add("19a");
        busNumberAll.add("19b");
        busNumberAll.add("19s");
        busNumberAll.add("1a");
        busNumberAll.add("1b");
        busNumberAll.add("206");
        busNumberAll.add("214");
        busNumberAll.add("214a");
        busNumberAll.add("215a");
        busNumberAll.add("218");
        busNumberAll.add("223");
        busNumberAll.add("228");
        busNumberAll.add("234");
        busNumberAll.add("234/1");
        busNumberAll.add("257");
        busNumberAll.add("3");
        busNumberAll.add("30d");
        busNumberAll.add("31a");
        busNumberAll.add("33");
        busNumberAll.add("37");
        busNumberAll.add("37a");
        busNumberAll.add("3a/2");
        busNumberAll.add("45");
        busNumberAll.add("45a");
        busNumberAll.add("45b");
        busNumberAll.add("47b");
        busNumberAll.add("5-n");
        busNumberAll.add("56");
        busNumberAll.add("6-n");
        busNumberAll.add("603");
        busNumberAll.add("7-c");
        busNumberAll.add("700");
        busNumberAll.add("75a");
        busNumberAll.add("78");
        busNumberAll.add("78/1");
        busNumberAll.add("78c/1");
        busNumberAll.add("78e");
        busNumberAll.add("79d");
        busNumberAll.add("7a");
        busNumberAll.add("80a");
        busNumberAll.add("80b");
        busNumberAll.add("9");
        busNumberAll.add("ac1");
        busNumberAll.add("ac10");
        busNumberAll.add("ac12");
        busNumberAll.add("ac12d");
        busNumberAll.add("ac14");
        busNumberAll.add("ac15");
        busNumberAll.add("ac17");
        busNumberAll.add("ac17b");
        busNumberAll.add("ac20");
        busNumberAll.add("ac23a");
        busNumberAll.add("ac24");
        busNumberAll.add("ac25");
        busNumberAll.add("ac3");
        busNumberAll.add("ac37a");
        busNumberAll.add("ac37b");
        busNumberAll.add("ac39");
        busNumberAll.add("ac4");
        busNumberAll.add("ac41");
        busNumberAll.add("ac43");
        busNumberAll.add("ac44");
        busNumberAll.add("ac47");
        busNumberAll.add("ac48");
        busNumberAll.add("ac49");
        busNumberAll.add("ac49a");
        busNumberAll.add("ac4a");
        busNumberAll.add("ac5");
        busNumberAll.add("ac50");
        busNumberAll.add("ac51");
        busNumberAll.add("ac52");
        busNumberAll.add("ac53");
        busNumberAll.add("ac5d");
        busNumberAll.add("ac6");
        busNumberAll.add("ac6a");
        busNumberAll.add("ac9");
        busNumberAll.add("ac9a");
        busNumberAll.add("ac9b");
        busNumberAll.add("as1");
        busNumberAll.add("as1a");
        busNumberAll.add("as2");
        busNumberAll.add("as3");
        busNumberAll.add("c11/1");
        busNumberAll.add("c12");
        busNumberAll.add("c14/1");
        busNumberAll.add("c18");
        busNumberAll.add("c1a");
        busNumberAll.add("c20");
        busNumberAll.add("c23");
        busNumberAll.add("c26");
        busNumberAll.add("c27");
        busNumberAll.add("c28");
        busNumberAll.add("c29");
        busNumberAll.add("c2a");
        busNumberAll.add("c5");
        busNumberAll.add("c7");
        busNumberAll.add("c8");
        busNumberAll.add("c8a");
        busNumberAll.add("d1");
        busNumberAll.add("e1");
        busNumberAll.add("e11");
        busNumberAll.add("e12");
        busNumberAll.add("e13");
        busNumberAll.add("e13/1");
        busNumberAll.add("e14");
        busNumberAll.add("e15");
        busNumberAll.add("e16");
        busNumberAll.add("e17");
        busNumberAll.add("e18");
        busNumberAll.add("e32");
        busNumberAll.add("e4");
        busNumberAll.add("e4c");
        busNumberAll.add("e6");
        busNumberAll.add("e7");
        busNumberAll.add("e7/1");
        busNumberAll.add("jm2");
        busNumberAll.add("k1");
        busNumberAll.add("kb12");
        busNumberAll.add("kb13");
        busNumberAll.add("kb14");
        busNumberAll.add("kb15");
        busNumberAll.add("kb16");
        busNumberAll.add("kb17");
        busNumberAll.add("kb18");
        busNumberAll.add("kb19");
        busNumberAll.add("kb20");
        busNumberAll.add("kp1");
        busNumberAll.add("kp2");
        busNumberAll.add("kp3");
        busNumberAll.add("kp4");
        busNumberAll.add("kp5");
        busNumberAll.add("kp6");
        busNumberAll.add("ks1");
        busNumberAll.add("ks2");
        busNumberAll.add("ks3");
        busNumberAll.add("ks4");
        busNumberAll.add("m2");
        busNumberAll.add("m7d");
        busNumberAll.add("m7e");
        busNumberAll.add("mb1");
        busNumberAll.add("mb2");
        busNumberAll.add("mb3");
        busNumberAll.add("mb4");
        busNumberAll.add("mb6");
        busNumberAll.add("mb7");
        busNumberAll.add("mb8");
        busNumberAll.add("mh2");
        busNumberAll.add("mh3");
        busNumberAll.add("mh4");
        busNumberAll.add("midi 1");
        busNumberAll.add("midi 2");
        busNumberAll.add("mini bus 129");
        busNumberAll.add("mini bus 32");
        busNumberAll.add("mw1");
        busNumberAll.add("mw2");
        busNumberAll.add("mw3");
        busNumberAll.add("mw9");
        busNumberAll.add("nb1");
        busNumberAll.add("nb2");
        busNumberAll.add("ord1");
        busNumberAll.add("s10");
        busNumberAll.add("s10a");
        busNumberAll.add("s11");
        busNumberAll.add("s12");
        busNumberAll.add("s12c");
        busNumberAll.add("s12d");
        busNumberAll.add("s12e");
        busNumberAll.add("s13");
        busNumberAll.add("s14");
        busNumberAll.add("s14c");
        busNumberAll.add("s15");
        busNumberAll.add("s15g");
        busNumberAll.add("s16");
        busNumberAll.add("s17a");
        busNumberAll.add("s19");
        busNumberAll.add("s2");
        busNumberAll.add("s21");
        busNumberAll.add("s22");
        busNumberAll.add("s22a");
        busNumberAll.add("s23");
        busNumberAll.add("s23a");
        busNumberAll.add("s24");
        busNumberAll.add("s24n");
        busNumberAll.add("s2a");
        busNumberAll.add("s2b");
        busNumberAll.add("s30");
        busNumberAll.add("s31");
        busNumberAll.add("s32");
        busNumberAll.add("s37a");
        busNumberAll.add("s3a");
        busNumberAll.add("s3b");
        busNumberAll.add("s3w");
        busNumberAll.add("s4");
        busNumberAll.add("s42");
        busNumberAll.add("s44");
        busNumberAll.add("s45");
        busNumberAll.add("s46");
        busNumberAll.add("s47");
        busNumberAll.add("s47a");
        busNumberAll.add("s48");
        busNumberAll.add("s49");
        busNumberAll.add("s4b");
        busNumberAll.add("s4d");
        busNumberAll.add("s5");
        busNumberAll.add("s50");
        busNumberAll.add("s51");
        busNumberAll.add("s53");
        busNumberAll.add("s5c");
        busNumberAll.add("s7");
        busNumberAll.add("s9");
        busNumberAll.add("s91b");
        busNumberAll.add("s9a");
        busNumberAll.add("sd16");
        busNumberAll.add("sd28");
        busNumberAll.add("sd4");
        busNumberAll.add("sd5");
        busNumberAll.add("sd76");
        busNumberAll.add("sd8");
        busNumberAll.add("st6");
        busNumberAll.add("t1");
        busNumberAll.add("t10");
        busNumberAll.add("t10/1");
        busNumberAll.add("t11");
        busNumberAll.add("t12");
        busNumberAll.add("t2");
        busNumberAll.add("t3");
        busNumberAll.add("t8");
        busNumberAll.add("t ac1");
        busNumberAll.add("v1");
        busNumberAll.add("v10");
        busNumberAll.add("v13");
        busNumberAll.add("v15");
        busNumberAll.add("v2");
        busNumberAll.add("v20");
        busNumberAll.add("v9");
        busNumberAll.add("vs1");
        busNumberAll.add("vs10");
        busNumberAll.add("vs11");
        busNumberAll.add("vs2");
        busNumberAll.add("vs3");
        busNumberAll.add("vs4");
        busNumberAll.add("vs6");
        busNumberAll.add("vs8");
        busNumberAll.add("vs9");

        for (int i = 0; i < busNumberAll.size(); i++) {
            busNumberAll.set(i, busNumberAll.get(i).toUpperCase());
        }
    }

    public static ArrayList<String> AddWordsBusStoppages() {
        ArrayList<String> bs = new ArrayList<String>();
        bs.add("4no bridge");
        bs.add("10no pole");
        bs.add("215a bus stand");
        bs.add("acharya prafulla chandra road");
        bs.add("adamas college");
        bs.add("agarpara jute mill");
        bs.add("airport");
        bs.add("airport gate no1");
        bs.add("airport gate no2");
        bs.add("ajanta cinema");
        bs.add("ajaynagar");
        bs.add("ajc bose road");
        bs.add("akra fatak");
        bs.add("alampore");
        bs.add("alif nagar");
        bs.add("alipore");
        bs.add("alipore judges court");
        bs.add("alipore zoo");
        bs.add("amdanga");
        bs.add("amherst street");
        bs.add("amri hospital");
        bs.add("amta");
        bs.add("amtala");
        bs.add("ananda palit");
        bs.add("anandapur");
        bs.add("andul station road");
        bs.add("ankurhati check post");
        bs.add("antila bridge");
        bs.add("akanksha");
        bs.add("apollo");
        bs.add("ariadaha");
        bs.add("asutosh mukherjee road");
        bs.add("avishikta");
        bs.add("awal sidhi");
        bs.add("b.bose avenue");
        bs.add("babughat");
        bs.add("badampur");
        bs.add("badamtala");
        bs.add("bagbazar");
        bs.add("baghajatin");
        bs.add("baghpota");
        bs.add("bagmari");
        bs.add("bagnan");
        bs.add("baguiati");
        bs.add("bairampur");
        bs.add("bairmari");
        bs.add("baisakhi");
        bs.add("baishali");
        bs.add("baishnabghata");
        bs.add("bakrahat");
        bs.add("bakshara");
        bs.add("bakultala");
        bs.add("baligadha");
        bs.add("balisai");
        bs.add("ballygunge");
        bs.add("ballygunge phari");
        bs.add("ballygunge station");
        bs.add("bally");
        bs.add("bally halt");
        bs.add("bally khal");
        bs.add("bally ps");
        bs.add("baman pukur");
        bs.add("bamanghata bantala");
        bs.add("bamboo villa");
        bs.add("banamalipur");
        bs.add("bangur hospital");
        bs.add("bankra bazar");
        bs.add("bansdroni");
        bs.add("banspara");
        bs.add("bantala");
        bs.add("bantala bazar");
        bs.add("barabazar");
        bs.add("bara garcha mukh");
        bs.add("barajagulia");
        bs.add("barakatlia");
        bs.add("barasat");
        bs.add("bardhaman");
        bs.add("bargachia");
        bs.add("bargachia dharmotala");
        bs.add("bargachia hantal more");
        bs.add("bargachia level crossing");
        bs.add("bargachia station");
        bs.add("barrackpore");
        bs.add("barrackpore station");
        bs.add("barrackpore court");
        bs.add("baruipur");
        bs.add("baruipur bridge");
        bs.add("basantipur");
        bs.add("bawali");
        bs.add("bb ganguly street");
        bs.add("bbd bag");
        bs.add("beadon street");
        bs.add("beckbagan");
        bs.add("behala");
        bs.add("behala 14no bus stand");
        bs.add("behala begar khal");
        bs.add("behala chowrasta");
        bs.add("behala janakalyan");
        bs.add("behala karunamoyee");
        bs.add("behala manton");
        bs.add("behala rabindranagar");
        bs.add("behala sakherbazar");
        bs.add("behala thana");
        bs.add("behala thakurpukur");
        bs.add("behala tram depot");
        bs.add("belepole");
        bs.add("belgachia");
        bs.add("belgachia metro");
        bs.add("belgharia");
        bs.add("belgharia expressway");
        bs.add("beliaghata");
        bs.add("beliaghata bypass");
        bs.add("beliaghata crossing");
        bs.add("beliaghata main road");
        bs.add("belpukur bazar");
        bs.add("belpukur college");
        bs.add("beltala");
        bs.add("belur math");
        bs.add("bengal chemical");
        bs.add("bhabani bhawan");
        bs.add("bhabans");
        bs.add("bhojerhat");
        bs.add("bhowanipur");
        bs.add("bhupen bose avenue");
        bs.add("bhusighata");
        bs.add("bibirait");
        bs.add("bibirhat");
        bs.add("bijan setu");
        bs.add("bikash bhawan");
        bs.add("bilkanda");
        bs.add("birati");
        bs.add("birati more");
        bs.add("birati station");
        bs.add("birla");
        bs.add("birshibpur");
        bs.add("bishalaxmi tala ");
        bs.add("bishnupur ps");
        bs.add("bit college");
        bs.add("bk pal");
        bs.add("bondel gate");
        bs.add("bongaon");
        bs.add("bonhoogly");
        bs.add("boral rakhiter more");
        bs.add("boral natunhat");
        bs.add("boralighat");
        bs.add("bosepukur");
        bs.add("bosontopur");
        bs.add("bowbazar");
        bs.add("bowbazar ps");
        bs.add("brabourne road");
        bs.add("brace bridge");
        bs.add("bridge no4");
        bs.add("bt college");
        bs.add("bt road");
        bs.add("budgebudge");
        bs.add("building more");
        bs.add("buntia");
        bs.add("calcutta medical research institute");
        bs.add("cap camp");
        bs.add("carry road");
        bs.add("central");
        bs.add("central avenue");
        bs.add("chaital bridge");
        bs.add("chakdaha");
        bs.add("chakhana");
        bs.add("chamrail");
        bs.add("chandipur");
        bs.add("chanditala");
        bs.add("chandni chawk");
        bs.add("chapadali");
        bs.add("chapadali more");
        bs.add("charial");
        bs.add("charu market");
        bs.add("chaul khola");
        bs.add("chetla");
        bs.add("chinar park");
        bs.add("chingrighata");
        bs.add("chiria more");
        bs.add("chitpur");
        bs.add("chitpur crossing");
        bs.add("chittaranjan hospital");
        bs.add("chowbaga");
        bs.add("chowhati");
        bs.add("chowringhee road");
        bs.add("cipt college");
        bs.add("cit more");
        bs.add("cit road");
        bs.add("city centre");
        bs.add("city centre ii");
        bs.add("college ghat");
        bs.add("college more");
        bs.add("college street");
        bs.add("colony more");
        bs.add("contai");
        bs.add("cr avenue");
        bs.add("cujarpur");
        bs.add("cuirepore");
        bs.add("dahighat");
        bs.add("dakbangla");
        bs.add("dak bunglow more");
        bs.add("dakghar");
        bs.add("dakshineswar");
        bs.add("dakshini bari");
        bs.add("dalhousie");
        bs.add("danesh sekh lane");
        bs.add("dankuni");
        bs.add("darga road");
        bs.add("daspara");
        bs.add("dasnagar station");
        bs.add("debpukur");
        bs.add("deshapran sasmal road");
        bs.add("deshapriya park");
        bs.add("devi shetty hospital");
        bs.add("dhalai bridge");
        bs.add("dhakuria");
        bs.add("dhamakhali");
        bs.add("dhamisha");
        bs.add("dhanpota");
        bs.add("dh road");
        bs.add("dhulaguri");
        bs.add("dhulagarh");
        bs.add("diamond harbour");
        bs.add("digha");
        bs.add("disha eye hospital");
        bs.add("dlf");
        bs.add("doltala");
        bs.add("domjur");
        bs.add("domjur stand");
        bs.add("dongaria");
        bs.add("dostipur");
        bs.add("dumdum");
        bs.add("dumdum park");
        bs.add("dumdum station");
        bs.add("dumdum 7no tank");
        bs.add("dumurjola");
        bs.add("dunlop");
        bs.add("duttabagan");
        bs.add("ecospace");
        bs.add("eco park");
        bs.add("eden city");
        bs.add("edf");
        bs.add("elgin road");
        bs.add("em bypass");
        bs.add("esplanade");
        bs.add("exide");
        bs.add("fartabad");
        bs.add("foreshore road");
        bs.add("fort william");
        bs.add("furfura sharif");
        bs.add("furfuria");
        bs.add("gabberia");
        bs.add("gadiara");
        bs.add("garden reach road");
        bs.add("garfa");
        bs.add("garia");
        bs.add("gariahat");
        bs.add("garia station");
        bs.add("gangulybagan");
        bs.add("gangulypukur");
        bs.add("gd block");
        bs.add("ghatak pukur");
        bs.add("ghoshpara");
        bs.add("ghusighata");
        bs.add("ghusuri");
        bs.add("girish park");
        bs.add("gobindakhatik road");
        bs.add("gobindapur");
        bs.add("gobrachalk");
        bs.add("golabari");
        bs.add("golf club road");
        bs.add("golfgreen");
        bs.add("golpark");
        bs.add("gopalpur");
        bs.add("gopalnagar");
        bs.add("gorabazar");
        bs.add("gouribari");
        bs.add("gouripur");
        bs.add("haldirams");
        bs.add("haraharitala");
        bs.add("haridebpur");
        bs.add("harinavi");
        bs.add("hastings");
        bs.add("hatibagan");
        bs.add("hatibari");
        bs.add("hazra");
        bs.add("hidco");
        bs.add("hiland park");
        bs.add("hindustan lever");
        bs.add("hindusthan sweets");
        bs.add("hmg college");
        bs.add("hoglashi");
        bs.add("howrah");
        bs.add("howrah ac market");
        bs.add("howrah fire station");
        bs.add("howrah japani gate");
        bs.add("howrah kadamtala");
        bs.add("howrah maidan");
        bs.add("howrah station");
        bs.add("hridaypur station");
        bs.add("hudco");
        bs.add("ichapur");
        bs.add("ichanagari");
        bs.add("islampur");
        bs.add("itc more");
        bs.add("jadavpur");
        bs.add("jadavpur 8b bus stand");
        bs.add("jadavpur ps");
        bs.add("jadavpur railway station");
        bs.add("jadupir more");
        bs.add("jagadispur");
        bs.add("jagannathpur");
        bs.add("jagatballavpur");
        bs.add("jagulia");
        bs.add("jafarpur");
        bs.add("jalan gate1");
        bs.add("jalan gate2");
        bs.add("jalpol");
        bs.add("james long sarani ");
        bs.add("jamuria bridge");
        bs.add("janaghati");
        bs.add("jessore road");
        bs.add("jeevandip");
        bs.add("jinjira bazar");
        bs.add("jodhpur park");
        bs.add("jogesh chandra college");
        bs.add("jogi battala");
        bs.add("jotindra mohan avenue");
        bs.add("joynagar");
        bs.add("jangalpara");
        bs.add("jangipara");
        bs.add("joka");
        bs.add("julpia");
        bs.add("kadapara");
        bs.add("kadamtala bazar");
        bs.add("kadamtala power house");
        bs.add("kaikhali");
        bs.add("kamalgazi");
        bs.add("kamalgazi more");
        bs.add("kamdevpur");
        bs.add("kamrabad");
        bs.add("kalachara");
        bs.add("kalatala");
        bs.add("kalighat");
        bs.add("kalighat metro");
        bs.add("kalikapur");
        bs.add("kalipara");
        bs.add("kalipark");
        bs.add("kalipur");
        bs.add("kalinagar");
        bs.add("kalinagar chowmatha");
        bs.add("kalindi");
        bs.add("kalitala");
        bs.add("kalyani");
        bs.add("kalyani expressway");
        bs.add("kalyani more");
        bs.add("kamarhati");
        bs.add("kankurgachi");
        bs.add("kantalia");
        bs.add("kantalia jhawtala");
        bs.add("kantalia station");
        bs.add("karai danga");
        bs.add("karunamoyee");
        bs.add("kasba");
        bs.add("kasba depot");
        bs.add("kashipore");
        bs.add("kathgola");
        bs.add("katlia");
        bs.add("kb/kc block");
        bs.add("kd ad more");
        bs.add("kestopur");
        bs.add("kestor more");
        bs.add("khadi bhaban");
        bs.add("khajurtala");
        bs.add("khalasari");
        bs.add("khanna");
        bs.add("khardaha");
        bs.add("khardaha ps");
        bs.add("kharuberia");
        bs.add("khariberia");
        bs.add("khidirpur");
        bs.add("khidirpur depot");
        bs.add("khidirpur tram depot");
        bs.add("khidirpur dockyard");
        bs.add("khila");
        bs.add("khilkapur");
        bs.add("kodalia battala");
        bs.add("koilaghata street");
        bs.add("kolachara");
        bs.add("kolaghat");
        bs.add("kolkata leather company");
        bs.add("kolkata station");
        bs.add("kona expressway");
        bs.add("konchowki");
        bs.add("krishnarampur");
        bs.add("kudghat");
        bs.add("kulgachi");
        bs.add("kumirmora");
        bs.add("labony");
        bs.add("lake kalibari");
        bs.add("lake town");
        bs.add("lalbaba college");
        bs.add("lalbazar");
        bs.add("lalgate");
        bs.add("lalkuthi");
        bs.add("lalpool");
        bs.add("lansdown");
        bs.add("laxmibazar");
        bs.add("layalka");
        bs.add("leather complex");
        bs.add("leather complex 2");
        bs.add("leather complex 3");
        bs.add("lenin sarani");
        bs.add("liluah");
        bs.add("loha pool");
        bs.add("lords more");
        bs.add("machada");
        bs.add("madhyamgram");
        bs.add("madhyamgram chowmatha");
        bs.add("madhyamgram station");
        bs.add("mahabirtala");
        bs.add("mahajyoti sadan");
        bs.add("mahamayatala");
        bs.add("mahesh rabha bridge");
        bs.add("mahestala");
        bs.add("mahishbathan");
        bs.add("majherhat");
        bs.add("makardaha");
        bs.add("makardaha more");
        bs.add("malancha");
        bs.add("malpara");
        bs.add("manarntola");
        bs.add("mandirtala");
        bs.add("maniktala");
        bs.add("mani square");
        bs.add("marine college");
        bs.add("masat");
        bs.add("matarangi");
        bs.add("matbari");
        bs.add("mayo road");
        bs.add("metropolitan");
        bs.add("mg road");
        bs.add("mg road crossing");
        bs.add("michael nagar");
        bs.add("minakha");
        bs.add("mint");
        bs.add("minto park");
        bs.add("mirhati");
        bs.add("mohanpur");
        bs.add("mohonbati");
        bs.add("molla para");
        bs.add("mollargate");
        bs.add("mominpur");
        bs.add("moukhali");
        bs.add("moula");
        bs.add("moulali");
        bs.add("muchipara");
        bs.add("mudiali");
        bs.add("mukundapur");
        bs.add("mullickbazar");
        bs.add("munshidangar more");
        bs.add("munsirhat");
        bs.add("munsirhat station");
        bs.add("nabanna");
        bs.add("nachinda");
        bs.add("nagerbazar");
        bs.add("naipukur");
        bs.add("naktala");
        bs.add("nalmuri");
        bs.add("nandakumar");
        bs.add("nandibagan");
        bs.add("narghat");
        bs.add("narayanpur");
        bs.add("narendrapur");
        bs.add("narendrapur mandir");
        bs.add("narendrapur mission");
        bs.add("narkelbagan");
        bs.add("national highway");
        bs.add("national medical college");
        bs.add("nature park");
        bs.add("navina");
        bs.add("nayabad");
        bs.add("neemdighi");
        bs.add("neilgunj");
        bs.add("nepalgunge");
        bs.add("netaji nagar");
        bs.add("new alipur");
        bs.add("new barrackpore");
        bs.add("new garia 206 bus stand");
        bs.add("new town");
        bs.add("nh6 crossing");
        bs.add("nicco park");
        bs.add("nimta");
        bs.add("nimtonri");
        bs.add("nirmal chandra street");
        bs.add("nirmal cinema hall");
        bs.add("nodakhali");
        bs.add("nona chandan pukur");
        bs.add("noyal");
        bs.add("nrs hospital");
        bs.add("nuntia");
        bs.add("old court house street");
        bs.add("p&rd");
        bs.add("p&rd office");
        bs.add("padmapukur");
        bs.add("padmasree");
        bs.add("panihati");
        bs.add("panipara");
        bs.add("paglahut");
        bs.add("paikpara");
        bs.add("pailan");
        bs.add("palbazar");
        bs.add("pamar bazar");
        bs.add("panchanantala mandir");
        bs.add("panchasayar");
        bs.add("panchla");
        bs.add("paribesh bhawan");
        bs.add("park circus");
        bs.add("park street");
        bs.add("parnasree");
        bs.add("patihal");
        bs.add("patihal hattala");
        bs.add("patihal station");
        bs.add("patuli");
        bs.add("peerless hospital");
        bs.add("pero");
        bs.add("pg hospital");
        bs.add("phoolbagan");
        bs.add("pirtola");
        bs.add("prafulla");
        bs.add("prasadpur");
        bs.add("prince anwar shah road");
        bs.add("priya cinema");
        bs.add("pts");
        bs.add("purta bhaban");
        bs.add("rabindra bharati");
        bs.add("rabindra sadan");
        bs.add("rahara bazar");
        bs.add("raghabpur");
        bs.add("raidighi");
        bs.add("raipur");
        bs.add("rajabagan");
        bs.add("rajabazar");
        bs.add("rajapur");
        bs.add("raja road");
        bs.add("rajbari");
        bs.add("rajballav para");
        bs.add("rajberia");
        bs.add("raja subodh mallick road");
        bs.add("rajarhat");
        bs.add("rajpur");
        bs.add("rajpur bazar");
        bs.add("rajpur police station");
        bs.add("rajpur burning ghat");
        bs.add("rampur");
        bs.add("ramgarh");
        bs.add("ramnagar");
        bs.add("rangapur");
        bs.add("ranihati");
        bs.add("ranikuthi");
        bs.add("rasapunja");
        bs.add("rashbehari crossing");
        bs.add("rashbehari avenue");
        bs.add("rashmani bazar");
        bs.add("rashpur");
        bs.add("rathtala");
        bs.add("reckjuani");
        bs.add("rg kar hospital");
        bs.add("rg kar road");
        bs.add("roy bahadur road");
        bs.add("ruby");
        bs.add("ruiya");
        bs.add("sb road");
        bs.add("safuipara");
        bs.add("sahebpara");
        bs.add("sai");
        bs.add("sajirhat");
        bs.add("sajneberia");
        bs.add("salap");
        bs.add("salap bazar");
        bs.add("salap school");
        bs.add("salkia");
        bs.add("saltlake");
        bs.add("saltlake aj block");
        bs.add("saltlake central park");
        bs.add("saltlake pnb");
        bs.add("saltlake stadium");
        bs.add("saltlake swimming pool");
        bs.add("samali");
        bs.add("sandhya bazar");
        bs.add("santoshpur(jadavpur)");
        bs.add("santoshpur(mahestala)");
        bs.add("santragachi");
        bs.add("sapoorji");
        bs.add("saraswati bridge");
        bs.add("sarat bose road");
        bs.add("sarisha");
        bs.add("sarkarpole");
        bs.add("sarsuna");
        bs.add("sasthir more");
        bs.add("satgachi");
        bs.add("satyanarayan park");
        bs.add("science city");
        bs.add("sdf");
        bs.add("sealdah");
        bs.add("sector v");
        bs.add("seven tanks");
        bs.add("shakuntala park");
        bs.add("shalimar station");
        bs.add("shamukpota");
        bs.add("shibpur");
        bs.add("shyambazar");
        bs.add("shyampukur");
        bs.add("shyampur");
        bs.add("siakhola");
        bs.add("sibanandabati");
        bs.add("sibani pith");
        bs.add("siemens");
        bs.add("sinthee more");
        bs.add("sirakole");
        bs.add("siristone");
        bs.add("siriti");
        bs.add("sishumangal hospital");
        bs.add("sitapur");
        bs.add("sld gate");
        bs.add("sn banerjee road");
        bs.add("sodpur");
        bs.add("sonamukhi bazar");
        bs.add("sonarpur");
        bs.add("southern avenue");
        bs.add("south city");
        bs.add("sovabazar");
        bs.add("sp mukherjee road");
        bs.add("sreenagar main road");
        bs.add("strand road");
        bs.add("subhashgram station");
        bs.add("subhas colony");
        bs.add("subhash sarobar");
        bs.add("sukanta setu");
        bs.add("sukanti nagar");
        bs.add("sukhchar girja");
        bs.add("sulekha");
        bs.add("sunpur more");
        bs.add("surendranath college");
        bs.add("tala park");
        bs.add("tala post office");
        bs.add("talpukur");
        bs.add("taltola");
        bs.add("tank no10");
        bs.add("taratala");
        bs.add("taratala hotel management");
        bs.add("tata medical centre");
        bs.add("tea board");
        bs.add("technopolis");
        bs.add("teghoria");
        bs.add("tetultala");
        bs.add("thakurpukur bazar");
        bs.add("tikiapara");
        bs.add("titagarh");
        bs.add("toll tax");
        bs.add("tollygunge");
        bs.add("tollygunge circular road");
        bs.add("tollygunge metro");
        bs.add("tollygunge phari");
        bs.add("tollygunge tram depot");
        bs.add("topsia");
        bs.add("udaynarayanpur");
        bs.add("uday shankar sarani");
        bs.add("ujal");
        bs.add("ultadanga");
        bs.add("uluberia");
        bs.add("uluberia check post");
        bs.add("uluberia railway crossing");
        bs.add("ulughata");
        bs.add("unitech");
        bs.add("university more");
        bs.add("vebia");
        bs.add("vidyasagar setu");
        bs.add("vip bazar");
        bs.add("vip road");
        bs.add("vivekananda road");
        bs.add("vivekananda road crossing");
        bs.add("wellington");
        bs.add("wipro more");
        bs.add("wireless more");
        bs.add("yaskine");

        for (int i = 0; i < bs.size(); i++) {
            bs.set(i, bs.get(i).toUpperCase());
        }

        return bs;
    }
}
