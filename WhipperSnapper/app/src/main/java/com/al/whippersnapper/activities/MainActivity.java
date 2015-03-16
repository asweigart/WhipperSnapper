package com.al.whippersnapper.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.al.whippersnapper.R;
import com.al.whippersnapper.applications.ParseApplication;
import com.al.whippersnapper.models.ParseChatRooms;
import com.al.whippersnapper.models.ParseWSUser;
import com.al.whippersnapper.utils.Util;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class MainActivity extends ActionBarActivity {
    public static int INVALID_LOGIN_CREDENTIAL = 101; // Odd. This constant doesn't seem to exist in Parse
    public static final boolean DEBUG_USE_REAL_PHONE_NUMBER = false;
    public static final String DEBUG_FAKE_PHONE_NUMBER = "12125550007";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                    Intent i = null;
                    boolean inChatRoom = parseChatRoomses.size() > 0; // TODO - this should only ever be 0 or 1

                    if (finalUser.getIsSenior()) {

                        // handle senior users
                        if (inChatRoom) {
                            // go to the Chat Room activity
                            i = new Intent(MainActivity.this, ChatActivity.class);
                            i.putExtra("otherUsername", parseChatRoomses.get(0).getVolunteerUsername());
                            i.putExtra("otherUserFullName", parseChatRoomses.get(0).getVolunteerFullName());
                        } else if (finalUser.getTaskType() == null || finalUser.getTaskType().equals("")) {
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
}
