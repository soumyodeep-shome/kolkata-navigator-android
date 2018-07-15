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
public class AutoMapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    private GoogleMap gMap;
    private ImageView autoNumberTranslucentBackground;
    private TextView autoNumberCloseButton;
    private Fragment fragment = null;
    private Bundle bundle;
    private Typeface font;


    public AutoMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_auto_map, container, false);

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
        String lat = bundle.getString("AutoLat", "-34");
        String lng = bundle.getString("AutoLong", "151");
        LatLng autoStand = new LatLng(Float.parseFloat(lat), Float.parseFloat(lng));
        gMap.addMarker(new MarkerOptions().snippet(bundle.getString("AutoHeader", "").toUpperCase())
                .position(autoStand).title("Auto Stand").icon(BitmapDescriptorFactory.fromResource(R.drawable.auto_icon)));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(autoStand, 14.0f));
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
        TextView autoHeaderText = (TextView) view.findViewById(R.id.autoNumberIconText);
        autoHeaderText.setTypeface(font);
        autoHeaderText.setText(bundle.getString("AutoHeader", "").toUpperCase());
        autoNumberTranslucentBackground = (ImageView) view.findViewById(R.id.autoNumberTranslucentBackground);

        AlphaAnimation animation1 = new AlphaAnimation(0.0f, 0.6f);
        animation1.setDuration(1000);
        animation1.setStartOffset(200);
        animation1.setFillAfter(true);
        autoNumberTranslucentBackground.startAnimation(animation1);
        try {
            fragment = getActivity().getSupportFragmentManager().findFragmentByTag("FloatingFragment");
        } catch (Exception e) {
            e.printStackTrace();
        }
        autoNumberCloseButton = (TextView) view.findViewById(R.id.autoNumberCloseButton);
        autoNumberCloseButton.setTypeface(font);
        autoNumberCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.slide_out).remove(fragment).commit();
            }
        });


    }


}
