package com.thebrokegamers.kolkatanavigator;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class AutoFragment extends Fragment {

    AutoCompleteTextView actv;
    AutoCompleteTextView actvd;
    private ImageView autoAtoBImage, autoStandImage, img;
    private RelativeLayout searchAuto;
    private RelativeLayout autoButtonLeft;
    private RelativeLayout autoButtonRight;
    private TestAdapter dbAdapter;
    private Typeface font;
    private ArrayAdapter<ArrayList<String>> adapterList;
    private TextView autoAtoBText, autoStandText;
    private ArrayList<ArrayList<String>> autoItems;
    private boolean autoStand;
    private TextInputLayout topText;
    private TextInputLayout bottomText;
    private UserDatabaseAdapter userDbAdapter;


    public AutoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_auto, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);

    }

    private void initUI(View view) {
        Bundle bundle = this.getArguments();
        font = Typeface.createFromAsset(getActivity().getAssets(), "Exo2-Regular.ttf");
        autoStand = false;
        autoItems = new ArrayList<ArrayList<String>>();
        userDbAdapter = new UserDatabaseAdapter(getActivity());
        dbAdapter = new TestAdapter(getActivity());
        dbAdapter.createDatabase();
        dbAdapter.open();
        actv = (AutoCompleteTextView) view.findViewById(R.id.sourceInputAuto);
        actv.setTypeface(font);
        actvd = (AutoCompleteTextView) view.findViewById(R.id.destinationInputAuto);
        actvd.setTypeface(font);
        actv.setThreshold(1);//will start working from first character
        actvd.setThreshold(1);
        actv.setAdapter(new CustomArrayAdapter
                (getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>(AddWordsAuto())));
        actvd.setAdapter(new CustomArrayAdapter
                (getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>(AddWordsAuto())));
        topText = (TextInputLayout) view.findViewById(R.id.top_text);
        topText.setTypeface(font);
        bottomText = (TextInputLayout) view.findViewById(R.id.bottom_text);
        bottomText.setTypeface(font);
        img = (ImageView) view.findViewById(R.id.sourceDestinationImageAuto);

        ListView autoList = (ListView) view.findViewById(R.id.listViewAuto);
        adapterList = new AutoListAdapter(getActivity(), autoItems);
        autoList.setAdapter(adapterList);


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

        TextView searchText = (TextView) view.findViewById(R.id.autoSearchText);
        searchText.setTypeface(font);
        searchAuto = (RelativeLayout) view.findViewById(R.id.searchAuto);
        searchAuto.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                              imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


                                              // Toast.makeText(getActivity(), "AUTO SEARCH", Toast.LENGTH_SHORT).show();
                                              autoItems.clear();
                                              if (autoStand) {
                                                  if (actv.getText().toString().equals("") || actv.getText().toString().length() == 0) {
                                                      Toast.makeText(getActivity(), "Auto Stand cannot be empty!", Toast.LENGTH_SHORT).show();
                                                  } else {
                                                      autoItems.addAll(dbAdapter.getAutoStand(actv.getText().toString().toLowerCase()));
                                                      if (autoItems.size() == 0) {
                                                          Toast.makeText(getActivity(), "Please choose Auto Stand from the available options!", Toast.LENGTH_SHORT).show();
                                                      }
                                                  }
                                              } else {
                                                  if (actv.getText().toString().equals("") || actv.getText().toString().length() == 0
                                                          || actvd.getText().toString().equals("") || actvd.getText().toString().length() == 0) {
                                                      Toast.makeText(getActivity(), "Source or Destination cannot be empty!", Toast.LENGTH_SHORT).show();
                                                  } else {
                                                      autoItems.addAll(dbAdapter.getAutos(actv.getText().toString().toLowerCase(), actvd.getText().toString().toLowerCase()));
                                                      if (autoItems.size() == 0) {
                                                          Toast.makeText(getActivity(), "No Autos found!", Toast.LENGTH_SHORT).show();
                                                      } else {
                                                          Calendar c = Calendar.getInstance();
                                                          String date = c.get(Calendar.YEAR) + " " + c.get(Calendar.MONTH) + " " + c.get(Calendar.DAY_OF_MONTH);
                                                          userDbAdapter.insertRecents("AUTO", actv.getText().toString(), actvd.getText().toString(), date);
                                                      }
                                                  }
                                              }

                                              adapterList.notifyDataSetChanged();
                                          }
                                      }

        );


        autoButtonLeft = (RelativeLayout) view.findViewById(R.id.autoButtonLeft);
        autoAtoBText = (TextView) view.findViewById(R.id.autoAtoBText);
        autoAtoBText.setTypeface(font);
        autoAtoBImage = (ImageView) view.findViewById(R.id.autoAtoBImage);
        autoButtonLeft.setOnClickListener(new View.OnClickListener()

                                          {
                                              @Override
                                              public void onClick(View v) {

                                                  autoStand = false;
                                                  actvd.setFocusable(true);
                                                  actvd.setFocusableInTouchMode(true);
                                                  actvd.setEnabled(true);
                                                  autoAtoBImage.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorWhite));
                                                  autoAtoBText.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                                                  autoStandImage.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                                                  autoStandText.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
                                                  actv.setText("");
                                                  actvd.setText("");
                                                  actv.setAdapter(new CustomArrayAdapter
                                                          (getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>(AddWordsAuto())));
                                                  actvd.setAdapter(new CustomArrayAdapter
                                                          (getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>(AddWordsAuto())));

                                              }
                                          }

        );

        autoButtonRight = (RelativeLayout) view.findViewById(R.id.autoButtonRight);
        autoStandText = (TextView) view.findViewById(R.id.autoStandText);
        autoStandText.setTypeface(font);
        autoStandImage = (ImageView) view.findViewById(R.id.autoStandImage);
        autoButtonRight.setOnClickListener(new View.OnClickListener()

                                           {
                                               @Override
                                               public void onClick(View v) {

                                                   autoStand = true;
                                                   autoStandImage.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorWhite));
                                                   autoStandText.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                                                   autoAtoBImage.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                                                   autoAtoBText.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
                                                   actv.setText("");
                                                   actvd.setText("");
                                                   actv.setAdapter(new CustomArrayAdapter
                                                           (getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>(AddWordsAutoStand())));
                                                   actvd.setFocusable(false);
                                                   actvd.setFocusableInTouchMode(false);
                                                   actvd.setEnabled(false);
                                                   actv.setFocusable(false);
                                                   actv.setFocusableInTouchMode(false);
                                                   actv.setFocusable(true);
                                                   actv.setFocusableInTouchMode(true);
                                               }
                                           }

        );

        if (bundle != null) {
            actv.setText(bundle.getString("source"));
            actvd.setText(bundle.getString("destination"));

            autoItems.addAll(dbAdapter.getAutos(actv.getText().toString().toLowerCase(), actvd.getText().toString().toLowerCase()));
            if (autoItems.size() == 0) {
                Toast.makeText(getActivity(), "No Autos found!", Toast.LENGTH_SHORT).show();
            } else {
                Calendar c = Calendar.getInstance();
                String date = c.get(Calendar.YEAR) + " " + c.get(Calendar.MONTH) + " " + c.get(Calendar.DAY_OF_MONTH);
                userDbAdapter.insertRecents("AUTO", actv.getText().toString(), actvd.getText().toString(), date);
            }

        }
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

    public static ArrayList<String> AddWordsAuto() {
        ArrayList<String> autoStoppages = new ArrayList<String>();
        autoStoppages.add("17a bus stand");
        autoStoppages.add("4b bus stand(cossipore)");
        autoStoppages.add("4no bridge");
        autoStoppages.add("9 & 15 bus stand");
        autoStoppages.add("ahiritola");
        autoStoppages.add("ahiritola launch ghat");
        autoStoppages.add("ajanta");
        autoStoppages.add("ajc bose road ");
        autoStoppages.add("akra fatak");
        autoStoppages.add("akra fatak road");
        autoStoppages.add("aleya cinema");
        autoStoppages.add("alipur bridge");
        autoStoppages.add("alipur judge court");
        autoStoppages.add("alipur police court");
        autoStoppages.add("alipur road");
        autoStoppages.add("anandamoyee tala");
        autoStoppages.add("andool raj road crossing");
        autoStoppages.add("apc road");
        autoStoppages.add("apc road crossing");
        autoStoppages.add("approach road");
        autoStoppages.add("aurobindo sarani");
        autoStoppages.add("aurobindo setu");
        autoStoppages.add("azad hind bag");
        autoStoppages.add("babubagan");
        autoStoppages.add("badartala");
        autoStoppages.add("bagbazar bata");
        autoStoppages.add("bagbazar launch ghat");
        autoStoppages.add("bagbazar (rabindra sarani)");
        autoStoppages.add("bagbazar street");
        autoStoppages.add("baghmari");
        autoStoppages.add("baguihati");
        autoStoppages.add("baishali");
        autoStoppages.add("baishnab sett street");
        autoStoppages.add("ballygunge");
        autoStoppages.add("ballygunge bus stand");
        autoStoppages.add("ballygunge circular road");
        autoStoppages.add("ballygunge phari");
        autoStoppages.add("ballygunge station");
        autoStoppages.add("baman ghata bantala");
        autoStoppages.add("barabazar");
        autoStoppages.add("basanti devi college");
        autoStoppages.add("bb chatterjee road");
        autoStoppages.add("beadon square");
        autoStoppages.add("beadon street");
        autoStoppages.add("beadon street apc road");
        autoStoppages.add("beckbagan");
        autoStoppages.add("behala");
        autoStoppages.add("behala 14no bus stand");
        autoStoppages.add("behala chowrasta");
        autoStoppages.add("behala oxytown");
        autoStoppages.add("behala karunamoyee");
        autoStoppages.add("behala sakherbazar");
        autoStoppages.add("behala thana");
        autoStoppages.add("behala tram depot");
        autoStoppages.add("belgachia bridge");
        autoStoppages.add("belgachia metro");
        autoStoppages.add("beliaghata id hospital");
        autoStoppages.add("beliaghata main road");
        autoStoppages.add("beltala road");
        autoStoppages.add("belvedere road");
        autoStoppages.add("bengal chemical");
        autoStoppages.add("bharat sevasram sangha");
        autoStoppages.add("bidhan sarani");
        autoStoppages.add("bidhan sarani apc road crossing");
        autoStoppages.add("bijan setu");
        autoStoppages.add("bijaygarh college");
        autoStoppages.add("bikramgarer more");
        autoStoppages.add("biplabi bankim ghosh sarani");
        autoStoppages.add("biresh guha road");
        autoStoppages.add("bk pal");
        autoStoppages.add("bk pal avenue");
        autoStoppages.add("bondel gate");
        autoStoppages.add("bondel road");
        autoStoppages.add("bosepukur");
        autoStoppages.add("br singh hospital");
        autoStoppages.add("bright street");
        autoStoppages.add("broad street");
        autoStoppages.add("budge budge road");
        autoStoppages.add("burdhaman road");
        autoStoppages.add("buro shibtala");
        autoStoppages.add("canal west road");
        autoStoppages.add("circular garden reach road");
        autoStoppages.add("chakraberia");
        autoStoppages.add("charu market");
        autoStoppages.add("chetla");
        autoStoppages.add("chetla(dalmiya park)");
        autoStoppages.add("chetla park");
        autoStoppages.add("chiria more");
        autoStoppages.add("chitrapuri cinema");
        autoStoppages.add("chitta ranjan hospital");
        autoStoppages.add("cit building");
        autoStoppages.add("cit padmapukur(entally)");
        autoStoppages.add("cit road");
        autoStoppages.add("cornfield road");
        autoStoppages.add("cossipore 4b bus stand");
        autoStoppages.add("cossipore ferry ghat");
        autoStoppages.add("cossipore road");
        autoStoppages.add("cr hospital");
        autoStoppages.add("dakghar");
        autoStoppages.add("darga road");
        autoStoppages.add("deshapriya park");
        autoStoppages.add("dhaka kalibari");
        autoStoppages.add("dhakuria");
        autoStoppages.add("dhakuria bridge");
        autoStoppages.add("dhalai bridge");
        autoStoppages.add("dhapa");
        autoStoppages.add("dhapa math pukur");
        autoStoppages.add("dharmatala");
        autoStoppages.add("diamond city");
        autoStoppages.add("diamond harbour road");
        autoStoppages.add("dinendra street");
        autoStoppages.add("dumdum gs colony");
        autoStoppages.add("dumdum road");
        autoStoppages.add("dumdum road(30a bus stand)");
        autoStoppages.add("dumdum station");
        autoStoppages.add("durgapur bridge");
        autoStoppages.add("duttabagan");
        autoStoppages.add("ekbalpur road");
        autoStoppages.add("ekdalia");
        autoStoppages.add("elliot road");
        autoStoppages.add("esi hospital");
        autoStoppages.add("fern road");
        autoStoppages.add("gajnabi setu");
        autoStoppages.add("gandhi maidan");
        autoStoppages.add("ganesh talkies");
        autoStoppages.add("garden reach road");
        autoStoppages.add("garfa");
        autoStoppages.add("garia");
        autoStoppages.add("garia station");
        autoStoppages.add("gariahat");
        autoStoppages.add("gariahat crossing");
        autoStoppages.add("gas street");
        autoStoppages.add("girish mancha");
        autoStoppages.add("golpark");
        autoStoppages.add("gopal nagar");
        autoStoppages.add("gosai park");
        autoStoppages.add("gouribari");
        autoStoppages.add("grant street");
        autoStoppages.add("greenfield city");
        autoStoppages.add("gun shell factory");
        autoStoppages.add("haltu");
        autoStoppages.add("haltu(kayastha para)");
        autoStoppages.add("haridevpur");
        autoStoppages.add("hastings park");
        autoStoppages.add("hazra");
        autoStoppages.add("hazra more");
        autoStoppages.add("hazra road");
        autoStoppages.add("hiland park");
        autoStoppages.add("hindustan sweets");
        autoStoppages.add("hudco");
        autoStoppages.add("indra biswas road");
        autoStoppages.add("jadavpur");
        autoStoppages.add("jadavpur ps");
        autoStoppages.add("james long sarani");
        autoStoppages.add("james long sarani chowmatha");
        autoStoppages.add("jin masjid");
        autoStoppages.add("jorabagan power house");
        autoStoppages.add("kabardanga");
        autoStoppages.add("kadapara");
        autoStoppages.add("kaizer street");
        autoStoppages.add("kalighat bridge");
        autoStoppages.add("kalikapur");
        autoStoppages.add("kamal talkies");
        autoStoppages.add("kankurgachi");
        autoStoppages.add("kankurgachi more");
        autoStoppages.add("karunamoyee");
        autoStoppages.add("kasba");
        autoStoppages.add("kasba connector");
        autoStoppages.add("kb/kc block");
        autoStoppages.add("kc road");
        autoStoppages.add("keshab sen street");
        autoStoppages.add("khalpar garia");
        autoStoppages.add("khanna");
        autoStoppages.add("khidirpur");
        autoStoppages.add("kk tagore street");
        autoStoppages.add("kudghat");
        autoStoppages.add("kustia");
        autoStoppages.add("lake gardens");
        autoStoppages.add("lake town");
        autoStoppages.add("lalkuthi");
        autoStoppages.add("lohapool");
        autoStoppages.add("lords bakery");
        autoStoppages.add("madan mohan burman street");
        autoStoppages.add("majerhat");
        autoStoppages.add("manicktala");
        autoStoppages.add("manicktala ps");
        autoStoppages.add("manicktala main road");
        autoStoppages.add("mani sanyal sarani");
        autoStoppages.add("mechua fruit market");
        autoStoppages.add("menoka cinema");
        autoStoppages.add("metiabruz");
        autoStoppages.add("metiabruz kanchi sarak");
        autoStoppages.add("metiabruz ps");
        autoStoppages.add("metiabruz(grse gate)");
        autoStoppages.add("metiabruz kanchi sarak");
        autoStoppages.add("metropolitan cooperative housing");
        autoStoppages.add("mg road");
        autoStoppages.add("milk colony");
        autoStoppages.add("mollargate");
        autoStoppages.add("mominpur");
        autoStoppages.add("monoharpukur road");
        autoStoppages.add("motor vehicle");
        autoStoppages.add("mouchak(golpark)");
        autoStoppages.add("mudiali");
        autoStoppages.add("narkeldanga main road");
        autoStoppages.add("navina");
        autoStoppages.add("new alipur");
        autoStoppages.add("new alipur petrol pump");
        autoStoppages.add("new howrah bridge");
        autoStoppages.add("new park street");
        autoStoppages.add("orient row");
        autoStoppages.add("padmapukur square");
        autoStoppages.add("paharpur road");
        autoStoppages.add("paikpara 1st row");
        autoStoppages.add("paikpara 2no stand");
        autoStoppages.add("pamar bazar crossing");
        autoStoppages.add("panchanan tala");
        autoStoppages.add("park circus");
        autoStoppages.add("park circus connector");
        autoStoppages.add("park circus maidan");
        autoStoppages.add("park circus tram depot");
        autoStoppages.add("park street");
        autoStoppages.add("parnasree pally");
        autoStoppages.add("patuli upanagari");
        autoStoppages.add("phoolbagan");
        autoStoppages.add("phoolbagan more");
        autoStoppages.add("picnic garden");
        autoStoppages.add("picnic garden road");
        autoStoppages.add("pilkhana");
        autoStoppages.add("pratapaditya road");
        autoStoppages.add("pratapaditya road crossing(rashbehari)");
        autoStoppages.add("prince anwar shah");
        autoStoppages.add("rabindra sarani crossing");
        autoStoppages.add("rabindra sarobar");
        autoStoppages.add("rafi ahamed kidwai road");
        autoStoppages.add("raj ballav para");
        autoStoppages.add("rajabagan bus stand");
        autoStoppages.add("rajabazar");
        autoStoppages.add("raja dinendra street");
        autoStoppages.add("rajdanga bazar");
        autoStoppages.add("rakhal das auddy road");
        autoStoppages.add("ramani chatterjee road");
        autoStoppages.add("ram lal bazar");
        autoStoppages.add("ramnagar");
        autoStoppages.add("ramnagar more");
        autoStoppages.add("rani harshamoyee road");
        autoStoppages.add("rashbehari");
        autoStoppages.add("rashbehari avenue");
        autoStoppages.add("rath tala");
        autoStoppages.add("rg kar hospital");
        autoStoppages.add("richie road");
        autoStoppages.add("rotary netralaya");
        autoStoppages.add("roy bahadur road");
        autoStoppages.add("ruby");
        autoStoppages.add("russa road");
        autoStoppages.add("sahapur");
        autoStoppages.add("sahapur road");
        autoStoppages.add("sailasri cinema");
        autoStoppages.add("sai main gate");
        autoStoppages.add("saltlake 13no tank");
        autoStoppages.add("saltlake karunamoyee");
        autoStoppages.add("saltlake pnb");
        autoStoppages.add("santoshpur");
        autoStoppages.add("santoshpur road(ghosh para road)");
        autoStoppages.add("satgachi(topsia)");
        autoStoppages.add("sc mallick road");
        autoStoppages.add("sdf building");
        autoStoppages.add("sealdah court complex");
        autoStoppages.add("sealdah(chabi ghar)");
        autoStoppages.add("seven tanks");
        autoStoppages.add("shakuntala park");
        autoStoppages.add("siemens");
        autoStoppages.add("singhgarh");
        autoStoppages.add("sinthee more");
        autoStoppages.add("sn banerjee road");
        autoStoppages.add("south city");
        autoStoppages.add("southern avenue");
        autoStoppages.add("sovabazar");
        autoStoppages.add("sovabazar launch ghat");
        autoStoppages.add("sovabazar metro");
        autoStoppages.add("sp mukherjee road");
        autoStoppages.add("strand bank");
        autoStoppages.add("tagore park");
        autoStoppages.add("taratala");
        autoStoppages.add("taratala road");
        autoStoppages.add("thakurpukur 3a bus stand");
        autoStoppages.add("thakurpukur cancer hospital");
        autoStoppages.add("tiljala");
        autoStoppages.add("tollygunge");
        autoStoppages.add("tollygunge circular road");
        autoStoppages.add("tollygunge phari");
        autoStoppages.add("tollygunge metro");
        autoStoppages.add("tollygunge tram depot");
        autoStoppages.add("topsia");
        autoStoppages.add("townsend road");
        autoStoppages.add("triangular park");
        autoStoppages.add("ultadanga");
        autoStoppages.add("ultadanga station");
        autoStoppages.add("vip crossing");

        for (int i = 0; i < autoStoppages.size(); i++) {
            autoStoppages.set(i, autoStoppages.get(i).toUpperCase());
        }

        return autoStoppages;
    }


    public static ArrayList<String> AddWordsAutoStand() {
        ArrayList<String> autoStands = new ArrayList<String>();
        autoStands.add("4no bridge");
        autoStands.add("9 & 15 bus stand");
        autoStands.add("ahiritola");
        autoStands.add("ahiritola launch ghat");
        autoStands.add("akra fatak");
        autoStands.add("akra fatak road");
        autoStands.add("aleya cinema");
        autoStands.add("alipur judge court");
        autoStands.add("anandamoyee tala");
        autoStands.add("badartala");
        autoStands.add("bagbazar (rabindra sarani)");
        autoStands.add("bagbazar bata");
        autoStands.add("bagbazar launch ghat");
        autoStands.add("baguihati");
        autoStands.add("ballygunge bus stand");
        autoStands.add("ballygunge phari");
        autoStands.add("ballygunge station");
        autoStands.add("baman ghata bantala");
        autoStands.add("barabazar");
        autoStands.add("basanti devi college");
        autoStands.add("baishali");
        autoStands.add("beadon square");
        autoStands.add("beadon street");
        autoStands.add("beadon street apc road");
        autoStands.add("behala");
        autoStands.add("behala 14no bus stand");
        autoStands.add("behala chowrasta");
        autoStands.add("behala sakherbazar");
        autoStands.add("behala thana");
        autoStands.add("behala tram depot");
        autoStands.add("belgachia metro");
        autoStands.add("beliaghata id hospital");
        autoStands.add("bengal chemical");
        autoStands.add("bijaygarh college");
        autoStands.add("bk pal");
        autoStands.add("bondel gate");
        autoStands.add("bosepukur");
        autoStands.add("br singh hospital");
        autoStands.add("chakraberia");
        autoStands.add("chetla");
        autoStands.add("chetla(dalmiya park)");
        autoStands.add("chetla park");
        autoStands.add("chiria more");
        autoStands.add("chitrapuri cinema");
        autoStands.add("chitta ranjan hospital");
        autoStands.add("cit building");
        autoStands.add("cit padmapukur(entally)");
        autoStands.add("cornfield road");
        autoStands.add("cossipore 4b bus stand");
        autoStands.add("cr hospital");
        autoStands.add("dakghar");
        autoStands.add("deshapriya park");
        autoStands.add("dhapa");
        autoStands.add("dhapa math pukur");
        autoStands.add("dharmatala");
        autoStands.add("dumdum road(30a bus stand)");
        autoStands.add("dumdum gs colony");
        autoStands.add("dumdum station");
        autoStands.add("ekdalia");
        autoStands.add("esi hospital");
        autoStands.add("ganesh talkies");
        autoStands.add("garia");
        autoStands.add("garia station");
        autoStands.add("gariahat");
        autoStands.add("golpark");
        autoStands.add("gun shell factory");
        autoStands.add("haltu");
        autoStands.add("haltu(kayastha para)");
        autoStands.add("haridevpur");
        autoStands.add("hazra");
        autoStands.add("hazra more");
        autoStands.add("hudco");
        autoStands.add("jadavpur");
        autoStands.add("jadavpur ps");
        autoStands.add("jorabagan power house");
        autoStands.add("judge court");
        autoStands.add("kabardanga");
        autoStands.add("kadapara");
        autoStands.add("kaizer street");
        autoStands.add("kankurgachi more");
        autoStands.add("karunamoyee");
        autoStands.add("kasba");
        autoStands.add("khidirpur");
        autoStands.add("kudghat");
        autoStands.add("kustia");
        autoStands.add("lake gardens");
        autoStands.add("lake town");
        autoStands.add("lohapool");
        autoStands.add("manicktala");
        autoStands.add("mechua fruit market");
        autoStands.add("metiabruz");
        autoStands.add("metiabruz kanchi sarak");
        autoStands.add("metiabruz ps");
        autoStands.add("metiabruz(grse gate)");
        autoStands.add("mg road");
        autoStands.add("milk colony");
        autoStands.add("mollargate");
        autoStands.add("mouchak(golpark)");
        autoStands.add("mudiali");
        autoStands.add("orient row");
        autoStands.add("paikpara 2no stand");
        autoStands.add("panchanan tala");
        autoStands.add("park circus");
        autoStands.add("park circus tram depot");
        autoStands.add("parnasree pally");
        autoStands.add("phoolbagan");
        autoStands.add("picnic garden");
        autoStands.add("pilkhana");
        autoStands.add("pratapaditya road crossing(rashbehari)");
        autoStands.add("rajabazar");
        autoStands.add("raja bagan bus stand");
        autoStands.add("rajdanga bazar");
        autoStands.add("ramnagar more");
        autoStands.add("rashbehari");
        autoStands.add("rg kar hospital");
        autoStands.add("rotary netralaya");
        autoStands.add("ruby");
        autoStands.add("sahapur");
        autoStands.add("sailasri cinema");
        autoStands.add("sai main gate");
        autoStands.add("saltlake 13no tank");
        autoStands.add("saltlake karunamoyee");
        autoStands.add("santoshpur");
        autoStands.add("satgachi(topsia)");
        autoStands.add("sdf building");
        autoStands.add("sealdah court complex");
        autoStands.add("sealdah(chabi ghar)");
        autoStands.add("sinthee more");
        autoStands.add("southern avenue");
        autoStands.add("sovabazar");
        autoStands.add("sovabazar launch ghat");
        autoStands.add("sp mukherjee road");
        autoStands.add("tagore park");
        autoStands.add("taratala");
        autoStands.add("thakurpukur 3a bus stand");
        autoStands.add("thakurpukur cancer hospital");
        autoStands.add("tiljala");
        autoStands.add("tollygunge");
        autoStands.add("tollygunge phari");
        autoStands.add("tollygunge tram depot");
        autoStands.add("topsia");
        autoStands.add("triangular park");
        autoStands.add("ultadanga station");
        autoStands.add("vip crossing");

        for (int i = 0; i < autoStands.size(); i++) {
            autoStands.set(i, autoStands.get(i).toUpperCase());
        }

        return autoStands;
    }

}
