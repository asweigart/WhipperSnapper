package com.al.whippersnapper.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import java.io.InputStream;

public class FillOutProfileActivity extends ActionBarActivity {
    public static final int TAKE_PHOTO_NOW_REQUEST = 1000;
    public static final int UPLOAD_PHOTO_REQUEST = 1001;

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
    byte[] photoBytes;

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
        pbCreatingProfile.setVisibility(View.VISIBLE);
        btnTakePhotoNow.setEnabled(false);
        btnUploadPhoto.setEnabled(false);
        etFullName.setEnabled(false);
        etAddress.setEnabled(false);
        etCityStateZip.setEnabled(false);
        btnCreateProfile.setText(getResources().getString(R.string.Creating_profile));
        btnCreateProfile.setEnabled(false);

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

        try {
            // We're displaying the progress bar, so this is fine.
            //long start = System.currentTimeMillis();
            profilePhotoFile.save(); // Takes an estimated 1300 ms
           // Log.e("XXXXXXXXXXXXX", String.valueOf(System.currentTimeMillis() - start));
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(FillOutProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

            // enable everything so the user can retry
            pbCreatingProfile.setVisibility(View.INVISIBLE);
            btnTakePhotoNow.setEnabled(true);
            btnUploadPhoto.setEnabled(true);
            etFullName.setEnabled(true);
            etAddress.setEnabled(true);
            etCityStateZip.setEnabled(true);
            setDoneButtonIfComplete();
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
                    pbCreatingProfile.setVisibility(View.INVISIBLE);
                    btnTakePhotoNow.setEnabled(true);
                    btnUploadPhoto.setEnabled(true);
                    etFullName.setEnabled(true);
                    etAddress.setEnabled(true);
                    etCityStateZip.setEnabled(true);
                    setDoneButtonIfComplete();
                }
            }
        });
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

        if (photo != null) {
            // depending on if the photo is wider or taller, scale it down proportionally to the ImageView
            if (photo.getWidth() > photo.getHeight()) {
                photo = Bitmap.createScaledBitmap(photo, ivProfilePhoto.getWidth(), (int)(ivProfilePhoto.getHeight() * ((float)(photo.getHeight()) / (float)(photo.getWidth()))), true);
            } else {
                photo = Bitmap.createScaledBitmap(photo, (int)(ivProfilePhoto.getWidth() * ((float)(photo.getWidth()) / (float)(photo.getHeight()))), ivProfilePhoto.getHeight(), true);
            }

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
