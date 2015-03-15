package com.al.whippersnapper.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.al.whippersnapper.R;
import com.al.whippersnapper.activities.FindTaskActivity;
import com.al.whippersnapper.activities.ShowTaskDetailsActivity;
import com.al.whippersnapper.models.ParseWSUser;
import com.al.whippersnapper.utils.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class FindTaskMapViewFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.InfoWindowAdapter,
        GoogleMap.OnInfoWindowClickListener {

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public final static int MAX_RANGE = 10000; // 10km, TODO: this should depend on the map zoom level

    private HashMap<Marker, ParseWSUser> markerUserMapping;
    private LayoutInflater mInflater;


    private OnFragmentInteractionListener mListener;

    public static FindTaskMapViewFragment newInstance() {
        FindTaskMapViewFragment fragment = new FindTaskMapViewFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FindTaskMapViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }

        markerUserMapping = new HashMap<Marker, ParseWSUser>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_find_task_map_view, container, false);
        mInflater = inflater;
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








    public void setMarkersForNearbyTasks(final LatLng position) {
        ParseQuery<ParseWSUser> q = ParseQuery.getQuery("_User");
        q.whereEqualTo("TaskAvailable", true); // TODO change this and other db strings to constants

        // TODO: horrible hack: We'll download the entire database of tasks, then filter based on distance between the Lat/Lng.
        q.findInBackground(new FindCallback<ParseWSUser>() {
            @Override
            public void done(List<ParseWSUser> parseWSUsers, ParseException e) {
                map.clear(); // clears all overlays, polylines, etc from map too, but that's okay because we don't use them
                markerUserMapping.clear();
                ((FindTaskActivity)getActivity()).getTaskItems().clear();

                // go through all the returned tasks and filter out the far away ones
                // adapted from https://stackoverflow.com/questions/223918/iterating-through-a-list-avoiding-concurrentmodificationexception-when-removing
                for (Iterator<ParseWSUser> it = parseWSUsers.iterator(); it.hasNext();) {
                    ParseWSUser user = it.next();
                    if (distance(position.latitude, (double)user.getTaskLat(), position.longitude, (double)user.getTaskLng()) > MAX_RANGE) {
                        it.remove();
                    }
                }

                // create markers for each task
                for (int i = 0; i < parseWSUsers.size(); i++) {
                    ParseWSUser user = parseWSUsers.get(i);
                    Marker marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng((double) user.getTaskLat(), (double) user.getTaskLng())));
                    markerUserMapping.put(marker, user);

                    // add the user object to the arraylist so that the List View fragment can access it
                    ((FindTaskActivity)getActivity()).getTaskItems().add(user);
                }
                ((FindTaskActivity)getActivity()).getLvAdapter().notifyDataSetChanged();
            }
        });
    }

    // Copied from https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
    public static double distance(double lat1, double lat2, double lon1, double lon2) {
        final int R = 6371; // Radius of the earth

        Double latDistance = deg2rad(lat2 - lat1);
        Double lonDistance = deg2rad(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // convert to meters
    }

    // Copied from https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }


    protected void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mapFragment == null) {
            mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFindTasks));

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


    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Toast.makeText(getActivity(), "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            map.setMyLocationEnabled(true);
            //map.setOnMapLongClickListener(this);
            map.setOnMarkerClickListener(this);
            map.setInfoWindowAdapter(this);
            map.setOnInfoWindowClickListener(this);

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
            Toast.makeText(getActivity(), "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            map.animateCamera(cameraUpdate);
            setMarkersForNearbyTasks(latLng);
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        if (!markerUserMapping.containsKey(marker)) {
            Toast.makeText(getActivity(), "This marker has no task data associated with it.", Toast.LENGTH_LONG).show();
            return null; // some error happened here, this should never happen
        }

        // gather the data for this info window
        ParseWSUser user = markerUserMapping.get(marker);

        // set the text
        View infoWindow = mInflater.inflate(R.layout.infowindow_task, null);
        TextView tvInfoTaskType = (TextView) infoWindow.findViewById(R.id.tvInfoTaskType);
        TextView tvInfoSeniorName = (TextView) infoWindow.findViewById(R.id.tvInfoSeniorName);
        TextView tvInfoTaskDetails = (TextView) infoWindow.findViewById(R.id.tvInfoTaskDetails);

        tvInfoTaskType.setText(markerUserMapping.get(marker).getTaskType());
        tvInfoTaskDetails.setText(markerUserMapping.get(marker).getTaskDetails());
        tvInfoSeniorName.setText(markerUserMapping.get(marker).getFullName());

        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        ParseWSUser user = markerUserMapping.get(marker);

        Intent i = new Intent(getActivity(), ShowTaskDetailsActivity.class);
        i.putExtra("seniorName", Util.getAnonymizedName(user.getFullName())); // TODO - use constants instead of "seniorName"
        i.putExtra("taskType", user.getTaskType());
        i.putExtra("taskDetails", user.getTaskDetails());
        i.putExtra("postedOn", Util.getRelativeTimeAgo(user.getTaskPostedOn().toString())); // TODO - might want to reformat this date, or have a relative "9 minutes ago" string
        startActivity(i);
    }
}
