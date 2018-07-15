package com.thebrokegamers.kolkatanavigator;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrainFragment extends Fragment {

    public ArrayList<ArrayList<String>> trainList;

    private ArrayList<String> autoCompleteSource;
    private ArrayList<String> autoCompleteDestination;
    private ArrayList<String> autoCompleteSourceTemp;
    private ArrayList<String> autoCompleteDestinationTemp;
    private static int TOTAL_TRAIN_LINES = 32;
    private ArrayAdapter<String> adapterSource;
    private ArrayAdapter<String> adapterDestination;
    private AutoCompleteTextView actv;
    private AutoCompleteTextView actvd;
    private RelativeLayout searchButton;
    private RelativeLayout leftTrainButton;
    private RelativeLayout rightTrainButton;
    private TextView leftTrainButtonText;
    private TextView rightTrainButtonText;
    private TestAdapter dbAdapter;
    private UserDatabaseAdapter userDbAdapter;
    private ListView trainListView;
    private ArrayList<ArrayList<String>> trains;
    private ArrayAdapter<ArrayList<String>> trainAdapter;
    private ImageView sourceDestinationIcon;
    private ArrayList<Integer> trainTableIndex;
    private ArrayList<Boolean> trainTableReverse;
    private int pos;
    private boolean listClicked;
    private ImageView reversibleArrows;
    private static  int mYear, mMonth, mDay, mHour, mMinutes;
    private Calendar c = Calendar.getInstance();
    private Typeface font;
    private TextInputLayout topText;
    private TextInputLayout bottomText;
    private TextView searchText;


    public TrainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_train, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUi(view);
    }

    private void initUi(View view) {
        Bundle bundle = this.getArguments();
        trainList =  new ArrayList<ArrayList<String>>(AddWordsTrain());
        font = Typeface.createFromAsset(getActivity().getAssets(), "Exo2-Regular.ttf");
        topText = (TextInputLayout) view.findViewById(R.id.top_text);
        topText.setTypeface(font);
        bottomText = (TextInputLayout) view.findViewById(R.id.bottom_text);
        bottomText.setTypeface(font);
        searchText = (TextView) view.findViewById(R.id.searchTrainText);
        searchText.setTypeface(font);
        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinutes = c.get(Calendar.MINUTE);
        dbAdapter = new TestAdapter(getActivity());
        userDbAdapter = new UserDatabaseAdapter(getActivity());
        searchButton = (RelativeLayout) view.findViewById(R.id.searchTrain);
        leftTrainButton = (RelativeLayout) view.findViewById(R.id.trainButtonLeft);
        leftTrainButtonText = (TextView) view.findViewById(R.id.trainButtonLeftText);
        leftTrainButtonText.setTypeface(font);
        rightTrainButtonText = (TextView) view.findViewById(R.id.trainButtonRightText);
        rightTrainButtonText.setTypeface(font);
        rightTrainButton = (RelativeLayout) view.findViewById(R.id.trainButtonRight);
        autoCompleteSource = new ArrayList<String>(AddWordsAutoComplete(trainList));
        autoCompleteSourceTemp = new ArrayList<String>(autoCompleteSource);
        autoCompleteDestination = new ArrayList<String>(AddWordsAutoComplete(trainList));
        autoCompleteDestinationTemp = new ArrayList<String>(autoCompleteDestination);
        trainListView = (ListView) view.findViewById(R.id.trainList);
        trains = new ArrayList<ArrayList<String>>();
        trainAdapter = new TrainListAdapter(getActivity(), trains);
        trainListView.setAdapter(trainAdapter);
        adapterSource = new CustomArrayAdapter(this.getActivity(), R.layout.suggestions_listview, autoCompleteSourceTemp);
        adapterDestination = new CustomArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, autoCompleteDestinationTemp);
        sourceDestinationIcon = (ImageView) view.findViewById(R.id.sourceDestinationImage);
        actv = (AutoCompleteTextView) view.findViewById(R.id.sourceInput);
        actv.setTypeface(font);
        actvd = (AutoCompleteTextView) view.findViewById(R.id.destinationInput);
        actvd.setTypeface(font);
        actv.setThreshold(1);//will start working from first character
        actvd.setThreshold(1);
        actv.setAdapter(adapterSource);//setting the adapter data into the AutoCompleteTextView
        actvd.setAdapter(adapterDestination);
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

        actv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sourceDestinationIcon.setImageResource(R.drawable.source_destination_icon);
                autoCompleteSourceTemp = new ArrayList<String>(autoCompleteSource);
                String ch = actvd.getText().toString();
                if (!ch.equals("") && ch.length() > 0) {
                    if (autoCompleteSourceTemp.contains(ch)) {
                        autoCompleteSourceTemp.remove(ch);
                        Log.d("BULLSHIT", "REMS");
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
                autoCompleteDestinationTemp = new ArrayList<String>(autoCompleteDestination);
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

        leftTrainButton.setOnClickListener(new View.OnClickListener() {
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
                        leftTrainButtonText.setText(f);
                        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                        mHour = c.get(Calendar.HOUR_OF_DAY);
                        mMinutes = c.get(Calendar.MINUTE);
                    }
                }, mHour, mMinutes, false);
                tpd.show();
            }
        });

        rightTrainButton.setOnClickListener(new View.OnClickListener() {
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
                                String b = dayOfMonth + " " + MetroFragment.MonthToString(monthOfYear);
                                Date todayDate = new Date();
                                Log.d("BITCHDATE", todayDate.getYear() + "     " + year);
                                if (dayOfMonth == todayDate.getDate() && monthOfYear == todayDate.getMonth() && year == todayDate.getYear() + 1900) {
                                    b = "Today";
                                }
                                rightTrainButtonText.setText(b);
                            }

                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                ShowTrainResults();
            }
        });

        trainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                removeFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                pos = position;
                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("FloatingFragment");
                if (fragment == null) {
                    Class fragmentClass;
                    fragmentClass = TrainNumberFragment.class;
                    ArrayList<String> train = new ArrayList<String>(trains.get(pos));
                    Bundle bundle = new Bundle();
                    bundle.putString("trainNumber", train.get(2));
                    bundle.putString("trainLine", train.get(4));
                    bundle.putBoolean("trainTableReverse", Boolean.parseBoolean(train.get(6)));
                    bundle.putStringArrayList("trainList", new ArrayList<String>(trainList.get(Integer.parseInt(train.get(5)))));
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

        if (bundle != null) {
            actv.setText(bundle.getString("source"));
            actvd.setText(bundle.getString("destination"));
            ShowTrainResults();
        }
    }

    private void ShowTrainResults () {
        removeFocus();
        if (!actv.getText().toString().equals("") && !actvd.getText().toString().equals("")
                && actv.getText().toString().length() > 0 && actvd.getText().toString().length() > 0
                && !actv.getText().toString().equals(actvd.getText().toString())) {
            trains.clear();
            String stA = actv.getText().toString();
            String stB = actvd.getText().toString();
            dbAdapter.open();
            ArrayList<String> trainLines = new ArrayList<String>();
            trainLines.addAll(getTrainLines(stA, stB));
            ArrayList<ArrayList<String>> t = new ArrayList<ArrayList<String>>
                    (dbAdapter.getTrains(actv.getText().toString(), actvd.getText().toString(), trainLines, trainTableIndex, trainTableReverse, c.get(Calendar.DAY_OF_WEEK)));
            trains.addAll(t);
            if (trains.size() == 0) {
                Toast.makeText(getActivity(), "No Trains Found!", Toast.LENGTH_SHORT).show();
            }
            else {
                Calendar d = Calendar.getInstance();
                String date = d.get(Calendar.YEAR) + " " + d.get(Calendar.MONTH) + " " + d.get(Calendar.DAY_OF_MONTH);
                Log.d("CURRENT_DATE", date);
                userDbAdapter.insertRecents("TRAIN", actv.getText().toString(), actvd.getText().toString(), date);
            }
            trainAdapter.notifyDataSetChanged();
            ScrollToPosition();
        }
        else {
            Toast.makeText(getActivity(), "Source or Destination cannot be empty!", Toast.LENGTH_SHORT).show();
        }
    }

    private void ScrollToPosition() {
        if (trains.size() > 0) {
            int i;
            for (i = 0; i < trains.size(); i++) {
                if (!TimeCompare(currentTime(), trains.get(i).get(0))) {
                    break;
                }
            }
            trainListView.setSelection(i);
        }
    }

    public static String currentTime () {
        String h;
        String m;
        if (mHour < 10) {
            h = "0" + mHour;
        }
        else {
            h = mHour + "";
        }
        if (mMinutes < 10) {
            m = "0" + mMinutes;
        }
        else {
            m = mMinutes + "";
        }
        return h + ":" + m;
    }

    private boolean TimeCompare(String currentTime, String comparisonTime) {
        int h1;
        int h2;
        int m1;
        int m2;
        h1 = Integer.parseInt(currentTime.substring(0, 2));
        m1 = Integer.parseInt(currentTime.substring(3));
        h2 = Integer.parseInt(comparisonTime.substring(0, 2));
        m2 = Integer.parseInt(comparisonTime.substring(3));
        if (h1 > h2) {
            return true;
        } else if (h1 == h2) {
            if (m1 > m2) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private ArrayList<String> getTrainLines(String stA, String stB) {
        ArrayList<String> temp = new ArrayList<String>();
        trainTableIndex = new ArrayList<Integer>();
        trainTableReverse = new ArrayList<Boolean>();
        for (int i = 0; i < TOTAL_TRAIN_LINES; i++) {
            if (trainList.get(i).contains(stA.toLowerCase()) && trainList.get(i).contains(stB.toLowerCase())) {
                if (trainList.get(i).indexOf(stB.toLowerCase()) > trainList.get(i).indexOf(stA.toLowerCase())) {
                    String s = ChooseTrainLine(i, true);
                    if (!s.equals("") && s.length() > 0) {
                        temp.add(s);
                        trainTableIndex.add(i);
                        trainTableReverse.add(true);
                    }
                } else {
                    String s = ChooseTrainLine(i, false);
                    if (!s.equals("") && s.length() > 0) {
                        temp.add(s);
                        trainTableIndex.add(i);
                        trainTableReverse.add(false);
                    }
                }
            }
        }
        return temp;
    }

    private String ChooseTrainLine(int a, boolean u) {

        if (u) {
            if (a == 0) {
                return ("howrah_midnapore");
            } else if (a == 1) {
                return ("howrah_amta");
            } else if (a == 2) {
                return ("howrah_barddhaman_main");
            } else if (a == 3) {
                return ("howrah_barddhaman_chord");
            } else if (a == 4) {
                return ("howrah_arambag");
            } else if (a == 5) {
                return ("howrah_haldia");
            } else if (a == 6) {
                return ("hwh_bdc_kwae");
            } else if (a == 7) {
                return ("bangaon_canning");
            } else if (a == 8) {
                return ("kalyani_budgebudge");
            } else if (a == 9) {
                return ("sealdah_diamond_harbour");
            } else if (a == 10) {
                return ("sealdah_namkhana");
            } else if (a == 11) {
                return ("mjt_lkpr");
            } else if (a == 12) {
                return ("bbdb_knj");
            } else if (a == 13) {
                return ("cht_mjt");
            } else if (a == 14) {
                return ("circ_majerhat1");
            } else if (a == 15) {
                return ("circ_majerhat2");
            } else if (a == 16) {
                return ("klym_mjt");
            } else if (a == 17) {
                return ("mjt_gof");
            } else if (a == 18) {
                return ("mjt_hnb");
            } else if (a == 19) {
                return ("sealdah_shantipur");
            } else if (a == 20) {
                return ("sealdah_knj");
            } else if (a == 21) {
                return ("sealdah_kalyani_simanta");
            } else if (a == 22) {
                return ("sealdah_gede");
            } else if (a == 23) {
                return ("sealdah_baruipara");
            } else if (a == 24) {
                return ("sealdah_hasnabad");
            } else if (a == 25) {
                return ("sealdah_katwa");
            } else if (a == 26) {
                return ("sealdah_bangaon");
            } else if (a == 27) {
                return ("sealdah_barddhaman");
            } else if (a == 28) {
                return ("shm_mca");
            } else if (a == 29) {
                return ("gede_mjt");
            } else if (a == 30) {
                return ("mjt_rha");
            } else if (a == 31) {
                return ("sealdah_kolkata");
            }
        } else {
            if (a == 0) {
                return ("howrah_midnapore_down");
            } else if (a == 1) {
                return ("howrah_amta_down");
            } else if (a == 2) {
                return ("howrah_barddhaman_main_down");
            } else if (a == 3) {
                return ("howrah_barddhaman_chord_down");
            } else if (a == 4) {
                return ("howrah_arambag_down");
            } else if (a == 5) {
                return ("howrah_haldia_down");
            } else if (a == 6) {
                return ("hwh_bdc_kwae_down");
            } else if (a == 7) {
                return ("canning_bangaon");
            } else if (a == 8) {
                return ("budgebudge_kalyani");
            } else if (a == 9) {
                return ("diamond_harbour_sealdah");
            } else if (a == 10) {
                return ("namkhana_sealdah");
            } else if (a == 11) {
                return ("mjt_lkpr_down");
            } else if (a == 14) {
                return ("circ_majerhat1_down");
            } else if (a == 15) {
                return ("circ_majerhat2_down");
            } else if (a == 19) {
                return ("shantipur_sealdah");
            } else if (a == 20) {
                return ("knj_sealdah");
            } else if (a == 21) {
                return ("kalyani_simanta_sealdah");
            } else if (a == 22) {
                return ("gede_sealdah");
            } else if (a == 23) {
                return ("baruipara_sealdah");
            } else if (a == 24) {
                return ("hasnabad_sealdah");
            } else if (a == 25) {
                return ("katwa_sealdah");
            } else if (a == 26) {
                return ("bangaon_sealdah");
            } else if (a == 27) {
                return ("barddhaman_sealdah");
            } else if (a == 28) {
                return ("shm_mca_down");
            }
        }
        return "";
        //Debug.Log("JON TARGERYAN WILL RISE AGAIN!   " + PlayerPrefs.GetString("TrainLine_" + f.ToString()));
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


    public static ArrayList<String> AddWordsAutoComplete(ArrayList<ArrayList<String>> t) {
        Set<String> set = new HashSet<String>();
        for (int i = 0; i < TOTAL_TRAIN_LINES; i++) {
            set.addAll(t.get(i));
        }
        ArrayList<String> ac = new ArrayList<String>(set);
        Collections.sort(ac);
        for (int i = 0; i < ac.size(); i++) {
            ac.set(i, ac.get(i).toUpperCase());
        }
        return ac;
    }

    public static ArrayList<ArrayList<String>> AddWordsTrain() {
        ArrayList<ArrayList<String>> tl = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < TOTAL_TRAIN_LINES; i++) {
            tl.add(new ArrayList<String>());
        }

        //HWH-MDN
        tl.get(0).add("howrah jn-hwh");
        tl.get(0).add("tikiapara-tpkr");
        tl.get(0).add("dashnagar-dsnr");
        tl.get(0).add("ramrajatala-rmj");
        tl.get(0).add("santragachi jn-src");
        tl.get(0).add("maurigram-mrgm");
        tl.get(0).add("andul-adl");
        tl.get(0).add("sankrall-sel");
        tl.get(0).add("abada-abb");
        tl.get(0).add("nalpur-nalr");
        tl.get(0).add("bauria jn-bva");
        tl.get(0).add("chengel-cga");
        tl.get(0).add("phuleswar-flr");
        tl.get(0).add("ulubaria-ulb");
        tl.get(0).add("bir shibpur-bsbp");
        tl.get(0).add("kulgachia-kgy");
        tl.get(0).add("bagnan-bzn");
        tl.get(0).add("ghoraghata-ggta");
        tl.get(0).add("deulti-dte");
        tl.get(0).add("kolaghat goods-kig");
        tl.get(0).add("mecheda-mca");
        tl.get(0).add("nandahganja-ndgj");
        tl.get(0).add("bhogpur-bop");
        tl.get(0).add("narayan pakuria mura-npmr");
        tl.get(0).add("panskura-pku");
        tl.get(0).add("khirai-khai");
        tl.get(0).add("haur-haur");
        tl.get(0).add("radhamohanpur-rdu");
        tl.get(0).add("duan-duan");
        tl.get(0).add("balichak-bck");
        tl.get(0).add("shyam chak-smck");
        tl.get(0).add("madpur-mpd");
        tl.get(0).add("jakpur-jpr");
        tl.get(0).add("kharagpur jn-kgp");
        tl.get(0).add("girimaidan-gmdn");
        tl.get(0).add("gokulpur-gkl");
        tl.get(0).add("midnapore-mdn");

        //HWH-AMZ
        tl.get(1).add("howrah jn-hwh");
        tl.get(1).add("tikiapara-tpkr");
        tl.get(1).add("dashnagar-dsnr");
        tl.get(1).add("ramrajatala-rmj");
        tl.get(1).add("santragachi jn-src");
        tl.get(1).add("bankra nayabaj-bknm");
        tl.get(1).add("baltikuri jn-balt");
        tl.get(1).add("kona-kona");
        tl.get(1).add("dansi-dni");
        tl.get(1).add("jhallurbar-jlbr");
        tl.get(1).add("makardaha-mdc");
        tl.get(1).add("domjur road-dmjr");
        tl.get(1).add("domjur-djr");
        tl.get(1).add("dakshin bari-dkb");
        tl.get(1).add("bargachia-bac");
        tl.get(1).add("pathiapal ph-pthl");
        tl.get(1).add("munshirhat-mnh");
        tl.get(1).add("mahendralal ngr ph-mhln");
        tl.get(1).add("maju-mjh");
        tl.get(1).add("jalalsi ph-jli");
        tl.get(1).add("harishdadpur ph-hdc");
        tl.get(1).add("amta-amz");

        //HWH-BWN-MAIN
        tl.get(2).add("howrah jn-hwh");
        tl.get(2).add("liluah-llh");
        tl.get(2).add("belur-beq");
        tl.get(2).add("bally-bly");
        tl.get(2).add("uttarpara-upa");
        tl.get(2).add("hind motor-hmz");
        tl.get(2).add("konnagar-kog");
        tl.get(2).add("rishra-ris");
        tl.get(2).add("serampore-srp");
        tl.get(2).add("seoraphuli-she");
        tl.get(2).add("baidyabati-bbae");
        tl.get(2).add("bhadreshwar-bhr");
        tl.get(2).add("mankundu-muu");
        tl.get(2).add("chandan nagar-cgr");
        tl.get(2).add("chuchura-cns");
        tl.get(2).add("hooghly-hgy");
        tl.get(2).add("bandel jn-bdc");
        tl.get(2).add("adi saptagram-adst");
        tl.get(2).add("magra-mug");
        tl.get(2).add("talandu-tlo");
        tl.get(2).add("khanyan-khn");
        tl.get(2).add("pundooah-pda");
        tl.get(2).add("simlagarh-slg");
        tl.get(2).add("bainchigram-bcgm");
        tl.get(2).add("bainchi-boi");
        tl.get(2).add("debipur-dbp");
        tl.get(2).add("bagila-bgf");
        tl.get(2).add("memari-mym");
        tl.get(2).add("nimo-nmf");
        tl.get(2).add("rasulpur-rslr");
        tl.get(2).add("palsit-plae");
        tl.get(2).add("saktigarh-skg");
        tl.get(2).add("gangpur-grp");
        tl.get(2).add("barddhaman-bwn");

        //HWH-BWN-CHORD
        tl.get(3).add("howrah jn-hwh");
        tl.get(3).add("liluah-llh");
        tl.get(3).add("belur-beq");
        tl.get(3).add("belanagar-bzl");
        tl.get(3).add("dankuni-dkae");
        tl.get(3).add("gobra-gbra");
        tl.get(3).add("janai road-jox");
        tl.get(3).add("begumpur-bpae");
        tl.get(3).add("baruipara-brpa");
        tl.get(3).add("mirzapur bnkipr-mbe");
        tl.get(3).add("balarambati-blae");
        tl.get(3).add("kamarkundu-kqu");
        tl.get(3).add("madhu sudanpur-mdse");
        tl.get(3).add("chandanpur-cdae");
        tl.get(3).add("porabazar-pbz");
        tl.get(3).add("belmuri-bmae");
        tl.get(3).add("dhaniakhali halt-dnhl");
        tl.get(3).add("sibaichandi-shbc");
        tl.get(3).add("cheragram bh-crae");
        tl.get(3).add("hajigarh-hih");
        tl.get(3).add("gurap-grae");
        tl.get(3).add("jhapandanga-jpq");
        tl.get(3).add("jaugram-jrae");
        tl.get(3).add("nabagram-nbae");
        tl.get(3).add("masagram-msae");
        tl.get(3).add("chanchai-chc");
        tl.get(3).add("palla road-prae");
        tl.get(3).add("saktigarh-skg");
        tl.get(3).add("gangpur-grp");
        tl.get(3).add("barddhaman-bwn");

        //HWH-AMBG
        tl.get(4).add("howrah jn-hwh");
        tl.get(4).add("liluah-llh");
        tl.get(4).add("belur-beq");
        tl.get(4).add("bally-bly");
        tl.get(4).add("uttarpara-upa");
        tl.get(4).add("hind motor-hmz");
        tl.get(4).add("konnagar-kog");
        tl.get(4).add("rishra-ris");
        tl.get(4).add("serampore-srp");
        tl.get(4).add("seoraphuli-she");
        tl.get(4).add("diara-dea");
        tl.get(4).add("nasibpur-nsf");
        tl.get(4).add("singur-siu");
        tl.get(4).add("kamarkundu lower-kqls");
        tl.get(4).add("nalikul-nkl");
        tl.get(4).add("maliya-mlya");
        tl.get(4).add("haripal-hpl");
        tl.get(4).add("kaikala-kkae");
        tl.get(4).add("bahir khanda-bahw");
        tl.get(4).add("loknath-lok");
        tl.get(4).add("tarakeswar-tak");
        tl.get(4).add("mayapur-mayp");
        tl.get(4).add("arambag-ambg");

        //HWH-HLZ
        tl.get(5).add("howrah jn-hwh");
        tl.get(5).add("tikiapara-tpkr");
        tl.get(5).add("dashnagar-dsnr");
        tl.get(5).add("ramrajatala-rmj");
        tl.get(5).add("santragachi jn-src");
        tl.get(5).add("maurigram-mrgm");
        tl.get(5).add("andul-adl");
        tl.get(5).add("sankrall-sel");
        tl.get(5).add("abada-abb");
        tl.get(5).add("nalpur-nalr");
        tl.get(5).add("bauria jn-bva");
        tl.get(5).add("chengel-cga");
        tl.get(5).add("phuleswar-flr");
        tl.get(5).add("ulubaria-ulb");
        tl.get(5).add("bir shibpur-bsbp");
        tl.get(5).add("kulgachia-kgy");
        tl.get(5).add("bagnan-bzn");
        tl.get(5).add("ghoraghata-ggta");
        tl.get(5).add("deulti-dte");
        tl.get(5).add("kolaghat goods-kig");
        tl.get(5).add("mecheda-mca");
        tl.get(5).add("nandahganja-ndgj");
        tl.get(5).add("bhogpur-bop");
        tl.get(5).add("narayan pakuria mura-npmr");
        tl.get(5).add("panskura-pku");
        tl.get(5).add("raghunathbari-rgx");
        tl.get(5).add("rajgoda-rga");
        tl.get(5).add("saheed matangini-smtg");
        tl.get(5).add("tamluk-tmz");
        tl.get(5).add("keshabpur-ksbp");
        tl.get(5).add("satish samanta ph-ssph");
        tl.get(5).add("mahisadal-msdl");
        tl.get(5).add("barda-brdb");
        tl.get(5).add("basulyasutahata-bysa");
        tl.get(5).add("durgachak-dzk");
        tl.get(5).add("durga chak town-dzkt");
        tl.get(5).add("bandar ph-baar");
        tl.get(5).add("haldia-hlz");

        //HWH-KWAE
        tl.get(6).add("howrah jn-hwh");
        tl.get(6).add("liluah-llh");
        tl.get(6).add("belur-beq");
        tl.get(6).add("bally-bly");
        tl.get(6).add("uttarpara-upa");
        tl.get(6).add("hind motor-hmz");
        tl.get(6).add("konnagar-kog");
        tl.get(6).add("rishra-ris");
        tl.get(6).add("serampore-srp");
        tl.get(6).add("seoraphuli-she");
        tl.get(6).add("baidyabati-bbae");
        tl.get(6).add("bhadreshwar-bhr");
        tl.get(6).add("mankundu-muu");
        tl.get(6).add("chandan nagar-cgr");
        tl.get(6).add("chuchura-cns");
        tl.get(6).add("hooghly-hgy");
        tl.get(6).add("bandel jn-bdc");
        tl.get(6).add("bansh baria-bsae");
        tl.get(6).add("tribeni-tbae");
        tl.get(6).add("kuntighat-kju");
        tl.get(6).add("dumurdaha-dmle");
        tl.get(6).add("khamargachhi-kmae");
        tl.get(6).add("jirat-jit");
        tl.get(6).add("balagarh-bgae");
        tl.get(6).add("somra bazar-soae");
        tl.get(6).add("behula-bhla");
        tl.get(6).add("guptipara-gpae");
        tl.get(6).add("ambika kalna-abka");
        tl.get(6).add("baghnapara-bgra");
        tl.get(6).add("dhatrigram-dtae");
        tl.get(6).add("samudra garh-smae");
        tl.get(6).add("kalinagar-klnt");
        tl.get(6).add("nabadwip dham-ndae");
        tl.get(6).add("bhandartikuri-bfz");
        tl.get(6).add("purbasthali-psae");
        tl.get(6).add("mertala phaleya halt-mtfa");
        tl.get(6).add("lakshmipur-lkx");
        tl.get(6).add("belerhat-bqy");
        tl.get(6).add("patuli-ptae");
        tl.get(6).add("agradwip-agae");
        tl.get(6).add("sahebtala halt-shba");
        tl.get(6).add("dainhat-dhae");
        tl.get(6).add("katwa jn-kwae");

        //BNJ-CG
        tl.get(7).add("bangaon jn-bnj");
        tl.get(7).add("bibhutibhushan-bnaa");
        tl.get(7).add("chandpara-cdp");
        tl.get(7).add("thakurnagar-tknr");
        tl.get(7).add("gobardanga-gbg");
        tl.get(7).add("machchalandapur-msl");
        tl.get(7).add("sanhati-snht");
        tl.get(7).add("habra-hb");
        tl.get(7).add("ashok nagar rd-askr");
        tl.get(7).add("guma-guma");
        tl.get(7).add("bira-bira");
        tl.get(7).add("dattapukur-dtk");
        tl.get(7).add("bamangachhi-bmg");
        tl.get(7).add("barasat-bt");
        tl.get(7).add("hridaypur-hhr");
        tl.get(7).add("madhyamgram-mmg");
        tl.get(7).add("new barrackpore-nbe");
        tl.get(7).add("bisharpara kodalia-brpk");
        tl.get(7).add("birati-bbt");
        tl.get(7).add("durganagar-dgnr");
        tl.get(7).add("dum dum cant-ddc");
        tl.get(7).add("dum dum-ddj");
        tl.get(7).add("bidhan nagar-bnxr");
        tl.get(7).add("sir gurudas banerjee-sgba");
        tl.get(7).add("sealdah-sdah");
        tl.get(7).add("park circus-pqs");
        tl.get(7).add("ballygunge jn-bln");
        tl.get(7).add("dhakuria-dkh");
        tl.get(7).add("jadabpur-jdp");
        tl.get(7).add("bagha jatin-bgjt");
        tl.get(7).add("new garia-ngri");
        tl.get(7).add("garia-gia");
        tl.get(7).add("narendrapur halt-nrpr");
        tl.get(7).add("sonarpur jn-spr");
        tl.get(7).add("bidyadharpur-bdyp");
        tl.get(7).add("kalikapur-klkr");
        tl.get(7).add("champahati-cht");
        tl.get(7).add("piali-plf");
        tl.get(7).add("gourdaha halt-gqd");
        tl.get(7).add("ghutiari sharif-gof");
        tl.get(7).add("betheria ghola-btpg");
        tl.get(7).add("taldi-tlx");
        tl.get(7).add("canning-cg");

        //KLYM-BGB
        tl.get(8).add("kalyani simanta-klym");
        tl.get(8).add("kalyani ghoshpara-klyg");
        tl.get(8).add("kalyani silpanchal-klys");
        tl.get(8).add("kalyani-kyi");
        tl.get(8).add("kanchrapara-kpa");
        tl.get(8).add("halisahar-hlr");
        tl.get(8).add("naihati jn-nh");
        tl.get(8).add("kankinara-knr");
        tl.get(8).add("jagadal-jgdl");
        tl.get(8).add("shyamnagar-snr");
        tl.get(8).add("ichhapur-ip");
        tl.get(8).add("palta-ptf");
        tl.get(8).add("barrackpore-bp");
        tl.get(8).add("titagarh-tgh");
        tl.get(8).add("khardaha-kdh");
        tl.get(8).add("sodpur-sep");
        tl.get(8).add("agarpara-agp");
        tl.get(8).add("belgharia-blh");
        tl.get(8).add("dum dum-ddj");
        tl.get(8).add("bidhan nagar-bnxr");
        tl.get(8).add("sir gurudas banerjee-sgba");
        tl.get(8).add("sealdah-sdah");
        tl.get(8).add("park circus-pqs");
        tl.get(8).add("ballygunge jn-bln");
        tl.get(8).add("lake gardens-lkf");
        tl.get(8).add("new alipur-nacc");
        tl.get(8).add("majerhat-mjt");
        tl.get(8).add("brace bridge-brj");
        tl.get(8).add("santoshpur-ssp");
        tl.get(8).add("akra-akra");
        tl.get(8).add("nangi-nai");
        tl.get(8).add("budge budge-bgb");

        //SDAH-DH
        tl.get(9).add("sealdah-sdah");
        tl.get(9).add("park circus-pqs");
        tl.get(9).add("ballygunge jn-bln");
        tl.get(9).add("dhakuria-dkh");
        tl.get(9).add("jadabpur-jdp");
        tl.get(9).add("bagha jatin-bgjt");
        tl.get(9).add("new garia-ngri");
        tl.get(9).add("garia-gia");
        tl.get(9).add("narendrapur halt-nrpr");
        tl.get(9).add("sonarpur jn-spr");
        tl.get(9).add("subhas gram-sbgr");
        tl.get(9).add("mallikpur-mak");
        tl.get(9).add("baruipur jn-brp");
        tl.get(9).add("kalyanpur-kyp");
        tl.get(9).add("dakshin durgapr-dkdp");
        tl.get(9).add("hotar-ht");
        tl.get(9).add("dhamua-dmu");
        tl.get(9).add("uttar radhanagar hal-utn");
        tl.get(9).add("magra hat-mgt");
        tl.get(9).add("bahrupiya halt-bhpa");
        tl.get(9).add("sangrampur-snu");
        tl.get(9).add("deula-d");
        tl.get(9).add("netra-nta");
        tl.get(9).add("basuldanga-bsd");
        tl.get(9).add("gurudas nagar-gurn");
        tl.get(9).add("diamond harbour-dh");

        //SDAH-NMKA
        tl.get(10).add("sealdah-sdah");
        tl.get(10).add("park circus-pqs");
        tl.get(10).add("ballygunge jn-bln");
        tl.get(10).add("dhakuria-dkh");
        tl.get(10).add("jadabpur-jdp");
        tl.get(10).add("bagha jatin-bgjt");
        tl.get(10).add("new garia-ngri");
        tl.get(10).add("garia-gia");
        tl.get(10).add("narendrapur halt-nrpr");
        tl.get(10).add("sonarpur jn-spr");
        tl.get(10).add("subhas gram-sbgr");
        tl.get(10).add("mallikpur-mak");
        tl.get(10).add("baruipur jn-brp");
        tl.get(10).add("shasan road-ssrd");
        tl.get(10).add("krishnamohan halt-krxm");
        tl.get(10).add("dhapdhapi-dpdp");
        tl.get(10).add("surjyapur-sjpr");
        tl.get(10).add("gocharan-gcn");
        tl.get(10).add("hogla-hga");
        tl.get(10).add("dakshin barasat-dbt");
        tl.get(10).add("baharu-baru");
        tl.get(10).add("jayngr majlipur-jnm");
        tl.get(10).add("mathurapur rd-mprd");
        tl.get(10).add("madhabpur-mdbp");
        tl.get(10).add("lakshmikantpur-lkpr");
        tl.get(10).add("udairampur-urp");
        tl.get(10).add("kalwan halt-klw");
        tl.get(10).add("karanjali halt-knji");
        tl.get(10).add("nischindapur-ncp");
        tl.get(10).add("kashinagar halt-khgr");
        tl.get(10).add("kakdwip-kwdp");
        tl.get(10).add("namkhana-nmka");

        //MJT-LKPR
        tl.get(11).add("majerhat-mjt");
        tl.get(11).add("new alipur-nacc");
        tl.get(11).add("lake gardens-lkf");
        tl.get(11).add("ballygunge jn-bln");
        tl.get(11).add("dhakuria-dkh");
        tl.get(11).add("jadabpur-jdp");
        tl.get(11).add("bagha jatin-bgjt");
        tl.get(11).add("new garia-ngri");
        tl.get(11).add("garia-gia");
        tl.get(11).add("narendrapur halt-nrpr");
        tl.get(11).add("sonarpur jn-spr");
        tl.get(11).add("subhas gram-sbgr");
        tl.get(11).add("mallikpur-mak");
        tl.get(11).add("baruipur jn-brp");
        tl.get(11).add("shasan road-ssrd");
        tl.get(11).add("krishnamohan halt-krxm");
        tl.get(11).add("dhapdhapi-dpdp");
        tl.get(11).add("surjyapur-sjpr");
        tl.get(11).add("gocharan-gcn");
        tl.get(11).add("hogla-hga");
        tl.get(11).add("dakshin barasat-dbt");
        tl.get(11).add("baharu-baru");
        tl.get(11).add("jayngr majlipur-jnm");
        tl.get(11).add("mathurapur rd-mprd");
        tl.get(11).add("madhabpur-mdbp");
        tl.get(11).add("lakshmikantpur-lkpr");

        //BBDB-KNJ
        tl.get(12).add("benoybadaldineshbag-bbdb");
        tl.get(12).add("barra bazar-bzb");
        tl.get(12).add("sovabazar ahiritola-sola");
        tl.get(12).add("bag bazar-bbr");
        tl.get(12).add("talla-tala");
        tl.get(12).add("kolkata-koaa");
        tl.get(12).add("patipukur-ptkr");
        tl.get(12).add("dum dum-ddj");
        tl.get(12).add("belgharia-blh");
        tl.get(12).add("agarpara-agp");
        tl.get(12).add("sodpur-sep");
        tl.get(12).add("khardaha-kdh");
        tl.get(12).add("titagarh-tgh");
        tl.get(12).add("barrackpore-bp");
        tl.get(12).add("palta-ptf");
        tl.get(12).add("ichhapur-ip");
        tl.get(12).add("shyamnagar-snr");
        tl.get(12).add("jagadal-jgdl");
        tl.get(12).add("kankinara-knr");
        tl.get(12).add("naihati jn-nh");
        tl.get(12).add("halisahar-hlr");
        tl.get(12).add("kanchrapara-kpa");
        tl.get(12).add("kalyani-kyi");
        tl.get(12).add("madanpur-mpj");
        tl.get(12).add("simurali-smx");
        tl.get(12).add("palpara-pxr");
        tl.get(12).add("chakdaha-cdh");
        tl.get(12).add("payradanga-pdx");
        tl.get(12).add("ranaghat jn-rha");
        tl.get(12).add("kalinarynpur jn-klnp");
        tl.get(12).add("birnagar-bij");
        tl.get(12).add("taherpur-thp");
        tl.get(12).add("badkulla-bdz");
        tl.get(12).add("jalalkhali halt-jkl");
        tl.get(12).add("krishngr cty jn-knj");

        //CHT-MJT
        tl.get(13).add("champahati-cht");
        tl.get(13).add("kalikapur-klkr");
        tl.get(13).add("bidyadharpur-bdyp");
        tl.get(13).add("sonarpur jn-spr");
        tl.get(13).add("narendrapur halt-nrpr");
        tl.get(13).add("garia-gia");
        tl.get(13).add("bagha jatin-bgjt");
        tl.get(13).add("jadabpur-jdp");
        tl.get(13).add("dhakuria-dkh");
        tl.get(13).add("ballygunge jn-bln");
        tl.get(13).add("lake gardens-lkf");
        tl.get(13).add("new alipur-nacc");
        tl.get(13).add("majerhat-mjt");

        //CIRCULAR 1
        tl.get(14).add("majerhat-mjt");
        tl.get(14).add("re mount road-rmtr");
        tl.get(14).add("khidirpur-kirp");
        tl.get(14).add("princep ghat-ppgt");
        tl.get(14).add("eden gardens-edg");
        tl.get(14).add("benoybadaldineshbag-bbdb");
        tl.get(14).add("barra bazar-bzb");
        tl.get(14).add("sovabazar ahiritola-sola");
        tl.get(14).add("bag bazar-bbr");
        tl.get(14).add("talla-tala");
        tl.get(14).add("kolkata-koaa");
        tl.get(14).add("patipukur-ptkr");
        tl.get(14).add("dum dum-ddj");
        tl.get(14).add("dum dum cant-ddc");
        tl.get(14).add("biman bunder-bnba");

        //CIRCULAR 2
        tl.get(15).add("majerhat-mjt");
        tl.get(15).add("new alipur-nacc");
        tl.get(15).add("lake gardens-lkf");
        tl.get(15).add("ballygunge jn-bln");
        tl.get(15).add("park circus-pqs");
        tl.get(15).add("sir gurudas banerjee-sgba");
        tl.get(15).add("bidhan nagar-bnxr");
        tl.get(15).add("dum dum-ddj");
        tl.get(15).add("dum dum cant-ddc");
        tl.get(15).add("biman bunder-bnba");

        //KLYM-MJT
        tl.get(16).add("kalyani simanta-klym");
        tl.get(16).add("kalyani ghoshpara-klyg");
        tl.get(16).add("kalyani silpanchal-klys");
        tl.get(16).add("kalyani-kyi");
        tl.get(16).add("kanchrapara-kpa");
        tl.get(16).add("halisahar-hlr");
        tl.get(16).add("naihati jn-nh");
        tl.get(16).add("kankinara-knr");
        tl.get(16).add("jagadal-jgdl");
        tl.get(16).add("shyamnagar-snr");
        tl.get(16).add("ichhapur-ip");
        tl.get(16).add("palta-ptf");
        tl.get(16).add("barrackpore-bp");
        tl.get(16).add("titagarh-tgh");
        tl.get(16).add("khardaha-kdh");
        tl.get(16).add("sodpur-sep");
        tl.get(16).add("agarpara-agp");
        tl.get(16).add("belgharia-blh");
        tl.get(16).add("dum dum cant-ddc");
        tl.get(16).add("dum dum-ddj");
        tl.get(16).add("patipukur-ptkr");
        tl.get(16).add("kolkata-koaa");
        tl.get(16).add("talla-tala");
        tl.get(16).add("bag bazar-bbr");
        tl.get(16).add("sovabazar ahiritola-sola");
        tl.get(16).add("barra bazar-bzb");
        tl.get(16).add("benoybadaldineshbag-bbdb");
        tl.get(16).add("eden gardens-edg");
        tl.get(16).add("princep ghat-ppgt");
        tl.get(16).add("khidirpur-kirp");
        tl.get(16).add("re mount road-rmtr");
        tl.get(16).add("majerhat-mjt");

        //BNJ-GOF
        tl.get(17).add("bangaon jn-bnj");
        tl.get(17).add("bibhutibhushan-bnaa");
        tl.get(17).add("chandpara-cdp");
        tl.get(17).add("thakurnagar-tknr");
        tl.get(17).add("gobardanga-gbg");
        tl.get(17).add("machchalandapur-msl");
        tl.get(17).add("sanhati-snht");
        tl.get(17).add("habra-hb");
        tl.get(17).add("ashok nagar rd-askr");
        tl.get(17).add("guma-guma");
        tl.get(17).add("bira-bira");
        tl.get(17).add("dattapukur-dtk");
        tl.get(17).add("bamangachhi-bmg");
        tl.get(17).add("barasat-bt");
        tl.get(17).add("hridaypur-hhr");
        tl.get(17).add("madhyamgram-mmg");
        tl.get(17).add("new barrackpore-nbe");
        tl.get(17).add("bisharpara kodalia-brpk");
        tl.get(17).add("birati-bbt");
        tl.get(17).add("durganagar-dgnr");
        tl.get(17).add("dum dum cant-ddc");
        tl.get(17).add("dum dum-ddj");
        tl.get(17).add("bidhan nagar-bnxr");
        tl.get(17).add("sir gurudas banerjee-sgba");
        tl.get(17).add("sealdah-sdah");
        tl.get(17).add("patipukur-ptkr");
        tl.get(17).add("kolkata-koaa");
        tl.get(17).add("talla-tala");
        tl.get(17).add("bag bazar-bbr");
        tl.get(17).add("sovabazar ahiritola-sola");
        tl.get(17).add("barra bazar-bzb");
        tl.get(17).add("benoybadaldineshbag-bbdb");
        tl.get(17).add("eden gardens-edg");
        tl.get(17).add("princep ghat-ppgt");
        tl.get(17).add("khidirpur-kirp");
        tl.get(17).add("re mount road-rmtr");
        tl.get(17).add("majerhat-mjt");
        tl.get(17).add("new alipur-nacc");
        tl.get(17).add("lake gardens-lkf");
        tl.get(17).add("ballygunge jn-bln");
        tl.get(17).add("dhakuria-dkh");
        tl.get(17).add("jadabpur-jdp");
        tl.get(17).add("bagha jatin-bgjt");
        tl.get(17).add("garia-gia");
        tl.get(17).add("narendrapur halt-nrpr");
        tl.get(17).add("sonarpur jn-spr");
        tl.get(17).add("subhas gram-sbgr");
        tl.get(17).add("mallikpur-mak");
        tl.get(17).add("baruipur jn-brp");
        tl.get(17).add("bidyadharpur-bdyp");
        tl.get(17).add("kalikapur-klkr");
        tl.get(17).add("champahati-cht");
        tl.get(17).add("piali-plf");
        tl.get(17).add("gourdaha halt-gqd");
        tl.get(17).add("ghutiari sharif-gof");

        //MJT-HNB
        tl.get(18).add("majerhat-mjt");
        tl.get(18).add("new alipur-nacc");
        tl.get(18).add("lake gardens-lkf");
        tl.get(18).add("ballygunge jn-bln");
        tl.get(18).add("park circus-pqs");
        tl.get(18).add("re mount road-rmtr");
        tl.get(18).add("khidirpur-kirp");
        tl.get(18).add("princep ghat-ppgt");
        tl.get(18).add("eden gardens-edg");
        tl.get(18).add("benoybadaldineshbag-bbdb");
        tl.get(18).add("barra bazar-bzb");
        tl.get(18).add("sovabazar ahiritola-sola");
        tl.get(18).add("bag bazar-bbr");
        tl.get(18).add("talla-tala");
        tl.get(18).add("kolkata-koaa");
        tl.get(18).add("patipukur-ptkr");
        tl.get(18).add("sir gurudas banerjee-sgba");
        tl.get(18).add("bidhan nagar-bnxr");
        tl.get(18).add("dum dum-ddj");
        tl.get(18).add("dum dum cant-ddc");
        tl.get(18).add("durganagar-dgnr");
        tl.get(18).add("birati-bbt");
        tl.get(18).add("bisharpara kodalia-brpk");
        tl.get(18).add("new barrackpore-nbe");
        tl.get(18).add("madhyamgram-mmg");
        tl.get(18).add("hridaypur-hhr");
        tl.get(18).add("barasat-bt");
        tl.get(18).add("bamangachhi-bmg");
        tl.get(18).add("dattapukur-dtk");
        tl.get(18).add("bira-bira");
        tl.get(18).add("guma-guma");
        tl.get(18).add("ashok nagar rd-askr");
        tl.get(18).add("habra-hb");
        tl.get(18).add("kazipara-kzpr");
        tl.get(18).add("karea kdmbgachi-kbgh");
        tl.get(18).add("bahira kalibari hal-bhka");
        tl.get(18).add("sondalia-sxc");
        tl.get(18).add("beliaghata road-bgrd");
        tl.get(18).add("lebutala-lbtl");
        tl.get(18).add("bhasila-bsla");
        tl.get(18).add("harua road-hro");
        tl.get(18).add("kankra mirzangr-kmza");
        tl.get(18).add("malatipur-mpe");
        tl.get(18).add("ghovarash ghona-ggv");
        tl.get(18).add("champapukur-cqr");
        tl.get(18).add("bhyabla halt-bbla");
        tl.get(18).add("basirhat-bsht");
        tl.get(18).add("madhyampur-mpn");
        tl.get(18).add("hasanabad-hnb");

        //SDAH-STB
        tl.get(19).add("sealdah-sdah");
        tl.get(19).add("bidhan nagar-bnxr");
        tl.get(19).add("dum dum-ddj");
        tl.get(19).add("belgharia-blh");
        tl.get(19).add("agarpara-agp");
        tl.get(19).add("sodpur-sep");
        tl.get(19).add("khardaha-kdh");
        tl.get(19).add("titagarh-tgh");
        tl.get(19).add("barrackpore-bp");
        tl.get(19).add("palta-ptf");
        tl.get(19).add("ichhapur-ip");
        tl.get(19).add("shyamnagar-snr");
        tl.get(19).add("jagadal-jgdl");
        tl.get(19).add("kankinara-knr");
        tl.get(19).add("naihati jn-nh");
        tl.get(19).add("halisahar-hlr");
        tl.get(19).add("kanchrapara-kpa");
        tl.get(19).add("kalyani-kyi");
        tl.get(19).add("madanpur-mpj");
        tl.get(19).add("simurali-smx");
        tl.get(19).add("palpara-pxr");
        tl.get(19).add("chakdaha-cdh");
        tl.get(19).add("payradanga-pdx");
        tl.get(19).add("ranaghat jn-rha");
        tl.get(19).add("kalinarynpur jn-klnp");
        tl.get(19).add("habibpur-hbe");
        tl.get(19).add("phulia-flu");
        tl.get(19).add("bathna kritti bas-btkb");
        tl.get(19).add("shantipur-stb");

        //SDAH-KNJ
        tl.get(20).add("sealdah-sdah");
        tl.get(20).add("bidhan nagar-bnxr");
        tl.get(20).add("dum dum-ddj");
        tl.get(20).add("belgharia-blh");
        tl.get(20).add("agarpara-agp");
        tl.get(20).add("sodpur-sep");
        tl.get(20).add("khardaha-kdh");
        tl.get(20).add("titagarh-tgh");
        tl.get(20).add("barrackpore-bp");
        tl.get(20).add("palta-ptf");
        tl.get(20).add("ichhapur-ip");
        tl.get(20).add("shyamnagar-snr");
        tl.get(20).add("jagadal-jgdl");
        tl.get(20).add("kankinara-knr");
        tl.get(20).add("naihati jn-nh");
        tl.get(20).add("halisahar-hlr");
        tl.get(20).add("kanchrapara-kpa");
        tl.get(20).add("kalyani-kyi");
        tl.get(20).add("madanpur-mpj");
        tl.get(20).add("simurali-smx");
        tl.get(20).add("palpara-pxr");
        tl.get(20).add("chakdaha-cdh");
        tl.get(20).add("payradanga-pdx");
        tl.get(20).add("ranaghat jn-rha");
        tl.get(20).add("kalinarynpur jn-klnp");
        tl.get(20).add("birnagar-bij");
        tl.get(20).add("taherpur-thp");
        tl.get(20).add("badkulla-bdz");
        tl.get(20).add("jalalkhali halt-jkl");
        tl.get(20).add("krishngr cty jn-knj");

        //SDAH-KLYM
        tl.get(21).add("sealdah-sdah");
        tl.get(21).add("bidhan nagar-bnxr");
        tl.get(21).add("dum dum-ddj");
        tl.get(21).add("belgharia-blh");
        tl.get(21).add("agarpara-agp");
        tl.get(21).add("sodpur-sep");
        tl.get(21).add("khardaha-kdh");
        tl.get(21).add("titagarh-tgh");
        tl.get(21).add("barrackpore-bp");
        tl.get(21).add("palta-ptf");
        tl.get(21).add("ichhapur-ip");
        tl.get(21).add("shyamnagar-snr");
        tl.get(21).add("jagadal-jgdl");
        tl.get(21).add("kankinara-knr");
        tl.get(21).add("naihati jn-nh");
        tl.get(21).add("halisahar-hlr");
        tl.get(21).add("kanchrapara-kpa");
        tl.get(21).add("kalyani-kyi");
        tl.get(21).add("kalyani silpanchal-klys");
        tl.get(21).add("kalyani ghoshpara-klyg");
        tl.get(21).add("kalyani simanta-klym");

        //SDAH-GEDE
        tl.get(22).add("sealdah-sdah");
        tl.get(22).add("bidhan nagar-bnxr");
        tl.get(22).add("dum dum-ddj");
        tl.get(22).add("belgharia-blh");
        tl.get(22).add("agarpara-agp");
        tl.get(22).add("sodpur-sep");
        tl.get(22).add("khardaha-kdh");
        tl.get(22).add("titagarh-tgh");
        tl.get(22).add("barrackpore-bp");
        tl.get(22).add("palta-ptf");
        tl.get(22).add("ichhapur-ip");
        tl.get(22).add("shyamnagar-snr");
        tl.get(22).add("jagadal-jgdl");
        tl.get(22).add("kankinara-knr");
        tl.get(22).add("naihati jn-nh");
        tl.get(22).add("halisahar-hlr");
        tl.get(22).add("kanchrapara-kpa");
        tl.get(22).add("kalyani-kyi");
        tl.get(22).add("madanpur-mpj");
        tl.get(22).add("simurali-smx");
        tl.get(22).add("palpara-pxr");
        tl.get(22).add("chakdaha-cdh");
        tl.get(22).add("payradanga-pdx");
        tl.get(22).add("ranaghat jn-rha");
        tl.get(22).add("bankim nagar-bnka");
        tl.get(22).add("pancheberia-pncb");
        tl.get(22).add("aranghata-ag");
        tl.get(22).add("bhairgachhi-bhgh");
        tl.get(22).add("bhayna-bhna");
        tl.get(22).add("bagula-bgl");
        tl.get(22).add("mayurhat-myht");
        tl.get(22).add("tarak nagar-tnx");
        tl.get(22).add("majhdia-mij");
        tl.get(22).add("banpur-bpn");
        tl.get(22).add("harish nagar halt-hrsr");
        tl.get(22).add("gede-gede");

        //SDAH-BRPA
        tl.get(23).add("sealdah-sdah");
        tl.get(23).add("bidhan nagar-bnxr");
        tl.get(23).add("dum dum-ddj");
        tl.get(23).add("baranagar road-barn");
        tl.get(23).add("dakhineswar-dake");
        tl.get(23).add("bally ghat-blyg");
        tl.get(23).add("dankuni-dkae");
        tl.get(23).add("gobra-gbra");
        tl.get(23).add("janai road-jox");
        tl.get(23).add("begumpur-bpae");
        tl.get(23).add("baruipara-brpa");

        //SDAH-HNB
        tl.get(24).add("sealdah-sdah");
        tl.get(24).add("sir gurudas banerjee-sgba");
        tl.get(24).add("bidhan nagar-bnxr");
        tl.get(24).add("dum dum-ddj");
        tl.get(24).add("dum dum cant-ddc");
        tl.get(24).add("durganagar-dgnr");
        tl.get(24).add("birati-bbt");
        tl.get(24).add("bisharpara kodalia-brpk");
        tl.get(24).add("new barrackpore-nbe");
        tl.get(24).add("madhyamgram-mmg");
        tl.get(24).add("hridaypur-hhr");
        tl.get(24).add("barasat-bt");
        tl.get(24).add("kazipara-kzpr");
        tl.get(24).add("karea kdmbgachi-kbgh");
        tl.get(24).add("bahira kalibari hal-bhka");
        tl.get(24).add("sondalia-sxc");
        tl.get(24).add("beliaghata road-bgrd");
        tl.get(24).add("lebutala-lbtl");
        tl.get(24).add("bhasila-bsla");
        tl.get(24).add("harua road-hro");
        tl.get(24).add("kankra mirzangr-kmza");
        tl.get(24).add("malatipur-mpe");
        tl.get(24).add("ghovarash ghona-ggv");
        tl.get(24).add("champapukur-cqr");
        tl.get(24).add("bhyabla halt-bbla");
        tl.get(24).add("basirhat-bsht");
        tl.get(24).add("madhyampur-mpn");
        tl.get(24).add("hasanabad-hnb");

        //SDAH-KWAE
        tl.get(25).add("sealdah-sdah");
        tl.get(25).add("sir gurudas banerjee-sgba");
        tl.get(25).add("bidhan nagar-bnxr");
        tl.get(25).add("dum dum-ddj");
        tl.get(25).add("belgharia-blh");
        tl.get(25).add("agarpara-agp");
        tl.get(25).add("sodpur-sep");
        tl.get(25).add("khardaha-kdh");
        tl.get(25).add("titagarh-tgh");
        tl.get(25).add("barrackpore-bp");
        tl.get(25).add("palta-ptf");
        tl.get(25).add("ichhapur-ip");
        tl.get(25).add("shyamnagar-snr");
        tl.get(25).add("jagadal-jgdl");
        tl.get(25).add("kankinara-knr");
        tl.get(25).add("naihati jn-nh");
        tl.get(25).add("garifa-gfae");
        tl.get(25).add("hooghly-hgy");
        tl.get(25).add("bandel jn-bdc");
        tl.get(25).add("bansh baria-bsae");
        tl.get(25).add("tribeni-tbae");
        tl.get(25).add("kuntighat-kju");
        tl.get(25).add("dumurdaha-dmle");
        tl.get(25).add("khamargachhi-kmae");
        tl.get(25).add("jirat-jit");
        tl.get(25).add("balagarh-bgae");
        tl.get(25).add("somra bazar-soae");
        tl.get(25).add("behula-bhla");
        tl.get(25).add("guptipara-gpae");
        tl.get(25).add("ambika kalna-abka");
        tl.get(25).add("baghnapara-bgra");
        tl.get(25).add("dhatrigram-dtae");
        tl.get(25).add("samudra garh-smae");
        tl.get(25).add("kalinagar-klnt");
        tl.get(25).add("nabadwip dham-ndae");
        tl.get(25).add("bhandartikuri-bfz");
        tl.get(25).add("purbasthali-psae");
        tl.get(25).add("mertala phaleya halt-mtfa");
        tl.get(25).add("lakshmipur-lkx");
        tl.get(25).add("belerhat-bqy");
        tl.get(25).add("patuli-ptae");
        tl.get(25).add("agradwip-agae");
        tl.get(25).add("sahebtala halt-shba");
        tl.get(25).add("dainhat-dhae");
        tl.get(25).add("katwa jn-kwae");

        //SDAH-BNJ
        tl.get(26).add("sealdah-sdah");
        tl.get(26).add("sir gurudas banerjee-sgba");
        tl.get(26).add("bidhan nagar-bnxr");
        tl.get(26).add("dum dum-ddj");
        tl.get(26).add("dum dum cant-ddc");
        tl.get(26).add("durganagar-dgnr");
        tl.get(26).add("birati-bbt");
        tl.get(26).add("bisharpara kodalia-brpk");
        tl.get(26).add("new barrackpore-nbe");
        tl.get(26).add("madhyamgram-mmg");
        tl.get(26).add("hridaypur-hhr");
        tl.get(26).add("barasat-bt");
        tl.get(26).add("bamangachhi-bmg");
        tl.get(26).add("dattapukur-dtk");
        tl.get(26).add("bira-bira");
        tl.get(26).add("guma-guma");
        tl.get(26).add("ashok nagar rd-askr");
        tl.get(26).add("habra-hb");
        tl.get(26).add("sanhati-snht");
        tl.get(26).add("machchalandapur-msl");
        tl.get(26).add("gobardanga-gbg");
        tl.get(26).add("thakurnagar-tknr");
        tl.get(26).add("chandpara-cdp");
        tl.get(26).add("bibhutibhushan-bnaa");
        tl.get(26).add("bangaon jn-bnj");

        //SDAH-BWN
        tl.get(27).add("sealdah-sdah");
        tl.get(27).add("bidhan nagar-bnxr");
        tl.get(27).add("dum dum-ddj");
        tl.get(27).add("belgharia-blh");
        tl.get(27).add("agarpara-agp");
        tl.get(27).add("sodpur-sep");
        tl.get(27).add("khardaha-kdh");
        tl.get(27).add("titagarh-tgh");
        tl.get(27).add("barrackpore-bp");
        tl.get(27).add("palta-ptf");
        tl.get(27).add("ichhapur-ip");
        tl.get(27).add("shyamnagar-snr");
        tl.get(27).add("jagadal-jgdl");
        tl.get(27).add("kankinara-knr");
        tl.get(27).add("naihati jn-nh");
        tl.get(27).add("garifa-gfae");
        tl.get(27).add("hooghly ghat-hyg");
        tl.get(27).add("bandel jn-bdc");
        tl.get(27).add("adi saptagram-adst");
        tl.get(27).add("magra-mug");
        tl.get(27).add("talandu-tlo");
        tl.get(27).add("khanyan-khn");
        tl.get(27).add("pundooah-pda");
        tl.get(27).add("simlagarh-slg");
        tl.get(27).add("bainchigram-bcgm");
        tl.get(27).add("bainchi-boi");
        tl.get(27).add("debipur-dbp");
        tl.get(27).add("bagila-bgf");
        tl.get(27).add("memari-mym");
        tl.get(27).add("nimo-nmf");
        tl.get(27).add("rasulpur-rslr");
        tl.get(27).add("palsit-plae");
        tl.get(27).add("saktigarh-skg");
        tl.get(27).add("gangpur-grp");
        tl.get(27).add("barddhaman-bwn");

        //SHM-MCA
        tl.get(28).add("shalimar-shm");
        tl.get(28).add("padmapukar-pdpk");
        tl.get(28).add("santragachi jn-src");
        tl.get(28).add("maurigram-mrgm");
        tl.get(28).add("andul-adl");
        tl.get(28).add("sankrall-sel");
        tl.get(28).add("abada-abb");
        tl.get(28).add("nalpur-nalr");
        tl.get(28).add("bauria jn-bva");
        tl.get(28).add("chengel-cga");
        tl.get(28).add("phuleswar-flr");
        tl.get(28).add("ulubaria-ulb");
        tl.get(28).add("bir shibpur-bsbp");
        tl.get(28).add("kulgachia-kgy");
        tl.get(28).add("bagnan-bzn");
        tl.get(28).add("ghoraghata-ggta");
        tl.get(28).add("deulti-dte");
        tl.get(28).add("kolaghat goods-kig");
        tl.get(28).add("mecheda-mca");

        //GEDE-MJT
        tl.get(29).add("gede-gede");
        tl.get(29).add("harish nagar halt-hrsr");
        tl.get(29).add("banpur-bpn");
        tl.get(29).add("majhdia-mij");
        tl.get(29).add("tarak nagar-tnx");
        tl.get(29).add("mayurhat-myht");
        tl.get(29).add("bagula-bgl");
        tl.get(29).add("bhayna-bhna");
        tl.get(29).add("bhairgachhi-bhgh");
        tl.get(29).add("aranghata-ag");
        tl.get(29).add("pancheberia-pncb");
        tl.get(29).add("bankim nagar-bnka");
        tl.get(29).add("ranaghat jn-rha");
        tl.get(29).add("payradanga-pdx");
        tl.get(29).add("chakdaha-cdh");
        tl.get(29).add("palpara-pxr");
        tl.get(29).add("simurali-smx");
        tl.get(29).add("madanpur-mpj");
        tl.get(29).add("kalyani-kyi");
        tl.get(29).add("kanchrapara-kpa");
        tl.get(29).add("halisahar-hlr");
        tl.get(29).add("naihati jn-nh");
        tl.get(29).add("kankinara-knr");
        tl.get(29).add("jagadal-jgdl");
        tl.get(29).add("shyamnagar-snr");
        tl.get(29).add("ichhapur-ip");
        tl.get(29).add("palta-ptf");
        tl.get(29).add("barrackpore-bp");
        tl.get(29).add("titagarh-tgh");
        tl.get(29).add("khardaha-kdh");
        tl.get(29).add("sodpur-sep");
        tl.get(29).add("agarpara-agp");
        tl.get(29).add("belgharia-blh");
        tl.get(29).add("dum dum-ddj");
        tl.get(29).add("bidhan nagar-bnxr");
        tl.get(29).add("sir gurudas banerjee-sgba");
        tl.get(29).add("park circus-pqs");
        tl.get(29).add("ballygunge junction-bln");
        tl.get(29).add("lake gardens-lkf");
        tl.get(29).add("new alipur-nacc");
        tl.get(29).add("patipukur-ptkr");
        tl.get(29).add("kolkata-koaa");
        tl.get(29).add("talla-tala");
        tl.get(29).add("bag bazar-bbr");
        tl.get(29).add("sovabazar ahiritola-sola");
        tl.get(29).add("barra bazar-bzb");
        tl.get(29).add("benoybadaldineshbag-bbdb");
        tl.get(29).add("eden gardens-edg");
        tl.get(29).add("princep ghat-ppgt");
        tl.get(29).add("khidirpur-kirp");
        tl.get(29).add("re mount road-rmtr");
        tl.get(29).add("majerhat-mjt");

        //MJT-RHA
        tl.get(30).add("majerhat-mjt");
        tl.get(30).add("re mount road-rmtr");
        tl.get(30).add("khidirpur-kirp");
        tl.get(30).add("princep ghat-ppgt");
        tl.get(30).add("eden gardens-edg");
        tl.get(30).add("benoybadaldineshbag-bbdb");
        tl.get(30).add("barra bazar-bzb");
        tl.get(30).add("sovabazar ahiritola-sola");
        tl.get(30).add("bag bazar-bbr");
        tl.get(30).add("talla-tala");
        tl.get(30).add("kolkata-koaa");
        tl.get(30).add("patipukur-ptkr");
        tl.get(30).add("new alipur-nacc");
        tl.get(30).add("lake gardens-lkf");
        tl.get(30).add("ballygunge junction-bln");
        tl.get(30).add("park circus-pqs");
        tl.get(30).add("sir gurudas banerjee-sgba");
        tl.get(30).add("bidhan nagar-bnxr");
        tl.get(30).add("dum dum-ddj");
        tl.get(30).add("belgharia-blh");
        tl.get(30).add("agarpara-agp");
        tl.get(30).add("sodpur-sep");
        tl.get(30).add("khardaha-kdh");
        tl.get(30).add("titagarh-tgh");
        tl.get(30).add("barrackpore-bp");
        tl.get(30).add("palta-ptf");
        tl.get(30).add("ichhapur-ip");
        tl.get(30).add("shyamnagar-snr");
        tl.get(30).add("jagadal-jgdl");
        tl.get(30).add("kankinara-knr");
        tl.get(30).add("naihati jn-nh");
        tl.get(30).add("halisahar-hlr");
        tl.get(30).add("kanchrapara-kpa");
        tl.get(30).add("kalyani-kyi");
        tl.get(30).add("madanpur-mpj");
        tl.get(30).add("simurali-smx");
        tl.get(30).add("palpara-pxr");
        tl.get(30).add("chakdaha-cdh");
        tl.get(30).add("payradanga-pdx");
        tl.get(30).add("ranaghat jn-rha");

        //SDAH-KOAA
        tl.get(31).add("sealdah-sdah");
        tl.get(31).add("park circus-pqs");
        tl.get(31).add("ballygunge junction-bln");
        tl.get(31).add("lake gardens-lkf");
        tl.get(31).add("new alipur-nacc");
        tl.get(31).add("majerhat-mjt");
        tl.get(31).add("re mount road-rmtr");
        tl.get(31).add("khidirpur-kirp");
        tl.get(31).add("princep ghat-ppgt");
        tl.get(31).add("eden gardens-edg");
        tl.get(31).add("benoybadaldineshbag-bbdb");
        tl.get(31).add("barra bazar-bzb");
        tl.get(31).add("sovabazar ahiritola-sola");
        tl.get(31).add("bag bazar-bbr");
        tl.get(31).add("talla-tala");
        tl.get(31).add("kolkata-koaa");

        return tl;
    }
}
