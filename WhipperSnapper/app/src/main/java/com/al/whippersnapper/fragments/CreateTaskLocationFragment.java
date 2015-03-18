package com.al.whippersnapper.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.al.whippersnapper.R;
import com.al.whippersnapper.activities.CreateTaskActivity;
import com.al.whippersnapper.activities.MainActivity;
import com.al.whippersnapper.models.ParseWSUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;

public class CreateTaskLocationFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMapLongClickListener {


    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    public LatLng getMarkerLocationOnMap() {
        return markerLocationOnMap;
    }

    public RadioButton getRbMyHomeAddress() {
        return rbMyHomeAddress;
    }

    public RadioButton getRbMapLocation() {
        return rbMapLocation;
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }


    private LatLng markerLocationOnMap = null; // starts off null until first placement

    /*
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private RadioButton rbMyHomeAddress;
    private RadioButton rbMapLocation;
    private Button btnDone_FromMap;
    private ProgressBar pbInLocationFrag;



    private onDoneFromMapClickListener doneFromMapClickListener;
    private OnFragmentInteractionListener mListener;



    public interface onDoneFromMapClickListener {
        public void onDoneFromMapClick();
    }

    public static CreateTaskLocationFragment newInstance() {
        CreateTaskLocationFragment fragment = new CreateTaskLocationFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CreateTaskLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    protected void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mapFragment == null) {
            mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));

        // Check if we were successful in obtaining the map.
            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        map.setMyLocationEnabled(true);
                        loadMap(map);
                    }
                });
            }
        }
    }

    public ProgressBar getPbInLocationFrag() {
        return pbInLocationFrag;
    }

    public Button getBtnDone_FromMap() {
        return btnDone_FromMap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_task_location, container, false);

        // get the current user from parse
        ParseWSUser thisUser = new ParseWSUser();
        //thisUser.logIn(getThisDevicePhoneNumber(), "password"); // TODO: this assume the user is still logged on, test this assumption
        thisUser = (ParseWSUser) ParseWSUser.getCurrentUser();

        // add actual address to the radio button text
        rbMyHomeAddress = (RadioButton) v.findViewById(R.id.rbMyHomeAddress);
        rbMyHomeAddress.setText(getResources().getString(R.string.My_home_address) + thisUser.getAddress());

        rbMapLocation = (RadioButton) v.findViewById(R.id.rbMapLocation);

        // set up click listener for All Done button.
        btnDone_FromMap = (Button) v.findViewById(R.id.btnDone_FromMap);
        btnDone_FromMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneFromMapClickListener.onDoneFromMapClick();
            }
        });

        pbInLocationFrag = (ProgressBar) v.findViewById(R.id.pbInDetailsFrag);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        if (activity instanceof CreateTaskActivity) {
            doneFromMapClickListener = (CreateTaskActivity) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
















    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Toast.makeText(getActivity(), "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            map.setMyLocationEnabled(true);
            map.setOnMapLongClickListener(this);

            // Now that map has loaded, let's get our location!
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();

            connectClient();
        } else {
            Toast.makeText(getActivity(), "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        // TODO - should probably use an overlay on the map instead of long pressing to set spot.
        Toast.makeText(getActivity().getApplicationContext(), "Long Press", Toast.LENGTH_LONG).show();

        rbMapLocation.setChecked(true); // setting map marker checks this radio button
        map.clear(); // clears all overlays, polylines, etc from map too, but that's okay because we don't use them
        map.addMarker(new MarkerOptions()
                        .position(point)
                        .draggable(true));
        markerLocationOnMap = point;
    }



    public void onMove(View v) {
        LatLng latLng = new LatLng(49, -122);
        CameraUpdate camUpdate = CameraUpdateFactory.newLatLng(latLng);
        map.animateCamera(camUpdate);
    }

    protected void connectClient() {
        // Connect the client.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        setUpMapIfNeeded();
        connectClient();
    }

    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                //ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                //errorFragment.setDialog(errorDialog);
                //errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }

            return false;
        }
    }


    /*
 * Called by Location Services if the connection to the location client
 * drops because of an error.
 */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(getActivity(), "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(getActivity(), "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //int lat = (int) location.getLatitude();
        //int lon = (int) location.getLongitude();
        //latitudeField.setText(String.valueOf(lat));
        //longitudeField.setText(String.valueOf(lon));
    }

    /*
     * Called by Location Services when the request to connect the client
     * finishes successfully. At this point, you can request the current
     * location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            //Toast.makeText(getActivity(), "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            map.animateCamera(cameraUpdate);
            startLocationUpdates();
        } else {
            Toast.makeText(getActivity(), "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);

    }

    // TODO used for debugging, and apparently I have to copy/paste this function into every
    // Activity class I want to use it from since getApplicationContext() can't be called from
    // a static method.
    public String getThisDevicePhoneNumber() {
        if (MainActivity.DEBUG_USE_REAL_PHONE_NUMBER) {
            TelephonyManager tMgr = (TelephonyManager) getActivity().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            return tMgr.getLine1Number();
        } else {
            return MainActivity.DEBUG_FAKE_PHONE_NUMBER;
        }
    }
}
