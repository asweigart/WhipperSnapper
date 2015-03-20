package com.al.whippersnapper.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.al.whippersnapper.R;
import com.al.whippersnapper.applications.ParseApplication;
import com.al.whippersnapper.models.ParseChatRooms;
import com.al.whippersnapper.models.ParseWSUser;
import com.al.whippersnapper.utils.Util;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.apache.http.Header;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    public static int INVALID_LOGIN_CREDENTIAL = 101; // Odd. This constant doesn't seem to exist in Parse
    public static final boolean DEBUG_USE_REAL_PHONE_NUMBER = false;
    public static String DEBUG_FAKE_PHONE_NUMBER = "12125550010";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check to see if a fake phone number should be loaded
        if (!DEBUG_USE_REAL_PHONE_NUMBER) {
            File fakePhoneFile = new File(Environment.getExternalStorageDirectory() + "/_fakePhoneNumber.txt");
            try {
                FileInputStream streamIn = new FileInputStream(fakePhoneFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(streamIn));
                DEBUG_FAKE_PHONE_NUMBER = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                finish();
            }
        }

        if (!isNetworkAvailable()) {
            // there's no internet access
            // hide the progress bar and show the "no internet" image.
            ProgressBar pbMainProgressBar = (ProgressBar) findViewById(R.id.pbMainProgressBar);
            pbMainProgressBar.setVisibility(View.GONE);
            ImageView ivNoInterent = (ImageView) findViewById(R.id.ivNoInternet);
            ivNoInterent.setVisibility(View.VISIBLE);
            TextView tvNoInternetLabel = (TextView) findViewById(R.id.tvNoInternetLabel);
            tvNoInternetLabel.setVisibility(View.VISIBLE);
            return;
        }

        boolean creatingProfile = false;

        ParseWSUser thisUser = null;
        try {
            thisUser.logIn(getThisDevicePhoneNumber(), "password");
            thisUser = (ParseWSUser) ParseWSUser.getCurrentUser();
        } catch (ParseException e) {
            if (e.getCode() == INVALID_LOGIN_CREDENTIAL) {
                // bad login means the account does not exist, so launch the profile creation activity
                Intent i = new Intent(MainActivity.this, UserTypePickerActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // clear back stack
                startActivity(i);
                creatingProfile = true;
            } else {
                // Something actually went wrong.
                // TODO - do airplane mode test here
                e.printStackTrace();
            }
        }


        final ParseWSUser finalUser = thisUser; // used so we can reference this in the callback

        if (!creatingProfile) {
            // logged in, so go to the appropriate senior/volunteer activity
            // First check if the user is in a chatroom
            ParseQuery<ParseChatRooms> q = ParseQuery.getQuery("ChatRooms");
            if (thisUser.getIsSenior()) {
                q.whereEqualTo("SeniorUsername", thisUser.getUsername());
            } else {
                q.whereEqualTo("VolunteerUsername", thisUser.getUsername());
            }
            q.findInBackground(new FindCallback<ParseChatRooms>() {
                @Override
                public void done(List<ParseChatRooms> parseChatRoomses, ParseException e) {
                    byte[] thisUserPhotoBytes = null;
                    byte[] otherUserPhotoBytes = null;
                    Intent i = null;
                    boolean inChatRoom = parseChatRoomses.size() > 0; // TODO - this should only ever be 0 or 1

                    // get profile photo info of both users if chat activity will be launched
                    if (inChatRoom) {
                        try {
                            // get the user object of the other user
                            ParseQuery<ParseWSUser> q2 = ParseQuery.getQuery("_User");
                            if (finalUser.getIsSenior()) {
                                q2.whereEqualTo("username", parseChatRoomses.get(0).getVolunteerUsername());
                            } else {
                                q2.whereEqualTo("username", parseChatRoomses.get(0).getSeniorUsername());
                            }
                            List<ParseWSUser> results = q2.find();

                            thisUserPhotoBytes = finalUser.getPhoto().getData();
                            otherUserPhotoBytes = results.get(0).getPhoto().getData();
                        } catch (ParseException e2) {
                            e2.printStackTrace();
                        }
                    }

                    if (finalUser.getIsSenior()) {
                        // handle senior users
                        if (inChatRoom) {
                            // go to the Chat Room activity
                            i = new Intent(MainActivity.this, ChatActivity.class);
                            i.putExtra("otherUsername", parseChatRoomses.get(0).getVolunteerUsername());
                            i.putExtra("otherUserFullName", parseChatRoomses.get(0).getVolunteerFullName());
                            i.putExtra("fromShowTaskDetailsActivity", false); // don't add the task summary chat message
                            i.putExtra("thisUserPhoto", thisUserPhotoBytes);
                            i.putExtra("otherUserPhoto", otherUserPhotoBytes);
                        } else if (finalUser.getTaskType() == null || finalUser.getTaskType().equals("")) { // a blank task type is a "deleted" (non-existent, unset) task.
                            // go to the Senior Home activity
                            i = new Intent(MainActivity.this, SeniorHomeActivity.class);
                        } else {
                            // go to the Waiting activity
                            i = new Intent(MainActivity.this, WaitingForChatActivity.class);
                            i.putExtra("taskType", finalUser.getTaskType());
                            i.putExtra("taskDetails", finalUser.getTaskDetails());
                            i.putExtra("postedOn", finalUser.getTaskPostedOn());
                        }
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // clear back stack
                        startActivity(i);
                    } else {

                        // handle volunteer users
                        if (inChatRoom) {
                            // go to the Chat Room activity
                            i = new Intent(MainActivity.this, ChatActivity.class);
                            i.putExtra("otherUsername", parseChatRoomses.get(0).getSeniorUsername());
                            i.putExtra("otherUserFullName", Util.getAnonymizedName(parseChatRoomses.get(0).getSeniorFullName()));
                            i.putExtra("fromShowTaskDetailsActivity", false); // don't add the task summary chat message
                            i.putExtra("thisUserPhoto", thisUserPhotoBytes);
                            i.putExtra("otherUserPhoto", otherUserPhotoBytes);
                        } else {
                            // go to the Find Task activity
                            i = new Intent(MainActivity.this, FindTaskActivity.class);
                        }
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // clear back stack
                        startActivity(i);
                    }
                }
            });


        }


    }


    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    */

    // TODO used for debugging, and apparently I have to copy/paste this function into every
    // Activity class I want to use it from since getApplicationContext() can't be called from
    // a static method.
    public String getThisDevicePhoneNumber() {
        if (MainActivity.DEBUG_USE_REAL_PHONE_NUMBER) {
            TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            return tMgr.getLine1Number();
        } else {
            return MainActivity.DEBUG_FAKE_PHONE_NUMBER;
        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
