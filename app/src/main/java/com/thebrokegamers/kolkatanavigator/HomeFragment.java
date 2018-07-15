package com.thebrokegamers.kolkatanavigator;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private UserDatabaseAdapter userDbAdapter;
    public static ArrayAdapter<ArrayList<String>> favouritesAdapter;
    public static ListView favouritesList;
    public static final int Delete = Menu.FIRST + 1;
    public static final int Add = Menu.FIRST + 2;
    private int pos;
    public static boolean favourites;
    private RelativeLayout favouritesButton;
    private RelativeLayout recentButton;
    private TextView favouritesButtonText;
    private TextView recentButtonText;
    public static RelativeLayout homeTut;
    private TextView homeTutText;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, Delete, Menu.NONE, "DELETE");
        menu.add(Menu.NONE, Add, Menu.NONE, "ADD TO FAVOURITES");
        if (favourites) {
            menu.findItem(Add).setVisible(false);
        } else {
            menu.findItem(Add).setVisible(true);
        }

        for (int i = 0; i < menu.size(); i++) {
            MenuItem mi = menu.getItem(i);
            applyFontToMenuItem(mi);
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Exo2-Regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return (applyMenuChoice(item) || super.onContextItemSelected(item));
    }


    private boolean applyMenuChoice(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case Delete:
                Log.d("DAFEK", favouritesAdapter.getItem(pos).get(2));
                String tutText;
                if (favourites) {
                    userDbAdapter.deleteFavourite(userDbAdapter.showFavourites().get(pos).get(0));
                    favouritesAdapter = new HomeFavouritesListAdapter(getActivity(),
                            new ArrayList<ArrayList<String>>(userDbAdapter.showFavourites()));
                    tutText = "Click the floating button to add favourites.";
                } else {
                    userDbAdapter.deleteRecent(userDbAdapter.showRecents().get(pos).get(0));
                    favouritesAdapter = new HomeRecentsListAdapter(getActivity(),
                            new ArrayList<ArrayList<String>>(userDbAdapter.showRecents()));
                    tutText = "Search History.";
                }
                favouritesList.setAdapter(favouritesAdapter);

                if (favouritesAdapter.getCount() == 0) {
                    homeTutText.setText(tutText);
                    homeTut.setVisibility(RelativeLayout.VISIBLE);
                }
                Toast.makeText(getActivity(), "Deleted!", Toast.LENGTH_SHORT).show();
                return true;
            case Add:
                ArrayList<String> m = new ArrayList<String>(userDbAdapter.showRecents().get(pos));
                userDbAdapter.insertFavourite(m.get(1), m.get(2), m.get(3));
                return true;
        }
        return false;
    }

    private void initUI(View view) {
        favourites = true;
        SetFont(view);
        SetButtons(view);
        userDbAdapter = new UserDatabaseAdapter(getActivity());
        SetFavouritesListView(view);
        favouritesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> t;
                if (favourites) {
                    t = new ArrayList<String>(userDbAdapter.showFavourites().get(position));
                } else {
                    t = new ArrayList<String>(userDbAdapter.showRecents().get(position));
                }

                Bundle bundle = new Bundle();
                bundle.putString("source", t.get(2));
                bundle.putString("destination", t.get(3));
                OpenFragment(t.get(1), bundle);
            }
        });
        favouritesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                return false;
            }
        });
        SetCapsuleButtons(view);
    }

    private void SetFavouritesListView(View view) {
        //favouritesInfo = new ArrayList<ArrayList<String>>(userDbAdapter.showFavourites());
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Exo2-Regular.ttf");
        homeTut = (RelativeLayout) view.findViewById(R.id.homeTutorial);
        homeTutText = (TextView) view.findViewById(R.id.homeTutorialText);
        homeTutText.setTypeface(font);
        favouritesAdapter = new HomeFavouritesListAdapter(getActivity(),
                new ArrayList<ArrayList<String>>(userDbAdapter.showFavourites()));
        favouritesList = (ListView) view.findViewById(R.id.homeList);
        favouritesList.setAdapter(favouritesAdapter);
        registerForContextMenu(favouritesList);
        if (favouritesAdapter.getCount() > 0) {
            homeTut.setVisibility(RelativeLayout.GONE);
        }
    }

    private void SetCapsuleButtons(View view) {
        favouritesButton = (RelativeLayout) view.findViewById(R.id.homeButtonLeft);
        recentButton = (RelativeLayout) view.findViewById(R.id.homeButtonRight);
        favouritesButtonText = (TextView) view.findViewById(R.id.homeButtonLeftText);
        recentButtonText = (TextView) view.findViewById(R.id.homeButtonRightText);
        favouritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!favourites) {
                    favourites = true;
                    favouritesButton.setBackgroundResource(R.drawable.capsule_small_left_button);
                    favouritesButtonText.setTextColor(Color.parseColor("#1C3144"));
                    recentButton.setBackgroundResource(R.drawable.capsule_small_right_button_deact);
                    recentButtonText.setTextColor(Color.parseColor("#FFFFFF"));
                    favouritesAdapter = new HomeFavouritesListAdapter(getActivity(),
                            new ArrayList<ArrayList<String>>(userDbAdapter.showFavourites()));
                    favouritesList.setAdapter(favouritesAdapter);
                    if (favouritesAdapter.getCount() > 0) {
                        homeTut.setVisibility(RelativeLayout.GONE);
                    } else {
                        homeTutText.setText("Click the floating button to add favourites.");
                        homeTut.setVisibility(RelativeLayout.VISIBLE);
                    }
                }
            }
        });
        recentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favourites) {
                    favourites = false;
                    favouritesButton.setBackgroundResource(R.drawable.capsule_small_left_button_deact);
                    favouritesButtonText.setTextColor(Color.parseColor("#FFFFFF"));
                    recentButton.setBackgroundResource(R.drawable.capsule_small_right_button);
                    recentButtonText.setTextColor(Color.parseColor("#1C3144"));
                    favouritesAdapter = new HomeRecentsListAdapter(getActivity(),
                            new ArrayList<ArrayList<String>>(userDbAdapter.showRecents()));
                    favouritesList.setAdapter(favouritesAdapter);
                    if (favouritesAdapter.getCount() > 0) {
                        homeTut.setVisibility(RelativeLayout.GONE);
                    } else {
                        homeTutText.setText("Search history.");
                        homeTut.setVisibility(RelativeLayout.VISIBLE);
                    }
                }
            }
        });
    }


    private void SetButtons(View view) {
        final ArrayList<RelativeLayout> setTouchListeners = new ArrayList<RelativeLayout>();
        setTouchListeners.add((RelativeLayout) view.findViewById(R.id.homeBusContainer));
        setTouchListeners.add((RelativeLayout) view.findViewById(R.id.homeMetroContainer));
        setTouchListeners.add((RelativeLayout) view.findViewById(R.id.homeAutoContainer));
        setTouchListeners.add((RelativeLayout) view.findViewById(R.id.homeTrainContainer));
        setTouchListeners.add((RelativeLayout) view.findViewById(R.id.homeTramContainer));
        setTouchListeners.add((RelativeLayout) view.findViewById(R.id.homeTaxiContainer));
        setTouchListeners.add((RelativeLayout) view.findViewById(R.id.homeFerryContainer));
        setTouchListeners.add((RelativeLayout) view.findViewById(R.id.homeEmergencyContainer));
        for (final RelativeLayout rLayout : setTouchListeners) {
            rLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            rLayout.setBackgroundColor(Color.parseColor("#1E88E5"));
                            return true; // if you want to handle the touch event
                        case MotionEvent.ACTION_UP:
                            rLayout.setBackgroundColor(Color.parseColor("#42A5F5"));
                            OpenFragment(rLayout.getId());
                            return true; // if you want to handle the touch event
                        case MotionEvent.ACTION_CANCEL:
                            rLayout.setBackgroundColor(Color.parseColor("#42A5F5"));
                            return true; // if you want to handle the touch event

                    }
                    return false;
                }
            });
        }

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                Class fragmentClass = HomeFavouritesFragment.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations
                        (R.anim.slide_in, R.anim.slide_out).add(R.id.flContent, fragment, "FloatingFragment").commit();
            }
        });
    }

    private void SetFont(View view) {
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Exo2-Regular.ttf");
        ArrayList<TextView> changeFonts = new ArrayList<TextView>();
        changeFonts.add((TextView) view.findViewById(R.id.homeBusText));
        changeFonts.add((TextView) view.findViewById(R.id.homeMetroText));
        changeFonts.add((TextView) view.findViewById(R.id.homeAutoText));
        changeFonts.add((TextView) view.findViewById(R.id.homeTrainText));
        changeFonts.add((TextView) view.findViewById(R.id.homeTramText));
        changeFonts.add((TextView) view.findViewById(R.id.homeTaxiText));
        changeFonts.add((TextView) view.findViewById(R.id.homeFerryText));
        changeFonts.add((TextView) view.findViewById(R.id.homeEmergencyText));
        changeFonts.add((TextView) view.findViewById(R.id.homeButtonRightText));
        changeFonts.add((TextView) view.findViewById(R.id.homeButtonLeftText));

        for (int i = 0; i < changeFonts.size(); i++) {
            changeFonts.get(i).setTypeface(font);
        }
    }

    private void OpenFragment(int id) {
        Class fragmentClass = null;
        Fragment fragment = null;
        String fragmentTag = "";
        if (id == R.id.homeBusContainer) {
            fragmentTag = "BusFragment";
            fragmentClass = BusFragment.class;
            MainActivity.navigationView.setCheckedItem(R.id.nav_bus);
            MainActivity.toolbarText.setText("Bus");
        } else if (id == R.id.homeMetroContainer) {
            fragmentTag = "MetroFragment";
            fragmentClass = MetroFragment.class;
            MainActivity.navigationView.setCheckedItem(R.id.nav_metro);
            MainActivity.toolbarText.setText("Metro");
        } else if (id == R.id.homeAutoContainer) {
            fragmentTag = "AutoFragment";
            fragmentClass = AutoFragment.class;
            MainActivity.navigationView.setCheckedItem(R.id.nav_auto);
            MainActivity.toolbarText.setText("Auto");
        } else if (id == R.id.homeTrainContainer) {
            fragmentTag = "TrainFragment";
            fragmentClass = TrainFragment.class;
            MainActivity.navigationView.setCheckedItem(R.id.nav_train);
            MainActivity.toolbarText.setText("Train");
        } else if (id == R.id.homeTramContainer) {
            fragmentTag = "TramFragment";
            fragmentClass = TramFragment.class;
            MainActivity.navigationView.setCheckedItem(R.id.nav_tram);
            MainActivity.toolbarText.setText("Tram");

        } else if (id == R.id.homeTaxiContainer) {
            fragmentTag = "TaxiFragment";
            fragmentClass = TaxiFragment.class;
            MainActivity.navigationView.setCheckedItem(R.id.nav_taxi);
            MainActivity.toolbarText.setText("Taxi");

        } else if (id == R.id.homeFerryContainer) {
            fragmentTag = "FerryFragment";
            fragmentClass = FerryFragment.class;
            MainActivity.navigationView.setCheckedItem(R.id.nav_ferry);
            MainActivity.toolbarText.setText("Ferry");

        } else if (id == R.id.homeEmergencyContainer) {
            fragmentTag = "EmergencyFragment";
            fragmentClass = EmergencyFragment.class;
            MainActivity.navigationView.setCheckedItem(R.id.nav_emergency);
            MainActivity.toolbarText.setText("Emergency");
        }
        if (fragmentClass != null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (fragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out).replace(R.id.flContent, fragment, fragmentTag).
                    addToBackStack(null).commit();
        }
    }

    private void OpenFragment(String name, Bundle bundle) {
        Class fragmentClass = null;
        Fragment fragment = null;
        String fragmentTag = "";
        if (name.equals("BUS")) {
            fragmentTag = "BusFragment";
            fragmentClass = BusFragment.class;
            MainActivity.navigationView.setCheckedItem(R.id.nav_bus);
            MainActivity.toolbarText.setText("Bus");
        } else if (name.equals("METRO")) {
            fragmentTag = "MetroFragment";
            fragmentClass = MetroFragment.class;
            MainActivity.navigationView.setCheckedItem(R.id.nav_metro);
            MainActivity.toolbarText.setText("Metro");
        } else if (name.equals("AUTO")) {
            fragmentTag = "AutoFragment";
            fragmentClass = AutoFragment.class;
            MainActivity.navigationView.setCheckedItem(R.id.nav_auto);
            MainActivity.toolbarText.setText("Auto");

        } else if (name.equals("TRAIN")) {
            fragmentTag = "TrainFragment";
            fragmentClass = TrainFragment.class;
            MainActivity.navigationView.setCheckedItem(R.id.nav_train);
            MainActivity.toolbarText.setText("Train");
        } else if (name.equals("TRAM")) {
            fragmentTag = "TramFragment";
            fragmentClass = TramFragment.class;
            MainActivity.navigationView.setCheckedItem(R.id.nav_tram);
            MainActivity.toolbarText.setText("Tram");
        }
        if (fragmentClass != null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                fragment.setArguments(bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (fragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out).replace(R.id.flContent, fragment, fragmentTag).
                    addToBackStack(null).commit();
        }
    }
}
