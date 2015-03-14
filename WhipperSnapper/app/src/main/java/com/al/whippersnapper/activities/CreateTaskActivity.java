package com.al.whippersnapper.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.al.whippersnapper.R;
import com.al.whippersnapper.adapters.CreateTaskFragmentPagerAdapter;
import com.al.whippersnapper.fragments.CreateTaskDetailsFragment;
import com.al.whippersnapper.fragments.CreateTaskLocationFragment;
import com.al.whippersnapper.models.ParseTask;
import com.al.whippersnapper.models.ParseWSUser;
import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

public class CreateTaskActivity extends FragmentActivity implements
        CreateTaskDetailsFragment.OnFragmentInteractionListener,
        CreateTaskLocationFragment.OnFragmentInteractionListener,
        CreateTaskDetailsFragment.onTaskPhotoClickListener,
        CreateTaskDetailsFragment.onDoneFromDetailsClickListener,
        CreateTaskLocationFragment.onDoneFromMapClickListener {

    private byte[] photoBytes;
    private Uri mCapturedImageURI;
    private CreateTaskFragmentPagerAdapter pagerAdapter;

    public static final int TAKE_TASK_PHOTO = 2000;
    public static final int TASK_PHOTO_MAX_WIDTH = 500;
    public static final int TASK_PHOTO_MAX_HEIGHT = 500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        pagerAdapter = new CreateTaskFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabsStrip.setViewPager(viewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void onTaskPhotoClick() {
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, TAKE_TASK_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap photo = null;
        if (requestCode == TAKE_TASK_PHOTO && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
        }

        ImageView ivTaskPhoto = pagerAdapter.getTaskDetailsFragment().getIvTaskPhoto(); // Did I mention that I don't like Java?
        Log.e("XXXXXXXXXX", "Original size: " + photo.getWidth() + " " + photo.getHeight());
        if (photo != null) {
            // Scale the photo
            Matrix m = new Matrix();
            m.setRectToRect(new RectF(0, 0, photo.getWidth(), photo.getHeight()), new RectF(0, 0, TASK_PHOTO_MAX_WIDTH, TASK_PHOTO_MAX_HEIGHT), Matrix.ScaleToFit.CENTER);
            photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), m, true);

            //Log.e("XXXXXXXXXX", "Scaled size: " + photo.getWidth() + " " + photo.getHeight());

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            photoBytes = stream.toByteArray();
            ivTaskPhoto.setImageBitmap(BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length));
        }
    }


    public void onDoneFromMapClick() {
        onDone();
    }

    public void onDoneFromDetailsClick() {
        onDone();
    }

    public void onDone() {
        // TODO: add progress bars

        ParseWSUser theUser = (ParseWSUser) ParseWSUser.getCurrentUser();

        // Create the parse task
        ParseTask theTask = new ParseTask();
        theTask.setSeniorUsername(theUser.getUsername());
        theTask.setAvailable(true);
        theTask.setDetails(pagerAdapter.getTaskDetailsFragment().getEtTaskDetails().getText().toString()); // Have I mentioned that I don't care for Java?
        theTask.setTaskType(pagerAdapter.getTaskDetailsFragment().getSpTaskType().getSelectedItem().toString());

        // figure out the final lat lng
        if (pagerAdapter.getTaskLocationFragment().getRbMyHomeAddress().isChecked()) {
            // use the user's home address
            setAddressIfNoLatLng(theUser, theTask);
        } else {
            // the "use this place on the map" is set
            LatLng markerLocationOnMap = pagerAdapter.getTaskLocationFragment().getMarkerLocationOnMap();
            if (markerLocationOnMap == null) {
                // no marker was ever set. Use the current location by default
                Location location = LocationServices.FusedLocationApi.getLastLocation(pagerAdapter.getTaskLocationFragment().getmGoogleApiClient());
                if (location != null) {
                    // was able to get current location
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    theTask.setLat(currentLocation.latitude);
                    theTask.setLng(currentLocation.longitude);
                } else {
                    // wasn't able to get location, use the user's address instead
                    setAddressIfNoLatLng(theUser, theTask);
                }
            } else {
                // use the location of the set marker
                theTask.setLat(markerLocationOnMap.latitude);
                theTask.setLng(markerLocationOnMap.longitude);
            }
        }

        theTask.saveInBackground(new SaveCallback() {
             @Override
             public void done(ParseException e) {
                 // go to Waiting activity
                 startActivity(new Intent(CreateTaskActivity.this, WaitingForChatActivity.class));
             }
         });

    }

    public void setAddressIfNoLatLng(ParseWSUser user, ParseTask task) {
        if (user.getLat() == null || user.getLng() == null) {
            // Just use the address since there is no lat/lng info
            task.setAddress(user.getAddress() + ", " + user.getCityStateZip());
            //task.setLat(null); // TODO - parse doesn't like null values. Come up with something to put here.
            //task.setLng(null); // TODO - parse doesn't like null values. Come up with something to put here.
        } else {
            // use the (more precise) lat/lng instead.
            task.setLat(user.getLat());
            task.setLng(user.getLng());
            //task.setAddress(null); // TODO - parse doesn't like null values. Come up with something to put here.
        }
    }
}
