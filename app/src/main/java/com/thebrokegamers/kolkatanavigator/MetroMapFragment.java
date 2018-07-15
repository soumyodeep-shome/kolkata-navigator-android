package com.thebrokegamers.kolkatanavigator;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 */
public class MetroMapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    private GoogleMap gMap;
    private ImageView metroNumberTranslucentBackground;
    private TextView metroNumberCloseButton;
    private Fragment fragment = null;
    private Bundle bundle;
    private Typeface font;


    public MetroMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_metro_map, container, false);

        mMapView = (MapView) v.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();
        mMapView.getMapAsync(this);
        MapsInitializer.initialize(getContext());


        return v;
    }


    @Override
    public void onMapReady(GoogleMap map) {
        bundle = this.getArguments();
        gMap = map;
        String lat = bundle.getString("MetroLat", "-34");
        String lng = bundle.getString("MetroLong", "151");
        LatLng metroStand = new LatLng(Float.parseFloat(lat), Float.parseFloat(lng));
        gMap.addMarker(new MarkerOptions().snippet(bundle.getString("MetroHeader", "").toUpperCase())
                .position(metroStand).title("Metro Station").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_subway_black_24dp)));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(metroStand, 14.0f));
    }

    /**
     * This is where we can add markers or lines, add listeners or move the
     * camera.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap}
     * is not null.
     */

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUi(view);

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void initUi(View view) {
        font = Typeface.createFromAsset(getActivity().getAssets(), "Exo2-Regular.ttf");
        bundle = this.getArguments();
        TextView metroHeaderText = (TextView) view.findViewById(R.id.metroNumberIconText);
        metroHeaderText.setTypeface(font);
        if (bundle.getString("MetroHeader", "").contains("-")) {
            metroHeaderText.setText(bundle.getString("MetroHeader", "").substring(0,
                    bundle.getString("MetroHeader", "").indexOf("-") - 1));
        } else {
            metroHeaderText.setText(bundle.getString("MetroHeader", ""));
        }
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


    }


}
