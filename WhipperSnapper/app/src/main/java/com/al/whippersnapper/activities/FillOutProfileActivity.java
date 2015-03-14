package com.al.whippersnapper.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.al.whippersnapper.R;
import com.al.whippersnapper.models.ParseWSUser;
import com.al.whippersnapper.utils.Util;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class FillOutProfileActivity extends ActionBarActivity {
    public static final int TAKE_PHOTO_NOW_REQUEST = 1000;
    public static final int UPLOAD_PHOTO_REQUEST = 1001;

    public static final int PROFILE_PHOTO_MAX_WIDTH = 300;
    public static final int PROFILE_PHOTO_MAX_HEIGHT = 300;

    private Button btnTakePhotoNow;
    private Button btnUploadPhoto;
    private ImageView ivProfilePhoto;
    private EditText etFullName;
    private EditText etAddress;
    private EditText etCityStateZip;
    private Button btnCreateProfile;
    private TextView tvForPrivacyLabel;
    private ProgressBar pbCreatingProfile;

    private String userPhoneNumber;
    private byte[] photoBytes;

    private boolean isPhotoSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_out_profile);

        btnTakePhotoNow = (Button) findViewById(R.id.btnTakePhotoNow);
        btnUploadPhoto = (Button) findViewById(R.id.btnUploadPhoto);
        btnCreateProfile = (Button) findViewById(R.id.btnCreateProfile);
        ivProfilePhoto = (ImageView) findViewById(R.id.ivProfilePhoto);
        etFullName = (EditText) findViewById(R.id.etFullName);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etCityStateZip = (EditText) findViewById(R.id.etCityStateZip);
        btnCreateProfile = (Button) findViewById(R.id.btnCreateProfile);
        tvForPrivacyLabel = (TextView) findViewById(R.id.tvForPrivacyLabel);
        pbCreatingProfile = (ProgressBar) findViewById(R.id.pbCreatingProfile);

        pbCreatingProfile.setVisibility(View.INVISIBLE);
        btnTakePhotoNow.setEnabled(true);
        btnUploadPhoto.setEnabled(true);
        etFullName.setEnabled(true);
        etAddress.setEnabled(true);
        etCityStateZip.setEnabled(true);

        isPhotoSet = false;
        setDoneButtonIfComplete();

        // obtain the phone number from the device
        TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        userPhoneNumber = tMgr.getLine1Number();

        // add TextWatcher to Full Name field
        etFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // s.toString()
                String fullName = etFullName.getText().toString();
                tvForPrivacyLabel.setText(getResources().getString(R.string.For_privacy_your_name_will_appear_as) + Util.getAnonymizedName(fullName));
                setDoneButtonIfComplete();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // add TextWatcher to Address field
        etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // s.toString()
                setDoneButtonIfComplete();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // add TextWatcher to City, State, Zip field
        etCityStateZip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // s.toString()
                setDoneButtonIfComplete();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fill_out_profile, menu);
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

    public void onTakePhotoNowClick(View v) {
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, TAKE_PHOTO_NOW_REQUEST);
    }

    public void onUploadPhotoClick(View v) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, UPLOAD_PHOTO_REQUEST);
    }

    public void onCreateProfileClick(View v) {
        // disable everything so the form can't be modified while creating the profile
        disableTheForm();
        btnCreateProfile.setText(getResources().getString(R.string.Creating_profile));

        // TODO: Run all this in a background thread

        // create a user account on Parse
        ParseWSUser thisUser = new ParseWSUser();
        thisUser.setUsername(userPhoneNumber);
        thisUser.setPassword("password"); // Super secret 1337 password, yo.
        thisUser.setPhone(userPhoneNumber);
        thisUser.setIsSenior(getIntent().getBooleanExtra("isSenior", true));
        thisUser.setFullName(etFullName.getText().toString());
        thisUser.setAddress(etAddress.getText().toString());
        thisUser.setCityStateZip(etCityStateZip.getText().toString());
        ParseFile profilePhotoFile = new ParseFile("profilePhoto.jpg", photoBytes);
        thisUser.setPhoto(profilePhotoFile);

        // geocode the address, save the lat & lng
        Address homeLatLng = new Address(Locale.ENGLISH); // 'murica!!
        try {
            Geocoder geocoder = new Geocoder(this);
            homeLatLng = geocoder.getFromLocationName(etAddress.getText().toString() + ", " + etCityStateZip.getText().toString(), 1).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        thisUser.setLat(homeLatLng.getLatitude()); // it's okay if lat & lng are null.
        thisUser.setLng(homeLatLng.getLongitude());

        try {
            // We're displaying the progress bar, so this is fine.
            //long start = System.currentTimeMillis();
            profilePhotoFile.save(); // Takes an estimated 1300 ms
           // Log.e("XXXXXXXXXXXXX", String.valueOf(System.currentTimeMillis() - start));
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(FillOutProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

            // enable everything so the user can retry
            enableTheForm();
        }

        thisUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    if (getIntent().getBooleanExtra("isSenior", true)) {
                        // This is a senior, start the Senior Home activity
                        Intent i = new Intent(FillOutProfileActivity.this, SeniorHomeActivity.class);
                        startActivity(i);
                    } else {
                        // This is a volunteer, start the Find Task activity
                        Intent i = new Intent(FillOutProfileActivity.this, FindTaskActivity.class);
                        startActivity(i);
                    }
                    FillOutProfileActivity.this.finish();
                } else {
                    e.printStackTrace();
                    Toast.makeText(FillOutProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                    // enable everything so the user can retry
                    enableTheForm();
                }
            }
        });
    }

    public void enableTheForm() {
        // enable all the fields and buttons on this activity, hide the progress bar
        pbCreatingProfile.setVisibility(View.INVISIBLE);
        btnTakePhotoNow.setEnabled(true);
        btnUploadPhoto.setEnabled(true);
        etFullName.setEnabled(true);
        etAddress.setEnabled(true);
        etCityStateZip.setEnabled(true);
        setDoneButtonIfComplete();
    }

    public void disableTheForm() {
        // disable all the fields and buttons on this activity, show the progress bar
        pbCreatingProfile.setVisibility(View.VISIBLE);
        btnTakePhotoNow.setEnabled(false);
        btnUploadPhoto.setEnabled(false);
        etFullName.setEnabled(false);
        etAddress.setEnabled(false);
        etCityStateZip.setEnabled(false);
        btnCreateProfile.setEnabled(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap photo = null;
        if (requestCode == TAKE_PHOTO_NOW_REQUEST && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
        } else if (requestCode == UPLOAD_PHOTO_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                photo = BitmapFactory.decodeStream(imageStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        //.e("XXXXXXXXXX", "Original size: " + photo.getWidth() + " " + photo.getHeight());
        if (photo != null) {

            // Scale the photo
            Matrix m = new Matrix();
            m.setRectToRect(new RectF(0, 0, photo.getWidth(), photo.getHeight()), new RectF(0, 0, PROFILE_PHOTO_MAX_WIDTH, PROFILE_PHOTO_MAX_HEIGHT), Matrix.ScaleToFit.CENTER);
            photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), m, true);
            //Log.e("XXXXXXXXXX", "Scaled size: " + photo.getWidth() + " " + photo.getHeight());

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            photoBytes = stream.toByteArray();
            //Log.e("XXXXXXXXXXX", String.valueOf(photoBytes.length)); // confirmed that called createScaledBitmap signifcantly reduces the size.
            ivProfilePhoto.setImageBitmap(BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length));

            isPhotoSet = true; // photo is marked as set (for the validator)
            setDoneButtonIfComplete();
        }
    }

    public void setDoneButtonIfComplete() {
        // enables the submit button and updates its text if everything is complete.
        if (etFullName.getText().toString().equals("")) {
            btnCreateProfile.setText(getResources().getString(R.string.Your_name_still_needs_to_be_entered));
            btnCreateProfile.setEnabled(false);
        } else if (etAddress.getText().toString().equals("")) {
            btnCreateProfile.setText(getResources().getString(R.string.Your_address_still_needs_to_be_entered));
            btnCreateProfile.setEnabled(false);
        } else if (etCityStateZip.getText().toString().equals("")) {
            btnCreateProfile.setText(getResources().getString(R.string.Your_address_still_needs_to_be_entered));
            btnCreateProfile.setEnabled(false);
        } else if (!isPhotoSet) {
            btnCreateProfile.setText(getResources().getString(R.string.Your_photo_is_still_needed));
            btnCreateProfile.setEnabled(false);
        } else {
            btnCreateProfile.setText(getResources().getString(R.string.All_done));
            btnCreateProfile.setEnabled(true);
        }
    }
}
