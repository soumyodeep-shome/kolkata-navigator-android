package com.thebrokegamers.kolkatanavigator;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

//chmod 777 /data /data/data /data/data/com.thebrokegamers.kolkatanavigator /data/data/com.thebrokegamers.kolkatanavigator/*

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TestAdapter dbAdapter;
    private UserDatabaseAdapter userDbAdapter;
    public static TextView toolbarText;
    private Typeface font;
    private TextView kolkataNavigatorText;
    public static NavigationView navigationView;
    private int checkItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        kolkataNavigatorText = (TextView) findViewById(R.id.kolkataNavigatorText);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarText = (TextView) findViewById(R.id.toolbarText);
        font = Typeface.createFromAsset(getAssets(), "Exo2-Bold.ttf");
        kolkataNavigatorText.setTypeface(Typeface.createFromAsset(getAssets(), "Exo2-Regular.ttf"));
        toolbarText.setTypeface(font);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        toolbarText.setText("Kolkata Navigator");
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Menu m = navigationView.getMenu();
                for (int i = 0; i < m.size(); i++) {
                    MenuItem mi = m.getItem(i);
                    if (mi.isChecked()) {
                        checkItemId = mi.getItemId();
                        break;
                    }
                }

            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        Menu m = navigationView.getMenu();

        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            applyFontToMenuItem(mi);
        }


        dbAdapter = new TestAdapter(this);
        dbAdapter.createDatabase();
        dbAdapter.open();

        dbAdapter.getTestData();
        dbAdapter.close();

        userDbAdapter = new UserDatabaseAdapter(this);

        Fragment fragment = null;
        Class fragmentClass = HomeFragment.class;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out).replace(R.id.flContent, fragment, "HomeFragment").commit();
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "Exo2-Regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }


    @Override
    public void onBackPressed() {
        Fragment fragment = null;
        fragment = getSupportFragmentManager().findFragmentByTag("FloatingFragment");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START) || fragment != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.slide_out).remove(fragment).commit();
            }
        } else {
            if (getVisibleFragment().equals("Kolkata Navigator")) {
                moveTaskToBack(true);
            }
            else {
                super.onBackPressed();
                toolbarText.setText(getVisibleFragment());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home_shortcut) {


            Fragment fragment = null;
            Fragment floatingFragment = getSupportFragmentManager().findFragmentByTag("FloatingFragment");

            if (!toolbarText.getText().toString().equals("Kolkata Navigator")) {
                Class fragmentClass = HomeFragment.class;
                String fragmentTag = "HomeFragment";
                toolbarText.setText("Kolkata Navigator");
                navigationView.setCheckedItem(R.id.nav_home);

                if (floatingFragment != null) {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.slide_out).remove(floatingFragment).commit();
                }

                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out).replace(R.id.flContent, fragment, fragmentTag).addToBackStack(null).commit();
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment fragment = null;
        Class fragmentClass = null;

        Fragment floatingFragment = getSupportFragmentManager().findFragmentByTag("FloatingFragment");

        if (floatingFragment != null) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.slide_out).remove(floatingFragment).commit();
        }

        String fragmentTag = "";

        int id = item.getItemId();

        if (id != checkItemId) {
            if (id == R.id.nav_home) {
                fragmentClass = HomeFragment.class;
                fragmentTag = "HomeFragment";
                toolbarText.setText("Kolkata Navigator");

            } else if (id == R.id.nav_bus) {
                fragmentClass = BusFragment.class;
                fragmentTag = "BusFragment";
                toolbarText.setText("Bus");
            } else if (id == R.id.nav_metro) {
                fragmentClass = MetroFragment.class;
                fragmentTag = "MetroFragment";
                toolbarText.setText("Metro");
            } else if (id == R.id.nav_auto) {
                fragmentClass = AutoFragment.class;
                fragmentTag = "AutoFragment";
                toolbarText.setText("Auto");
            } else if (id == R.id.nav_train) {
                fragmentClass = TrainFragment.class;
                fragmentTag = "TrainFragment";
                toolbarText.setText("Train");
            } else if (id == R.id.nav_tram) {
                fragmentClass = TramFragment.class;
                fragmentTag = "TramFragment";
                toolbarText.setText("Tram");
            } else if (id == R.id.nav_taxi) {
                toolbarText.setText("Taxi");
                fragmentClass = TaxiFragment.class;
                fragmentTag = "TaxiFragment";
                toolbarText.setText("Taxi");
            } else if (id == R.id.nav_ferry) {
                fragmentClass = FerryFragment.class;
                fragmentTag = "FerryFragment";
                toolbarText.setText("Ferry");
            } else if (id == R.id.nav_emergency) {
                fragmentClass = EmergencyFragment.class;
                fragmentTag = "EmergencyFragment";
                toolbarText.setText("Emergency");
            } else if (id == R.id.nav_share) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, "I'm being sent!!");
                startActivity(Intent.createChooser(share, "Share Text"));

            } else if (id == R.id.nav_rate) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.TheBrokeGamers.KNav"));
                startActivity(browserIntent);
            } else if (id == R.id.nav_like) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Kolkata-Navigator-925780400776597/"));
                startActivity(browserIntent);
            }


            if (fragmentClass != null) {
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out).replace(R.id.flContent, fragment, fragmentTag).addToBackStack(null).commit();
            }
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        return true;
    }

    private String getVisibleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ArrayList<Fragment> fragments = new ArrayList<Fragment>(fragmentManager.getFragments());
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible()) {
                if (fragment.getTag().equals("HomeFragment")) {
                    navigationView.setCheckedItem(R.id.nav_home);
                    return "Kolkata Navigator";
                } else if (fragment.getTag().equals("BusFragment")) {
                    navigationView.setCheckedItem(R.id.nav_bus);
                    return "Bus";
                } else if (fragment.getTag().equals("MetroFragment")) {
                    navigationView.setCheckedItem(R.id.nav_metro);
                    return "Metro";
                } else if (fragment.getTag().equals("AutoFragment")) {
                    navigationView.setCheckedItem(R.id.nav_auto);
                    return "Auto";
                } else if (fragment.getTag().equals("TrainFragment")) {
                    navigationView.setCheckedItem(R.id.nav_train);
                    return "Train";
                } else if (fragment.getTag().equals("FerryFragment")) {
                    navigationView.setCheckedItem(R.id.nav_ferry);
                    return "Ferry";
                } else if (fragment.getTag().equals("EmergencyFragment")) {
                    navigationView.setCheckedItem(R.id.nav_emergency);
                    return "Emergency";
                }
            }
        }
        return "";
    }
}
