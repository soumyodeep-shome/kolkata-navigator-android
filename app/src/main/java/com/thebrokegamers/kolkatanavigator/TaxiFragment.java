package com.thebrokegamers.kolkatanavigator;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class TaxiFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private double latitude, longitude;
    MapView mMapView;
    private GoogleMap gMap;
    private Connectivity mConnectivity;
    private List<Address> addresses;
    private String address, city, state, country, postalCode, knownName, mUpdatedSource;
    private LatLng mLatLng;
    private Location mLastLocation;
    // private LatLng src,dst;
    protected GoogleApiClient mGoogleApiClient, mGoogleApiClientLocation;
    private TextView taxiDistanceText;
    private TextView taxiTimeText;
    private ArrayList<ArrayList<String>> taxis;
    private ArrayAdapter<ArrayList<String>> taxiAdapter;
    private ListView taxiList;
    private boolean mLocation;
    private ImageView img;

    private PlaceAutocompleteAdapter mAdapter;
    String fromAddr = "";
    String toAddr = "";

    private AutoCompleteTextView actv;
    private AutoCompleteTextView actvd;

    private RelativeLayout searchTaxi;
    private int dist;
    private int durtn;
    private static int apiClientId = 0;
    private boolean errorShown;

    private static final LatLngBounds BOUNDS_GREATER_KOL = new LatLngBounds(new LatLng(22.273460, 87.840616), new LatLng(22.880960, 88.697859));

    // EditText et;
    //  Button bt;
    public TaxiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_taxi, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view, savedInstanceState);

    }

    public void initUI(View view, Bundle savedInstanceState) {

        //et=(EditText)view.findViewById(R.id.taxiText);
        //bt=(Button)view.findViewById(R.id.taxiButton);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Exo2-Regular.ttf");
        searchTaxi = (RelativeLayout) view.findViewById(R.id.searchTaxi);

        mMapView = (MapView) view.findViewById(R.id.mapTaxi);
        mMapView.onCreate(savedInstanceState);


        //editor = sharedPref.edit();

        // mMapView.onResume();
        mMapView.getMapAsync(this);
        MapsInitializer.initialize(getContext());

        //---------------------------For Google Autocomplete textview--------------------------------------//
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).enableAutoManage(getActivity(), apiClientId /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();


        //-------------------------------------------For Location Service API--------------------------------------------------//

        if (mGoogleApiClientLocation == null) {
            mGoogleApiClientLocation = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        actv = (AutoCompleteTextView)
                view.findViewById(R.id.sourceInput);
        mLocation = false;

        img = (ImageView) view.findViewById(R.id.sourceDestinationImageTaxi);
        actvd = (AutoCompleteTextView) view.findViewById(R.id.destinationInput);

        // Register a listener that receives callbacks when a suggestion has been selected
        actv.setOnItemClickListener(mAutocompleteSourceClickListener);
        actvd.setOnItemClickListener(mAutocompleteDestinationClickListener);

        // Retrieve the TextViews that will display details and attributions of the selected place.
        //mPlaceDetailsText = (TextView) view.findViewById(R.id.place_details);
        //mPlaceDetailsAttribution = (TextView)view. findViewById(R.id.place_attribution);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient, BOUNDS_GREATER_KOL,
                null);

        actv.setAdapter(mAdapter);
        actv.setTypeface(font);
        actvd.setTypeface(font);
        actvd.setAdapter(mAdapter);

        taxiDistanceText = (TextView) view.findViewById(R.id.taxiDistanceText);
        taxiTimeText = (TextView) view.findViewById(R.id.taxiTimeText);
        taxiDistanceText.setTypeface(font);
        taxiTimeText.setTypeface(font);


        TextView searchText = (TextView) view.findViewById(R.id.searchTaxiText);
        searchText.setTypeface(font);

        TextInputLayout topText = (TextInputLayout) view.findViewById(R.id.top_text);
        topText.setTypeface(font);
        TextInputLayout bottomText = (TextInputLayout) view.findViewById(R.id.bottom_text);
        bottomText.setTypeface(font);


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

        final RelativeLayout rLayout = (RelativeLayout) view.findViewById(R.id.myLocationBackground);

        rLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        rLayout.setBackgroundColor(Color.parseColor("#1E88E5"));
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        rLayout.setBackgroundColor(Color.parseColor("#42A5F5"));
                        if (isNetworkAvailable()) {
                                if (mConnectivity.isConnectedFast(getActivity())){
                                    mUpdatedSource = LatLngToAddr(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                                    if (!mLocation) {
                                        actv.setText(mUpdatedSource);
                                        mLocation = !mLocation;
                                    } else {
                                        actv.setText("");
                                        mLocation = !mLocation;
                                    }
                                }
                                else {
                                    Toast.makeText(getActivity(), "Slow connection detected." ,Toast.LENGTH_SHORT).show();
                                }



                        } else {
                            Toast.makeText(getActivity(), "Check your network connection!", Toast.LENGTH_SHORT).show();
                        }
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_CANCEL:
                        rLayout.setBackgroundColor(Color.parseColor("#42A5F5"));
                        return true; // if you want to handle the touch event

                }
                return false;
            }
        });


        searchTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                taxis.clear();
                taxiTimeText.setText("");
                taxiDistanceText.setText("");
                taxiAdapter.notifyDataSetChanged();

                gMap.clear();

                if (mLocation) {
                    fromAddr = mUpdatedSource;
                }

                if (actv.getText().toString().equals("") || actvd.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Source or destination cannot be empty!", Toast.LENGTH_SHORT).show();
                }
                else {

                    try {
                        new RotaTask(getActivity(), gMap, fromAddr, toAddr).execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        taxis = new ArrayList<ArrayList<String>>();
        taxiAdapter = new TaxiListAdapter(getActivity(), taxis);
        taxiList = (ListView) view.findViewById(R.id.taxiList);
        taxiList.setAdapter(taxiAdapter);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClientLocation);
        if (mLastLocation != null) {
            mLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public class RotaTask extends AsyncTask<Void, Integer, Boolean> {


        private static final String TOAST_MSG = "Calculating";
        private static final String TAG = "TaxiFragment";
        private static final String TOAST_ERR_MAJ = "Impossible to trace Itinerary";

        private Context context;
        private final ArrayList<String> distance = new ArrayList<String>();
        private final ArrayList<String> duration = new ArrayList<String>();
        private GoogleMap gMap;
        private String editFrom;
        private String editTo;
        private final ArrayList<LatLng> lstLatLng = new ArrayList<LatLng>();


        public RotaTask(final Context context, final GoogleMap gMap, final String editFrom, final String editTo) {


            this.context = context;
            this.gMap = gMap;
            this.editFrom = editFrom;
            this.editTo = editTo;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPreExecute() {
            Toast.makeText(context, TOAST_MSG, Toast.LENGTH_SHORT).show();
        }

        /***
         * {@inheritDoc}
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                final StringBuilder url = new StringBuilder("http://maps.googleapis.com/maps/api/directions/xml?sensor=false&language=pt");
                url.append("&origin=");
                url.append(editFrom.replace(' ', '+'));
                url.append("&destination=");
                url.append(editTo.replace(' ', '+'));

                final InputStream stream = new URL(url.toString()).openStream();

                final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                documentBuilderFactory.setIgnoringComments(true);

                final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                final Document document = documentBuilder.parse(stream);
                document.getDocumentElement().normalize();

                final String status = document.getElementsByTagName("status").item(0).getTextContent();
                if (!"OK".equals(status)) {
                    return false;
                }

                final Element elementLeg = (Element) document.getElementsByTagName("leg").item(0);
                final NodeList nodeListStep = elementLeg.getElementsByTagName("step");
                final int length = nodeListStep.getLength();

                for (int i = 0; i < length; i++) {
                    final Node nodeStep = nodeListStep.item(i);
                    Log.d(TAG, "index=" + i + " Node Name: " + nodeStep.getNodeName());

                    if (nodeStep.getNodeType() == Node.ELEMENT_NODE) {
                        final Element elementStep = (Element) nodeStep;
                        decodePolylines(elementStep.getElementsByTagName("points").item(0).getTextContent());
                        calculateDistance(elementStep.getElementsByTagName("distance").item(0).getChildNodes().item(1).getTextContent());
                        calculateDuration(elementStep.getElementsByTagName("duration").item(0).getChildNodes().item(1).getTextContent());
                        Log.d(TAG, " Element Name: " + elementStep.getElementsByTagName("distance").item(0).getTextContent());

                    }
                }

                return true;
            } catch (final Exception e) {
                return false;
            }
        }

        private void calculateDistance(final String x) {
            distance.add(x);
        }

        private void calculateDuration(final String x) {
            duration.add(x);
        }


        private void decodePolylines(final String encodedPoints) {
            int index = 0;
            int lat = 0, lng = 0;

            while (index < encodedPoints.length()) {
                int b, shift = 0, result = 0;

                do {
                    b = encodedPoints.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);

                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;
                shift = 0;
                result = 0;

                do {
                    b = encodedPoints.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);

                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                lstLatLng.add(new LatLng((double) lat / 1E5, (double) lng / 1E5));
            }
        }


        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPostExecute(final Boolean result) {
            if (!result) {
                Toast.makeText(context, TOAST_ERR_MAJ, Toast.LENGTH_SHORT).show();

            } else {
                final PolylineOptions polylines = new PolylineOptions();
                polylines.color(Color.BLUE);

                for (final LatLng latLng : lstLatLng) {
                    polylines.add(latLng);
                }


                // delegate.processFinish(message);

                final MarkerOptions markerA = new MarkerOptions();
                markerA.position(lstLatLng.get(0));
                markerA.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                final MarkerOptions markerB = new MarkerOptions();
                markerB.position(lstLatLng.get(lstLatLng.size() - 1));
                markerB.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lstLatLng.get(0), 10));
                gMap.addMarker(markerA);
                gMap.addPolyline(polylines);
                gMap.addMarker(markerB);

                dist = 0;
                for (int i = 0; i < distance.size(); i++) {
                    dist += Integer.parseInt(distance.get(i));
                }


                durtn = 0;
                for (int i = 0; i < duration.size(); i++) {
                    durtn += Integer.parseInt(duration.get(i));
                }

                int fare = 0;
                String fareText = "";
                ArrayList<String> temp = new ArrayList<String>();
                fare = uberXFareCalculator(dist, durtn);
                if (fare > 95) {
                    fareText = "Rs." + (fare - 15) + " - " + (fare + 20);
                }
                else if (fare > 80) {
                    fareText = "Rs.80" + " - " + (fare + 10);
                }
                else {
                    fareText = "Rs.80";
                }
                temp.add("Uber X");
                temp.add(fareText);

                taxis.add(new ArrayList<String>(temp));

                //Ola Mini
                temp.clear();
                fare = olaMiniFareCalculator(dist, durtn);
                if (fare > 104) {
                    fareText = "Rs." + (fare - 15) + " - " + (fare + 20);
                }
                else if (fare > 99) {
                    fareText = "Rs.99" + " - " + (fare + 10);
                }
                else {
                    fareText = "Rs.99";
                }
                temp.add("Ola Mini");
                temp.add(fareText);

                taxis.add(new ArrayList<String>(temp));

                // Ola Sedan
                temp.clear();
                fare = olaFareCalculator(dist, durtn);
                if (fare > 104) {
                    fareText = "Rs." + (fare - 15) + " - " + (fare + 20);
                }
                else if (fare > 99) {
                    fareText = "Rs.99" + " - " + (fare + 10);
                }
                else {
                    fareText = "Rs.99";
                }
                temp.add("Ola Sedan");
                temp.add(fareText);

                taxis.add(new ArrayList<String>(temp));

                temp.clear();
                fare = tfsHatchbackFareCalculator(dist, durtn);
                if (fare > 60) {
                    fareText = "Rs." + (fare - 15) + " - " + (fare + 20);
                }
                else if (fare > 45) {
                    fareText = "Rs.45" + " - " + (fare + 10);
                }
                else {
                    fareText = "Rs.45";
                }
                temp.add("Taxi For Sure Hatchback");
                temp.add(fareText);

                taxis.add(new ArrayList<String>(temp));

                temp.clear();
                fare = tfsSedanFareCalculator(dist, durtn);
                if (fare > 90) {
                    fareText = "Rs." + (fare - 15) + " - " + (fare + 20);
                }
                else if (fare > 75) {
                    fareText = "Rs.75" + " - " + (fare + 10);
                }
                else {
                    fareText = "Rs.75";
                }
                temp.add("Taxi For Sure Sedan");
                temp.add(fareText);

                taxis.add(new ArrayList<String>(temp));

                taxiAdapter.notifyDataSetChanged();
                float dis = dist / 100;
                taxiTimeText.setText("Approximate Time : " + durtn / 60 + " Minutes");
                taxiDistanceText.setText("Approximate Distance : " + dis / 10.0f + " km");

            }
        }
    }


    public int uberXFareCalculator(int dist, int durtn) {
        int fare = 0;
        double mins = 0, km = 0;

        mins = durtn / 60;
        km = dist / 1000;

        double cost = 40 + mins + km * 9;

        fare = (int) Math.round(cost);

        if (fare > 80) {
            return fare;
        } else {
            return 80;
        }

    }

    public int olaMiniFareCalculator(int dist, int durtn) {
        int fare = 0;
        double mins = 0, km = 0;

        mins = durtn / 60;
        km = dist / 1000;


        double cost = 99 + (mins - 10) + (km - 4) * 7;

        fare = (int) Math.round(cost);

        if (fare > 99) {
            return fare;
        } else {
            return 99;
        }

    }

    public int olaFareCalculator(int dist, int durtn) {
        int fare = 0;
        double mins = 0, km = 0;

        mins = durtn / 60;
        km = dist / 1000;


        double cost = 99 + (mins - 10) + (km - 4) * 10;

        fare = (int) Math.round(cost);

        if (fare > 99) {
            return fare;
        } else {
            return 99;
        }

    }


    public int tfsHatchbackFareCalculator(int dist, int durtn) {
        int fare = 0;
        double mins = 0, km = 0;

        mins = durtn / 60;
        km = dist / 1000;

        double cost = 35 + (mins * 1.25) + km * 8;

        fare = (int) Math.round(cost);

        if (fare > 45) {
            return fare;
        } else {
            return 45;
        }

    }

    public int tfsSedanFareCalculator(int dist, int durtn) {
        int fare = 0;
        double mins = 0, km = 0;

        mins = durtn / 60;
        km = dist / 1000;

        double cost = 40 + (mins * 1.25) + km * 8;

        fare = (int) Math.round(cost);

        if (fare > 75) {
            return fare;
        } else {
            return 75;
        }

    }

    public void CoordGPS(String address, Context context) {
        Geocoder gc = new Geocoder(context, Locale.ENGLISH);
        List<Address> addresses;
        try {
            addresses = gc.getFromLocationName(address, 1);
            if (addresses.size() > 0) {
                this.latitude = addresses.get(0).getLatitude();
                this.longitude = addresses.get(0).getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String LatLngToAddr(double latitude, double longitude) {
        Geocoder geocoder;

        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }


        address = addresses.get(0).getAddressLine(0) + addresses.get(0).getAddressLine(1); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        city = addresses.get(0).getLocality();
        state = addresses.get(0).getAdminArea();
        country = addresses.get(0).getCountryName();
        postalCode = addresses.get(0).getPostalCode();
        knownName = addresses.get(0).getFeatureName();
        return address + ", " + city + ", " + state + ", " + country;
    }

    private AdapterView.OnItemClickListener mAutocompleteSourceClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            // Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdateSourceDetailsCallback);

            //Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    private AdapterView.OnItemClickListener mAutocompleteDestinationClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            // Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdateDestinationDetailsCallback);

            //Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    @Override
    public void onMapReady(GoogleMap map) {
        gMap = map;
        LatLng kolkata = new LatLng(22.572646, 88.363895);
        //Location myLoc=gMap.getMyLocation();

        //gMap.addMarker(new MarkerOptions().position(kolkata).title("Marker in Kolkata"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom((kolkata), 13.0f));

    }

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdateSourceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                // Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // Format details of the place for display and show it in a TextView.
            //mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
            //place.getId(), place.getAddress(), place.getPhoneNumber(),
            //place.getWebsiteUri()));
            //CoordGPS(place.getAddress().toString(), getActivity());
            fromAddr = place.getAddress().toString();
            //src=place.getLatLng();
            //gMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("Source Marker").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
            gMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
                //mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                //mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                //mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }

            //Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };


    private ResultCallback<PlaceBuffer> mUpdateDestinationDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                // Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            toAddr = place.getAddress().toString();
            gMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
                // mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                //mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                //mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }

            //Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        if (!errorShown) {
            Toast.makeText(getActivity(),
                    "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                    Toast.LENGTH_SHORT).show();
            errorShown = true;
        }

        /*final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                errorShown = false;
                handler.postDelayed(this, 10000);
            }
        };
        handler.postDelayed(r, 10000);*/

    }


    public void onStart() {
        mGoogleApiClientLocation.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClientLocation.disconnect();
        super.onStop();
    }


    @Override
    public void onResume() {
        apiClientId++;
        Log.d("Balchal--->", String.valueOf(apiClientId));
        super.onResume();
        mMapView.onResume();
        mGoogleApiClient.connect();

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
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


}
