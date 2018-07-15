package com.thebrokegamers.kolkatanavigator;


import android.content.Context;
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
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFavouritesFragment extends Fragment {

    private int TOTAL_BUTTONS = 5;
    private Typeface font;
    private Typeface fontBold;
    private ArrayList<ImageView> setTouchListeners;
    private ArrayList<ImageView> icons;
    private ArrayList<TextView> changeFonts;
    private Fragment fragment = null;
    private int selected;
    private AutoCompleteTextView actv;
    private AutoCompleteTextView actvd;
    private TextInputLayout topText;
    private TextInputLayout bottomText;
    private ImageView sourceDestinationIcon;
    private ArrayList<String> sourceStoppages;
    private ArrayList<String> destinationStoppages;
    private ArrayList<ArrayList<String>> savedInfo;
    private UserDatabaseAdapter userDbAdapter;

    public HomeFavouritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_favourites, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUi(view);
    }

    private void initUi(View view) {
        userDbAdapter = new UserDatabaseAdapter(getActivity());
        ImageView homeFavouritesTranslucentBackground;
        TextView homeFavouritesCloseButton;
        TextView homeFavouritesSaveButton;
        TextView homeFavouritesTitle;
        font = Typeface.createFromAsset(getActivity().getAssets(), "Exo2-Regular.ttf");
        fontBold = Typeface.createFromAsset(getActivity().getAssets(), "Exo2-Bold.ttf");
        topText = (TextInputLayout) view.findViewById(R.id.top_text);
        topText.setTypeface(font);
        bottomText = (TextInputLayout) view.findViewById(R.id.bottom_text);
        bottomText.setTypeface(font);
        actv = (AutoCompleteTextView) view.findViewById(R.id.sourceInput);
        actv.setTypeface(font);
        actvd = (AutoCompleteTextView) view.findViewById(R.id.destinationInput);
        actvd.setTypeface(font);
        actv.setThreshold(1);//will start working from first character
        actvd.setThreshold(1);
        sourceDestinationIcon = (ImageView) view.findViewById(R.id.sourceDestinationImage);
        actv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sourceDestinationIcon.setImageResource(R.drawable.source_destination_icon);
                ArrayList<String> autoCompleteSourceTemp = new ArrayList<String>(sourceStoppages);
                if (selected == 1 || selected == 3) {
                    String ch = actvd.getText().toString();
                    if (!ch.equals("") && ch.length() > 0) {
                        if (autoCompleteSourceTemp.contains(ch)) {
                            autoCompleteSourceTemp.remove(ch);
                        }
                    }
                    actv.setAdapter(new CustomArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, autoCompleteSourceTemp));
                }
                return false;
            }
        });

        actvd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sourceDestinationIcon.setImageResource(R.drawable.source_destination_icon_reverse);
                ArrayList<String> autoCompleteDestinationTemp = new ArrayList<String>(destinationStoppages);
                if (selected == 1 || selected == 3) {
                    String ch = actv.getText().toString();
                    if (!ch.equals("") && ch.length() > 0) {
                        if (autoCompleteDestinationTemp.contains(ch)) {
                            autoCompleteDestinationTemp.remove(ch);
                        }
                    }
                    actvd.setAdapter(new CustomArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, autoCompleteDestinationTemp));
                }
                return false;
            }
        });
        homeFavouritesTranslucentBackground = (ImageView) view.findViewById(R.id.homeFavouritesTranslucentBackground);
        AlphaAnimation animation1 = new AlphaAnimation(0.0f, 0.6f);
        animation1.setDuration(1000);
        animation1.setStartOffset(200);
        animation1.setFillAfter(true);
        homeFavouritesTranslucentBackground.startAnimation(animation1);
        try {
            fragment = getActivity().getSupportFragmentManager().findFragmentByTag("FloatingFragment");
        } catch (Exception e) {
            e.printStackTrace();
        }
        homeFavouritesTitle = (TextView) view.findViewById(R.id.homeFavouritesIconText);
        homeFavouritesTitle.setTypeface(fontBold);

        homeFavouritesCloseButton = (TextView) view.findViewById(R.id.homeFavouritesCloseButton);
        homeFavouritesCloseButton.setTypeface(font);
        homeFavouritesCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.slide_out).remove(fragment).commit();
            }
        });

        homeFavouritesSaveButton = (TextView) view.findViewById(R.id.homeFavouritesSetButton);
        homeFavouritesSaveButton.setTypeface(font);
        homeFavouritesSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (!actv.getText().toString().equals("") && !actvd.getText().toString().equals("")
                        && actv.getText().toString().length() > 0 && actvd.getText().toString().length() > 0) {
                    if (sourceStoppages.contains(actv.getText().toString()) && destinationStoppages.contains(actvd.getText().toString())) {
                        long id = userDbAdapter.insertFavourite(ConvertSelected(selected), actv.getText().toString(), actvd.getText().toString());
                        Log.d("SAVED_ID", id + "");
                        if (HomeFragment.favourites) {
                            HomeFragment.favouritesAdapter = new HomeFavouritesListAdapter(getActivity(),
                                    new ArrayList<ArrayList<String>>(userDbAdapter.showFavourites()));
                            HomeFragment.favouritesList.setAdapter(HomeFragment.favouritesAdapter);
                            HomeFragment.homeTut.setVisibility(RelativeLayout.GONE);
                            //HomeFragment.favouritesAdapter.notifyDataSetChanged();
                        }
                    }
                    else {
                        Toast.makeText(getActivity(), "Please choose source and destination stops from the available options!", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getActivity(), "Source or Destination cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        SetFont(view);
        SetButtons(view);
    }

    private String ConvertSelected (int s) {
        if (s == 0) {
            return "BUS";
        }
        else if (s == 1) {
            return "METRO";
        }
        else if (s == 2) {
            return "AUTO";
        }
        else if (s == 3) {
            return "TRAIN";
        }
        else {
            return "TRAM";
        }
    }
    private void SetFont(View view) {
        changeFonts = new ArrayList<TextView>();
        changeFonts.add((TextView) view.findViewById(R.id.homeFavouritesBusText));
        changeFonts.add((TextView) view.findViewById(R.id.homeFavouritesMetroText));
        changeFonts.add((TextView) view.findViewById(R.id.homeFavouritesAutoText));
        changeFonts.add((TextView) view.findViewById(R.id.homeFavouritesTrainText));
        changeFonts.add((TextView) view.findViewById(R.id.homeFavouritesTramText));

        for (int i = 0; i < changeFonts.size(); i++) {
            changeFonts.get(i).setTypeface(font);
        }
    }

    private void resetFilters() {
        for (int b = 0; b < TOTAL_BUTTONS; b++) {
            setTouchListeners.get(b).setColorFilter(Color.parseColor("#1C3144"));
            icons.get(b).setColorFilter(Color.parseColor("#1C3144"));
            changeFonts.get(b).setTypeface(font);
            changeFonts.get(b).setTextColor(Color.parseColor("#1C3144"));
        }
    }

    private void SetButtons(View view) {
        icons = new ArrayList<ImageView>();


        int i = 0;
        setTouchListeners = new ArrayList<ImageView>();
        setTouchListeners.add((ImageView) view.findViewById(R.id.homeFavouritesBusCircle));
        icons.add((ImageView) view.findViewById(R.id.homeFavouritesBusIcon));
        setTouchListeners.add((ImageView) view.findViewById(R.id.homeFavouritesMetroCircle));
        icons.add((ImageView) view.findViewById(R.id.homeFavouritesMetroIcon));
        setTouchListeners.add((ImageView) view.findViewById(R.id.homeFavouritesAutoCircle));
        icons.add((ImageView) view.findViewById(R.id.homeFavouritesAutoIcon));
        setTouchListeners.add((ImageView) view.findViewById(R.id.homeFavouritesTrainCircle));
        icons.add((ImageView) view.findViewById(R.id.homeFavouritesTrainIcon));
        setTouchListeners.add((ImageView) view.findViewById(R.id.homeFavouritesTramCircle));
        icons.add((ImageView) view.findViewById(R.id.homeFavouritesTramIcon));


        setTouchListeners.get(0).setColorFilter(Color.parseColor("#FFFFFF"));
        icons.get(0).setColorFilter(Color.parseColor("#FFFFFF"));
        changeFonts.get(0).setTypeface(fontBold);
        changeFonts.get(0).setTextColor(Color.parseColor("#FFFFFF"));

        SetAutoTextViewAdapter(0);
        removeFocus();

        for (final ImageView rLayout : setTouchListeners) {
            rLayout.setTag(i);
            rLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int tl = (Integer) rLayout.getTag();
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            return true;
                        case MotionEvent.ACTION_UP:
                            resetFilters();
                            setTouchListeners.get(tl).setColorFilter(Color.parseColor("#FFFFFF"));
                            icons.get(tl).setColorFilter(Color.parseColor("#FFFFFF"));
                            changeFonts.get(tl).setTypeface(fontBold);
                            changeFonts.get(tl).setTextColor(Color.parseColor("#FFFFFF"));
                            selected = tl;
                            actv.setText("");
                            actvd.setText("");
                            SetAutoTextViewAdapter(selected);
                            removeFocus();
                            return true;
                        case MotionEvent.ACTION_CANCEL:
                            return true;

                    }
                    return false;
                }
            });
            i++;
        }
    }

    private void SetAutoTextViewAdapter(int s) {
        if (s == 0) {
            sourceStoppages = new ArrayList<String>(BusFragment.AddWordsBusStoppages());
            destinationStoppages = new ArrayList<String>(BusFragment.AddWordsBusStoppages());
            actv.setAdapter(new CustomArrayAdapter
                    (this.getActivity(), android.R.layout.simple_list_item_1,
                            new ArrayList<String>(sourceStoppages)));
            actvd.setAdapter(new CustomArrayAdapter
                    (this.getActivity(), android.R.layout.simple_list_item_1,
                            new ArrayList<String>(destinationStoppages)));
        }
        else if (s == 1) {
            sourceStoppages = new ArrayList<String>(MetroFragment.AddMetroStations());
            destinationStoppages = new ArrayList<String>(MetroFragment.AddMetroStations());
            actv.setAdapter(new CustomArrayAdapter
                    (this.getActivity(), android.R.layout.simple_list_item_1,
                            new ArrayList<String>(sourceStoppages)));
            actvd.setAdapter(new CustomArrayAdapter
                    (this.getActivity(), android.R.layout.simple_list_item_1,
                            new ArrayList<String>(destinationStoppages)));
        }
        else if (s == 2) {
            sourceStoppages = new ArrayList<String>(AutoFragment.AddWordsAuto());
            destinationStoppages = new ArrayList<String>(AutoFragment.AddWordsAuto());
            actv.setAdapter(new CustomArrayAdapter
                    (this.getActivity(), android.R.layout.simple_list_item_1,
                            new ArrayList<String>(sourceStoppages)));
            actvd.setAdapter(new CustomArrayAdapter
                    (this.getActivity(), android.R.layout.simple_list_item_1,
                            new ArrayList<String>(destinationStoppages)));
        }
        else if (s == 3) {
            sourceStoppages = new ArrayList<String>(TrainFragment.AddWordsAutoComplete(new ArrayList<ArrayList<String>>(TrainFragment.AddWordsTrain())));
            destinationStoppages = new ArrayList<String>(TrainFragment.AddWordsAutoComplete(new ArrayList<ArrayList<String>>(TrainFragment.AddWordsTrain())));
            actv.setAdapter(new CustomArrayAdapter
                    (this.getActivity(), android.R.layout.simple_list_item_1,
                            new ArrayList<String>(sourceStoppages)));
            actvd.setAdapter(new CustomArrayAdapter
                    (this.getActivity(), android.R.layout.simple_list_item_1,
                            new ArrayList<String>(destinationStoppages)));
        }
        else {
            sourceStoppages = new ArrayList<String>(TramFragment.AddWordsTram());
            destinationStoppages = new ArrayList<String>(TramFragment.AddWordsTram());
            actv.setAdapter(new CustomArrayAdapter
                    (this.getActivity(), android.R.layout.simple_list_item_1,
                            new ArrayList<String>(sourceStoppages)));
            actvd.setAdapter(new CustomArrayAdapter
                    (this.getActivity(), android.R.layout.simple_list_item_1,
                            new ArrayList<String>(destinationStoppages)));
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

}
